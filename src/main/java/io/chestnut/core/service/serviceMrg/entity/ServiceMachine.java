package io.chestnut.core.service.serviceMrg.entity;

import io.chestnut.core.orm.Column;

public class ServiceMachine{
	
	public ServiceMachine() {
		
	}
	
	public ServiceMachine(String ipddr, int port) {
		this.ipddr = ipddr;
		this.port = port;
	}
	@Column()
	public String ipddr;
	@Column()
	public int port;
	@Column()
	public int state;
}
