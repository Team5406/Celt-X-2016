package ca.team5406.util;

public interface Loopable {
	  public void runControlLoop();
	  public default void sendSmartdashInfo(){}
	}