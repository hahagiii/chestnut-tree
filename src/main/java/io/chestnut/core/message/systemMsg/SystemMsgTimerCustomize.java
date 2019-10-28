package io.chestnut.core.message.systemMsg;

import io.chestnut.core.InternalMessage;

public class SystemMsgTimerCustomize extends InternalMessage{
	public int timerId;
	
	public SystemMsgTimerCustomize() {
	}

	@Override
	public short id() {
		return SystemMessageDefine.SystemMsgTimerCustomize;
	}

}
