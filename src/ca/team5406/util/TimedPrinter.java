package ca.team5406.util;

import edu.wpi.first.wpilibj.Timer;

public class TimedPrinter {

	private double period;
	private Timer timer;
	
	public TimedPrinter(double period){
		period = this.period;
		timer = new Timer();
	}
	
	public void setPeriod(double period){
		period = this.period;
	}
	
	public boolean print(String str){
		if(timer.get() >= period){
			timer.reset();
			System.out.println(str);
			return true;
		}
		return false;
	}
	
}
