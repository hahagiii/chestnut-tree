package io.chestnut.core;

import java.util.ArrayList;

import io.chestnut.core.OptionsTimerEvent.TimerEventCustomizeEnum;
import io.chestnut.core.OptionsTimerEvent.TimerEventEnum;

public class ThreadGroupOptions {
	private String threadGroupName;
	private int threadNum;
	private ArrayList<OptionsTimerEvent> timerEvent;
	
	public String getThreadGroupName() {
		return threadGroupName;
	}

	public int getThreadNum() {
		return threadNum;
	}
	
	public ArrayList<OptionsTimerEvent> timerEvent() {
		return timerEvent;
	}
	
	public ThreadGroupOptions setThreadGroupInfo(String threadGroupName,int threadNum) {
		this.threadGroupName = threadGroupName;
		this.threadNum = threadNum;
		return this;
	}

	public ThreadGroupOptions addTimerEvent(TimerEventEnum timerEventType,String timeData,int timerId) {
		if(timerEvent ==null) {
			timerEvent = new ArrayList<>();
		}
		timerEvent.add(new OptionsTimerEvent(timerEventType));
		return this;
	}
	
	public ThreadGroupOptions addTimerEvent(TimerEventCustomizeEnum timerEventType,String timeData,int timerId) {
		if(timerEvent ==null) {
			timerEvent = new ArrayList<>();
		}
		timerEvent.add(new OptionsTimerEvent(timerEventType,timeData, timerId));
		return this;
	}
}
