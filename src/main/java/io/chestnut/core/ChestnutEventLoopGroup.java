package io.chestnut.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.chestnut.core.systemMessage.SystemMsgTimerSecond;

public class ChestnutEventLoopGroup {
	private Timer timerEventTimer;
	private String groupName;
	Map<String, ChestnutEventLoopThread> chestnutEventLoopThreadMap = new HashMap<String, ChestnutEventLoopThread>();
	

	public ChestnutEventLoopGroup(ThreadGroupOptions options) {
		for (int i = 0; i < options.getThreadNum(); i++) {
			ChestnutEventLoopThread chestnutEventLoopThread = new ChestnutEventLoopThread();
			chestnutEventLoopThread.setName(options.getThreadGroupName() + "_" + i);
			chestnutEventLoopThreadMap.put(chestnutEventLoopThread.getName(), chestnutEventLoopThread);
			chestnutEventLoopThread.start();
		}
	}

	public ChestnutEventLoopThread getLowestLoadThread() {
		ArrayList<ChestnutEventLoopThread> list = new ArrayList<>(chestnutEventLoopThreadMap.size());
		list.addAll(chestnutEventLoopThreadMap.values());
		return list.get(0);
		
	}
	
	public Timer getTimerEventTimer() {
		return timerEventTimer;
	}

	public void setTimerEventTimer(Timer timerEventTimer) {
		this.timerEventTimer = timerEventTimer;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void castAll(SystemMsgTimerSecond systemMsgTimerSecond) {
		for (ChestnutEventLoopThread chestnutEventLoopThread : chestnutEventLoopThreadMap.values()) {
			WrapMessageToAll wrapMessage = new WrapMessageToAll(systemMsgTimerSecond);
			chestnutEventLoopThread.addMessageToHandle(wrapMessage);
		}
	}

	public void addTimerEvent(TimerTask timerTask, long delay, long period) {
		timerEventTimer.scheduleAtFixedRate(timerTask, delay, period);
	}

}
