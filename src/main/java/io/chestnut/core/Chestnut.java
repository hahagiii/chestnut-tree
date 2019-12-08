package io.chestnut.core;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.chestnut.core.exception.CallFail;
import io.chestnut.core.exception.MessageNotHandle;

class MHandle{
	public MHandle(ChestnutComponent<?> handleCallComponent, MethodHandle mh) {
		this.handleCallComponent = handleCallComponent;
		this.callMh  = mh;
		
	}
	public ChestnutComponent<?> handleCallComponent;
	public MethodHandle callMh;
}

public abstract class Chestnut {
	
	public abstract String getId();
	protected ChestnutEventLoopThread chestnutEventLoopThread;
	
	private MHandle[] callHandle = new MHandle[Short.MAX_VALUE];
	private ChestnutTree chestnutTree;
	@SuppressWarnings("unchecked")
	private ArrayList<MHandle>[] castHandleList = new ArrayList[Short.MAX_VALUE];
	private Map<String, ChestnutComponent<?>> chestnutComponentMap = new HashMap<>();

	
	public void start() {
		
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ChestnutComponent<?>> T getComponent(String componentId) {
		ChestnutComponent<?> chestnutComponent = chestnutComponentMap.get(componentId);
		if(chestnutComponent == null) {
			return null;
		}
		return (T) chestnutComponent;
	}
	public void addComponent(ChestnutComponent<?> chestnutComponent) {
		if(chestnutComponent.componentId == null) {
			chestnutComponent.componentId = chestnutComponent.getClass().getSimpleName();
		}
		chestnutComponentMap.put(chestnutComponent.componentId, chestnutComponent);
		chestnutComponent.setComponentOwner(this);
	
		for (Method m : chestnutComponent.getClass().getDeclaredMethods()) {
			for (Annotation an : m.getDeclaredAnnotations()) {
				if (an instanceof HandleCast) {
					short eventId = ((HandleCast) an).id();
					if(castHandleList[eventId] == null) {
						castHandleList[eventId] = new ArrayList<>(8);
					}
					MethodType mt = MethodType.methodType(void.class,Message.class);
					MethodHandle mh = null;
					try {
						mh = MethodHandles.lookup().findVirtual(chestnutComponent.getClass(),m.getName(),mt);
						m.setAccessible(true);
						castHandleList[eventId].add(new MHandle(chestnutComponent, mh));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if(an instanceof HandleCall) {
					short eventId = ((HandleCall) an).id();
					if(callHandle[eventId] != null) {
						return;
					}
					MethodType mt = MethodType.methodType(Message.class,Message.class);
					MethodHandle mh = null;
					try {
						mh = MethodHandles.lookup().findVirtual(chestnutComponent.getClass(),m.getName(),mt);
						callHandle[eventId] = new MHandle(chestnutComponent,mh);
						m.setAccessible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
		}
		
	}

	public ChestnutEventLoopThread chestnutEventLoopThread() {
		return chestnutEventLoopThread;
	}

	public void setChestnutEventLoopThread(ChestnutEventLoopThread chestnutEventLoopThread) {
		this.chestnutEventLoopThread = chestnutEventLoopThread;
	}
	
	public <T extends Message> T call(Message request) throws CallFail {
		MHandle mHandle = callHandle[request.id()];
		if(mHandle == null) {
			CallFail callFail = new CallFail("mHandle is null " + request.id());
			throw callFail;
		}
		final MethodHandle methodHandle = mHandle.callMh;
		try {
			return (T) methodHandle.invoke(mHandle.handleCallComponent, request);
		}catch (Throwable e) {
			CallFail callFail = null;
			if(e instanceof CallFail){
				callFail = (CallFail) e;
			}else{
				callFail = new CallFail("invoke error " + e.getMessage() + " request is" + request.id());
			}
			throw callFail;
		}	
	}
	
	public void cast(short messageId) throws Exception {
		cast(chestnutTree.getMessage(messageId));
	}
	public void cast(Message request) throws Exception {
			if(request == null) {
				throw new Exception("request is null");
			}
			request.setMessageDest(this);
			chestnutEventLoopThread.addMessageToHandle(request);
			return;
	}
	
	public boolean isCast(Message request) {
		ArrayList<MHandle> mHandleList = castHandleList[request.id()];
		if(mHandleList == null ||mHandleList.isEmpty()) {
			return false;
		}
		return true;

	}
	public void handleCast(Message request) throws Throwable {
		ArrayList<MHandle> mHandleList = castHandleList[request.id()];
		if(mHandleList == null || mHandleList.isEmpty()) {
			throw new MessageNotHandle("requestId: " + request.id());
		}
		for (MHandle mHandle : mHandleList) {
			try {
				final MethodHandle methodHandle = mHandle.callMh;
				methodHandle.invoke(mHandle.handleCallComponent, request);
			} catch (WrongMethodTypeException e){
				throw e;
			}catch (ClassCastException e){
				throw e;
			} catch (Throwable e) {
				mHandle.handleCallComponent.castException(e,request);
			}
		}
		
	}

	public ChestnutTree chestnutTree() {
		return chestnutTree;
	}

	public void setChestnutTree(ChestnutTree chestnutTree) {
		this.chestnutTree = chestnutTree;
	}
}
