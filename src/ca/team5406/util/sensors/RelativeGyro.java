package ca.team5406.util.sensors;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;

public class RelativeGyro extends AHRS{
	
	private float zero = 0;

	
	public RelativeGyro(){
		super(SPI.Port.kMXP);
	}
	
	public void zero(){
		zero = super.getYaw();
	}
	
	public float getYaw(){
		return (super.getYaw() - zero);
	}
	
	public float getActual(){
		return super.getYaw();
	}
	
	public void preset(float setpoint){
		zero -= setpoint;
	}
	
	public void set(float setpoint){
		zero = super.getYaw() - setpoint;
	}
	
}
