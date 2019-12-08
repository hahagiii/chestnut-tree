package io.chestnut.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.chestnut.core.util.DebugUtil;

public class ChestnutEventLoopThread extends Thread {
	private static final Logger logger = LoggerFactory.getLogger(ChestnutEventLoopThread.class);
	public volatile byte runState = 0;
	protected LinkedBlockingQueue<Message> msgQueue = new LinkedBlockingQueue<Message>();
	public Map<String, Chestnut> threadChestnutMap = new ConcurrentHashMap<String, Chestnut>();

	@Override
	public void run() {
		while (runState == 0) {
			Message message;
				try {
					message = msgQueue.take();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					continue;
				}
				
				if(message instanceof WrapMessageExecute) {
					WrapMessageExecute wrapMessageExecute = (WrapMessageExecute) message;
					wrapMessageExecute.handler.handle(wrapMessageExecute.internalMessage);
				}else if(message instanceof WrapMessageToAll) {
					WrapMessageToAll wrapMessage = (WrapMessageToAll) message;
					for (Chestnut chestnut : threadChestnutMap.values()) {
						if(!chestnut.isCast(wrapMessage.message)) {
							continue;
						}
						try {
							chestnut.handleCast(wrapMessage.message);
						} catch (Throwable e) {
							logger.error(DebugUtil.stackPath(e));
						}
					}
				}else {
					try {
						((Message)(message)).messageDest().handleCast(message);
					} catch (Throwable e) {
						logger.error(DebugUtil.stackPath(e));
					}
				}
				

			
		}
	}
	
	public void addMessageToHandle(Message message) {
		msgQueue.offer(message);
	}

	public void removeChestnut(String id) {
		threadChestnutMap.remove(id);
	}
	
	public void chestnutBindThread(Chestnut chestnut) {
		threadChestnutMap.put(chestnut.getId(),chestnut);
	}

	public void execute(Handler handler, InternalMessage internalMessage) {
		msgQueue.offer(new WrapMessageExecute(handler, internalMessage));
	}
}
