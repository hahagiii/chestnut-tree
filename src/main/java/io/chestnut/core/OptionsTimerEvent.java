package io.chestnut.core;


public class OptionsTimerEvent {
	
	public static enum TimerEventEnum {
		HOUR, Minute, Second
	}
	
	public static enum TimerEventCustomizeEnum {
		Millisecond, HourMinuteSecond, MinuteSecond;
	}
	
	
	public OptionsTimerEvent(TimerEventEnum timerEventType) {
		this.timerEventEnum = timerEventType;
	}
	
	public OptionsTimerEvent(TimerEventCustomizeEnum timerEventType, String timeData,int timerId) {
		this.timerEventCustomizeEnum = timerEventType;
		this.timeData = timeData;
		this.timerId = timerId;
	}

	public TimerEventEnum timerEventEnum;
	public TimerEventCustomizeEnum timerEventCustomizeEnum;
	public String timeData;
	public int timerId;

	
	
	
}
