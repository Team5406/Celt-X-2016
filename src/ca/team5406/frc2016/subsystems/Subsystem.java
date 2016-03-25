package ca.team5406.frc2016.subsystems;

import java.util.ArrayList;

public abstract class Subsystem {
	
	public static ArrayList<Subsystem> registeredSubsystems = new ArrayList<Subsystem>();

	private String name;
	private double randomMonitor;
	
	public Subsystem(String name){
		randomMonitor = -1.0;
	    this.name = name;
	    
	    registeredSubsystems.add(this);
	}
	
	public double getRandomMonitor(){
		return randomMonitor;
	}
	
	public void run(){
		randomMonitor = Math.random();
		runControlLoop();
	}
	
	public abstract void runControlLoop();
	
	public String getName(){
		return name;
	}

	public abstract void sendSmartdashInfo();
	
}
