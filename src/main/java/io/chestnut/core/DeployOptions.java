package io.chestnut.core;

import java.util.ArrayList;

import io.chestnut.core.OptionsTimerEvent.TimerEventCustomizeEnum;
import io.chestnut.core.OptionsTimerEvent.TimerEventEnum;

public class DeployOptions {
	
	
	private String threadGroupName;
	private ArrayList<OptionsTimerEvent> timerEvent;
	
	public String getThreadGroupName() {
		return threadGroupName;
	}

	public ArrayList<OptionsTimerEvent> timerEvent() {
		return timerEvent;
	}
	
	public DeployOptions setThreadGroupName(String threadGroupName) {
		this.threadGroupName = threadGroupName;
		return this;
	}

	public DeployOptions addTimerEvent(TimerEventEnum timerEventType,String timeData,int timerId) {
		if(timerEvent ==null) {
			timerEvent = new ArrayList<>();
		}
		timerEvent.add(new OptionsTimerEvent(timerEventType));
		return this;
	}
	
	public DeployOptions addTimerEvent(TimerEventCustomizeEnum timerEventType,String timeData,int timerId) {
		if(timerEvent ==null) {
			timerEvent = new ArrayList<>();
		}
		timerEvent.add(new OptionsTimerEvent(timerEventType,timeData, timerId));
		return this;
	}

}
