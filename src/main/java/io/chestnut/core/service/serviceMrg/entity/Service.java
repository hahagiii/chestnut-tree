package io.chestnut.core.service.serviceMrg.entity;


import java.util.List;

import io.chestnut.core.orm.Column;
import io.chestnut.core.orm.Entity;
import io.chestnut.core.orm.Id;
import io.chestnut.core.service.serviceMrg.Define;

@Entity(name = "service")
public class Service{
	
	@Id()
	public String serviceName;
	
	@Column()
	public List<ServiceMachine> serviceMachineList;
	
	

	public List<ServiceMachine> getServiceMachineList() {
		return serviceMachineList;
	}

	public void setServiceMachineList(List<ServiceMachine> serviceMachineList) {
		this.serviceMachineList = serviceMachineList;
	}

	public ServiceMachine randServiceMachine() {
		ServiceMachine returnServiceMachine = null;
		for (ServiceMachine serviceMachine : serviceMachineList) {
			if(serviceMachine.state != Define.ServiceMachineStateOnline) {
				continue;
			}
			returnServiceMachine = serviceMachine;
		}
		return returnServiceMachine;
		
	}







}
