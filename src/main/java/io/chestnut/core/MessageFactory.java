package io.chestnut.core;

import java.util.Set;

import io.chestnut.core.util.ClassScanner;

public class MessageFactory<T extends Message> {
	private static final Class<?>[] EMPTY_PARAMS = new Class[0];

	@SuppressWarnings("unchecked")
	public final Class<? extends T>[] protocolClassMap = (Class<? extends T>[]) new Class<?>[Short.MAX_VALUE];

	
	public MessageFactory(short messageIdList[], Class<? extends T>[] classList) {
		int length = messageIdList.length;
		for (int i = 0; i < length; i++) {
			short messageId = messageIdList[i];
			Class<? extends T> messageClass = classList[i];
			add(messageId, messageClass);
		}
	}
	
	public MessageFactory(String path) {
		add(path);
		
	}

	public MessageFactory() {
	}
	
	@SuppressWarnings("unchecked")
	public void add(String path) {
		Set<Class<?>> classSet = ClassScanner.getClasses(path);
		for (Class<?> clazz : classSet) {
			MsgAnnotate protocol = clazz.getAnnotation(MsgAnnotate.class);
			if(protocol != null) {
				add(protocol.id(), (Class<? extends T>) clazz);
			}
		}
	}

	public final void add(short messageId, Class<? extends T> messageClass) {
		if (messageClass == null) {
			throw new NullPointerException();
		}
		try {
			messageClass.getConstructor(EMPTY_PARAMS);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("The specified class doesn't have a public default constructor.");
		}

		if (protocolClassMap[messageId] != null) {
			throw new IllegalArgumentException("The messageClasses has already the commandId:[" + messageId + "] mapping class @ " + protocolClassMap[messageId]);
		}

		for (Class<?> class1 : protocolClassMap) {
			if (class1 == messageClass) {
				throw new IllegalArgumentException("The messageClasses has already the commandId mapping class @ " + messageClass);
			}
		}
		if (!Message.class.isAssignableFrom(messageClass)) {
			throw new IllegalArgumentException("Unregisterable type: " + messageClass);
		}
		protocolClassMap[messageId] = messageClass;
	}
	
	public T get(short id) {
		Class<? extends T> messageClass = protocolClassMap[id];
		try {
			if (messageClass == null) {
				return null;
			}
			T protocol = messageClass.newInstance();
			return protocol;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
