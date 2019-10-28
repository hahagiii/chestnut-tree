package io.chestnut.core.message.systemMsg;

import io.chestnut.core.InternalMessage;

public class SystemMsgTimerSecond extends InternalMessage{
	

	@Override
	public short id() {
		return SystemMessageDefine.SystemMsgTimerSecond;
	}
}
