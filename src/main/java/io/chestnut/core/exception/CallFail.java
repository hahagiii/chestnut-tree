package io.chestnut.core.exception;

public class CallFail  extends Exception{
	private int errorCode;
	private short messageId;
	private static final long serialVersionUID = 1L;
	
	public CallFail(int errorCode, short messageId, String describe) {
		super(describe);
		this.errorCode = errorCode;
		this.messageId = messageId;
	}

	public CallFail(String describe) {
		super(describe);
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public short getMessageId() {
		return messageId;
	}

}
