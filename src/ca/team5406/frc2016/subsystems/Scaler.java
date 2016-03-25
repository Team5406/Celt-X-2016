package ca.team5406.frc2016.subsystems;

import ca.team5406.frc2016.Constants;
import ca.team5406.util.Util;
import ca.team5406.util.sensors.RelativeEncoder;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.VictorSP;

public class Scaler extends Subsystem {

	private VictorSP motorA;
	private VictorSP motorB;
	
	private CANTalon talonA;
	private CANTalon talonB;
	
	private RelativeEncoder enc;
	private DigitalInput stopSensor;
	
	private boolean isPracticeBot;
	
	private Position desiredPosition;
	private Position currentPosition;

	private Position previousPosition;
	
	public static enum Position{
		START,
		MID,
		IN,
		OUT,
		NONE,
		MANUAL,
		MOVING;

	    private int pos;
	    public int getValue() { return pos; }
		public void set(int pos) {
			this.pos = pos;
		}
	}

	public Scaler(boolean isPracticeBot) {
		super("Scaler");
		this.isPracticeBot = isPracticeBot;
		
		if(isPracticeBot){
			talonA = new CANTalon(Constants.scalerTalonA);
			talonB = new CANTalon(Constants.scalerTalonB);
			talonA.changeControlMode(TalonControlMode.PercentVbus);
			talonB.changeControlMode(TalonControlMode.PercentVbus);
		}

		motorA = new VictorSP(Constants.scalerMotorA);
		motorB = new VictorSP(Constants.scalerMotorB);
		
		enc = new RelativeEncoder(Constants.scalerEncA, Constants.scalerEncB);
		stopSensor = new DigitalInput(Constants.scalerStopSensor);

		Position.IN.set(Constants.scalerInPosition);
		Position.MID.set(Constants.scalerMidPosition);
		Position.OUT.set(Constants.scalerOutPosition);
		Position.START.set(Constants.scalerStartPosition);
		Position.NONE.set(Constants.nullPositionValue);
		Position.MANUAL.set(Constants.nullPositionValue);
		Position.MOVING.set(Constants.nullPositionValue);
		
		currentPosition = Position.NONE;
		desiredPosition = Position.NONE;
		previousPosition = Position.NONE;
	}
	
	private void set(double value){
		if(getStopSensor()){
			value = 0.0;
		}
		value = Util.limitValue(value, 1.0);
		if(isPracticeBot){
			talonA.set(value);
			talonB.set(value);
		}
		else{
			motorA.set(value);
			motorB.set(value);
		}
	}
	
	public void joystickControl(double value){
		value = Util.applyDeadband(value, Constants.xboxControllerDeadband);
		if(desiredPosition == Position.MANUAL){
			set(value);
		}
	}
	
	public Position getDesiredPosition(){
		return desiredPosition;
	}
	
	public int getEncoder(){
		return -enc.get();
	}
	
	public boolean getStopSensor(){
		return stopSensor.get();
	}

	public void setDesiredPos(Position pos, int armEncPosition){
		if(pos != desiredPosition){
			boolean armOk = false;
			boolean scalerOk = false;
			switch(pos){
				default:
					setPos(pos);
					break;
				case IN:
					scalerOk = (getEncoder() > Constants.scalerMidPosition);
					if(scalerOk){
						setPos(pos);
					}
					break;
				case OUT:
					armOk = (armEncPosition >= (Constants.armUpPos - Constants.armPos_deadband));
					scalerOk = (getEncoder() > (Constants.scalerStartPosition - Constants.scalerPosDeadband));
					if(armOk && scalerOk){
						setPos(pos);
					}
					break;
				case START:
				case MID:
					break;
			}
		}
	}
	
	private void setPos(Position pos){
		desiredPosition = pos;
		currentPosition = Position.MOVING;
	}
	
	@Override
	public void runControlLoop() {
		if(currentPosition == Position.MOVING){
			switch(desiredPosition){
				default:
					break;
				case IN:
					if(getStopSensor()){
						reachedDest();
						set(0.0);
						break;
					}
				case OUT:
				case START:
					if(getEncoder() < desiredPosition.getValue() - Constants.scalerPosDeadband){
						set(1.0);
					}
					else if(getEncoder() > desiredPosition.getValue() + Constants.scalerPosDeadband){
						set(-1.0);
					}
					else{
						reachedDest();
						set(0.0);
					}
					break;
			}
		}
	}
	
	private void reachedDest(){
		previousPosition = currentPosition;
		currentPosition = desiredPosition;
		desiredPosition = Position.NONE;
	}

	@Override
	public void sendSmartdashInfo() {
		SmartDashboard.putNumber("Scaler Enc", getEncoder());
		SmartDashboard.putBoolean("Scaler Stop", getStopSensor());
		SmartDashboard.putString("Scaler Pos", currentPosition.name());
	}

}
