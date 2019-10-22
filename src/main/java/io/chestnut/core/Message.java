package io.chestnut.core;

public interface Message {
	public short id();
	public Chestnut messageDest();
	public void setMessageDest(Chestnut chestnut);
}
