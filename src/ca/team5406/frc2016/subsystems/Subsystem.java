package ca.team5406.frc2016.subsystems;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Subsystem {
	
	public static ArrayList<Subsystem> registeredSubsystems = new ArrayList<Subsystem>();
	public static HashMap<String, Subsystem> subsystems = new HashMap<String, Subsystem>();

	private String name;
	private double randomMonitor;
	
	public Subsystem(String name){
		randomMonitor = -1.0;
	    this.name = name;
	    
	    registeredSubsystems.add(this);
	    subsystems.put(name, this);
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
	
	public static Subsystem getSubsystem(String subsystemName){
		return subsystems.get(subsystemName);
	}
	
}
