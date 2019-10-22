package io.chestnut.core.systemMessage;

import io.chestnut.core.InternalMessage;

public class SystemMsgTimerSecond extends InternalMessage{
	

	@Override
	public short id() {
		return SystemMessageDefine.SystemMsgTimerSecond;
	}
}
