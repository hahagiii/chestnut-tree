package io.chestnut.core;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.chestnut.core.exception.CallFail;
import io.chestnut.core.exception.HandleInternalError;
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
	
	public MHandle[] callHandle = new MHandle[Short.MAX_VALUE];
	
	@SuppressWarnings("unchecked")
	public ArrayList<MHandle>[] castHandleList = new ArrayList[Short.MAX_VALUE];
	public Map<String, ChestnutComponent<?>> chestnutComponentMap = new HashMap<>();

	
	public void start() {
		
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
			callFail.printStackTrace();
			throw callFail;
		}
		final MethodHandle methodHandle = mHandle.callMh;
		try {
			return (T) methodHandle.invoke(mHandle.handleCallComponent, request);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new CallFail("invoke error");
		}
	}
	
	public void execute(Message request) throws CallFail {
		MHandle mHandle = callHandle[request.id()];
		if(mHandle == null) {
			CallFail callFail = new CallFail("mHandle is null " + request.id());
			callFail.printStackTrace();
			throw callFail;
		}
		final MethodHandle methodHandle = mHandle.callMh;
		try {
			methodHandle.invoke(mHandle.handleCallComponent, request);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new CallFail("invoke error");
		}
	}

	public void cast(short messageId) throws Exception {
		cast(InternalMsgFactory.getMessage(messageId));
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
	public void handleCast(Message request) throws MessageNotHandle,HandleInternalError {
		ArrayList<MHandle> mHandleList = castHandleList[request.id()];
		if(mHandleList == null || mHandleList.isEmpty()) {
			throw new MessageNotHandle("requestId: " + request.id());
		}
		for (MHandle mHandle : mHandleList) {
			try {
				final MethodHandle methodHandle = mHandle.callMh;
				methodHandle.invoke(mHandle.handleCallComponent, request);
			} catch (Throwable e) {
				e.printStackTrace();
				throw new HandleInternalError();
			}
		}
		
	}
}
