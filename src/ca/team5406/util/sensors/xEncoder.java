package ca.team5406.util.sensors;

import edu.wpi.first.wpilibj.Encoder;

public class xEncoder extends Encoder{
	
	private int zero = 0;

	
	public xEncoder(int aChannel, int bChannel, boolean reverse){
		super(aChannel, bChannel, reverse);
	}

	public xEncoder(int aChannel, int bChannel){
		super(aChannel, bChannel);
	}
	
	public void reset(){
		zero = super.get();
	}
	
	public int get(){
		return (super.get() - zero);
	}
	
	public int getActual(){
		return super.get();
	}
	
	public void preset(int setpoint){
		zero -= setpoint;
	}
	
	public void set(int setpoint){
		zero = super.get() - setpoint;
	}
	
}
