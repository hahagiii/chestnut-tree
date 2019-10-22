package io.chestnut.core.systemMessage;

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
