package ca.team5406.frc2016.subsystems;

import ca.team5406.frc2016.Constants;
import ca.team5406.util.PID;
import ca.team5406.util.Util;
import ca.team5406.util.sensors.VictorSafegaurd;
import ca.team5406.util.sensors.RelativeEncoder;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BatteringRamp extends Subsystem {
	
	private VictorSP motor;
	private CANTalon talon;
	private RelativeEncoder encoder;
	private VictorSafegaurd motorSafegaurd;
	
	private boolean isPracticeBot;
	
	private PID positionPid;
	private PID posHoldPid;
	
	private Positions desiredPosition;
	private Positions currentPosition;
	public static final String NAME = "Battering Ramp";
	
	public static enum Positions{
		NONE,
		DOWN,
		MID,
		SCALE,
		INSIDE,
		UP,
		MOVING,
		MANUAL;

	    private int id;
	    public int getValue() { return id; }
		public void set(int i) {
			this.id = i;
		}
	}
	
	public BatteringRamp(boolean isPracticeBot){
		super(NAME);
		this.isPracticeBot = isPracticeBot;
		
		motor = new VictorSP(Constants.batteringRampMotor);
		if(isPracticeBot){
			talon = new CANTalon(Constants.rampTalon);
			talon.changeControlMode(TalonControlMode.PercentVbus);
			talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
			talon.configNominalOutputVoltage(0.0f, -0.0f);
			talon.configPeakOutputVoltage(12.0f, -12.0f);
			talon.setEncPosition(0);
		}
		encoder = new RelativeEncoder(Constants.batteringRampEncA, Constants.batteringRampEncB);
	    encoder.reset();
	    motorSafegaurd = new VictorSafegaurd(encoder, motor, Constants.armPos_deadband);
		
		positionPid = new PID();
		positionPid.setConstants(Constants.rampPos_kP, Constants.rampPos_kI, Constants.rampPos_kD);
		positionPid.setDesiredPosition(getEncoder());
		posHoldPid = new PID();
		posHoldPid.setConstants(Constants.rampHold_kP, Constants.rampHold_kI, Constants.rampHold_kD);
		posHoldPid.setDesiredPosition(getEncoder());
		
		Positions.NONE.set(Constants.nullPositionValue);
		Positions.MOVING.set(Constants.nullPositionValue);
		Positions.MANUAL.set(Constants.nullPositionValue);
		Positions.UP.set(Constants.rampUpPosition);
		Positions.DOWN.set(Constants.rampDownPosition);
		Positions.MID.set(Constants.rampMidPosition);
		Positions.SCALE.set(Constants.rampScalePosition);
		Positions.INSIDE.set(Constants.rampInsidePosition);

		desiredPosition = Positions.NONE;
		currentPosition = Positions.NONE;
	}
	
	public void setDesiredPos(Positions pos){
		if(pos == Positions.MANUAL){
			currentPosition = Positions.MANUAL;
			desiredPosition = Positions.MANUAL;
			return;
		}
		else{
			currentPosition = Positions.MOVING;
			desiredPosition = pos;
			positionPid.setDesiredPosition(desiredPosition.getValue());
		}
	}

	public Positions getDesiredPos(){
		return desiredPosition;
	}
	public Positions getCurrentPos(){
		return currentPosition;
	}
	
	public int getEncoder(){
		if(isPracticeBot){
			return -talon.getEncPosition();
		}
		else{
			return encoder.get();
		}
	}
	
	public void joystickControl(double value){
		if(desiredPosition == Positions.MANUAL){
			value = Util.applyDeadband(value, Constants.xboxControllerDeadband);
			set(value);
		}
	}
	
    @Override
    public void runControlLoop() {
    	motorSafegaurd.test();
		if(currentPosition == Positions.MOVING && desiredPosition.getValue() != Constants.nullPositionValue){
			switch(desiredPosition){
				case DOWN:
				case UP:
				case MID:
				case SCALE:
				case INSIDE:
					double speed = Util.limitValue(positionPid.calcSpeed(getEncoder()), 1.0);
					set(speed);
					break;
				case NONE:
				case MANUAL:
				case MOVING:
					break;
			}
			posHoldPid.setDesiredPosition(getEncoder());
			if(positionPid.isDone(getEncoder(), Constants.rampPos_deadband)){
				currentPosition = desiredPosition;
				desiredPosition = Positions.NONE;
				positionPid.resetAccumI();
			}
		}
//		else if(currentPosition != Positions.MANUAL && posHoldPid.getDesiredPosition() != Constants.nullPositionValue){
//			double speed = Util.limitValue(posHoldPid.calcSpeed(getEncoder()), 1.0);
//			set(speed);
//		}
		else if(currentPosition != Positions.MANUAL){
			set(0);
		}
	}
	
	public void stopMotors(){
		set(0);
	}
	
	public void resetEncoder(){
		if(isPracticeBot){
			talon.setEncPosition(0);
		}
		else{
			encoder.reset();
		}
	}
	
	private void set(double value){
		value = Util.limitValue(value, 1.0) * 0.8;
		if(value != 0 && ((value < 0 && getEncoder() < Constants.rampDownPosition) || (value > 0 && getEncoder() > Constants.rampUpPosition))){
			set(0);
		}
		else{
			if(isPracticeBot){
				talon.set(value);
			}
			else{
				motor.set(value);
			}
		}
	}
	
	@Override
	public void sendSmartdashInfo(){
		SmartDashboard.putBoolean("Ramp On Target", positionPid.isDone(getEncoder(), Constants.rampPos_deadband));
		SmartDashboard.putString("Ramp Pos", getCurrentPos().name());
		SmartDashboard.putNumber("Ramp Enc", getEncoder());
		SmartDashboard.putBoolean("Ramp Motor Good", !motorSafegaurd.getCaution());
		SmartDashboard.putNumber("Ramp random", this.getRandomMonitor());
	}
	
}
