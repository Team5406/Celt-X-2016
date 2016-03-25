package ca.team5406.util.sensors;

import edu.wpi.first.wpilibj.CANTalon;

public class ExternalEncoderTalon extends CANTalon{
	
	private RelativeEncoder encoder;

	public ExternalEncoderTalon(int deviceNumber, int encoderPinA, int encoderPinB) {
		super(deviceNumber);
		encoder = new RelativeEncoder(encoderPinA, encoderPinB);
	}
	
	public void update(){
		this.setEncPosition(encoder.get());
	}
	
	public int getEncPosition(){
		return encoder.get();
	}
	
}
