package io.chestnut.core.exception;

public class CallFail  extends Exception{
	private int errorCode;
	private short messageId;
	private static final long serialVersionUID = 1L;
	
	public CallFail(String arg0) {
		super(arg0);
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
	public void setMessageId(short messageId) {
		this.messageId = messageId;
	}

}
