package io.chestnut.core.message.systemMsg;

import io.chestnut.core.InternalMessage;

public class SystemMsgTimerMinute extends InternalMessage{
	

	@Override
	public short id() {
		return  SystemMessageDefine.SystemMsgTimerMinute;
	}
}
