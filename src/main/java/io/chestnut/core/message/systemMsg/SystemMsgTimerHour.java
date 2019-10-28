package io.chestnut.core.message.systemMsg;

import io.chestnut.core.InternalMessage;

public class SystemMsgTimerHour extends InternalMessage{
	
	public SystemMsgTimerHour() {
	}

	@Override
	public short id() {
		return SystemMessageDefine.SystemMsgTimerHour;
	}
}
