package io.chestnut.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DebugUtil {
	public static StringBuilder printStack() {
		Throwable t = new Throwable();
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement ste : t.getStackTrace()) {
			sb.append(ste + "\n");
		}
		return sb;
	}


	
	public static String printStackFirstLine(Throwable e) {
		StringWriter message = new StringWriter();
		PrintWriter writer = new PrintWriter(message);
		e.printStackTrace(writer);
		String messageString = message.toString();
		int index = messageString.indexOf("\n");
		if(index > 0)
			return messageString.substring(0, index-1);
		return message.toString();
	}
	
	public static StringBuilder printStack(Throwable t) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement ste : t.getStackTrace()) {
			sb.append(ste + "\n");
		}
		return sb;
	}

	public static String stackPath(Throwable e) {
		StringWriter message = new StringWriter();
		PrintWriter writer = new PrintWriter(message);
		e.printStackTrace(writer);
		return message.toString();
	}

}
