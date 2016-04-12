package ca.team5406.frc2016;

import ca.team5406.frc2016.subsystems.Arm;
import ca.team5406.frc2016.subsystems.BatteringRamp;
import ca.team5406.frc2016.subsystems.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotStateController extends Subsystem{
	
	private Arm arm;
	private BatteringRamp ramp;
	
	private RobotState desiredState;
	private Arm.Positions armPosOverride;
	private BatteringRamp.Positions rampPosOverride;

	public static String NAME = "Robot State Controller";
	
	public static enum RobotState{
		DOWN_DOWN,
		CARRY_MID,
		CARRY_SCALE,
		CARRY_UP,
		UP_SCALE,
		INSIDE_MID,
		INSIDE_SCALE,
		INSIDE_INSIDE,
		NONE_NONE,
		MANUAL_MANUAL;

	    private Arm.Positions armPos;
	    private BatteringRamp.Positions rampPos;
	    public Arm.Positions getArmPos() { return armPos; }
	    public BatteringRamp.Positions getRampPos() { return rampPos; }
		public void set(Arm.Positions armPos, BatteringRamp.Positions rampPos) {
			this.armPos = armPos;
			this.rampPos = rampPos;
		}
	}
	
	public RobotStateController(Arm arm, BatteringRamp ramp){
		super(NAME);
		this.arm = arm;
		this.ramp = ramp;

		RobotState.NONE_NONE.set(Arm.Positions.NONE, BatteringRamp.Positions.NONE);
		RobotState.MANUAL_MANUAL.set(Arm.Positions.MANUAL, BatteringRamp.Positions.MANUAL);
		RobotState.DOWN_DOWN.set(Arm.Positions.DOWN, BatteringRamp.Positions.DOWN);
		RobotState.CARRY_MID.set(Arm.Positions.CARRY, BatteringRamp.Positions.MID);
		RobotState.CARRY_SCALE.set(Arm.Positions.CARRY, BatteringRamp.Positions.SCALE);
		RobotState.CARRY_UP.set(Arm.Positions.CARRY, BatteringRamp.Positions.UP);
		RobotState.UP_SCALE.set(Arm.Positions.UP, BatteringRamp.Positions.SCALE);
		RobotState.INSIDE_MID.set(Arm.Positions.INSIDE, BatteringRamp.Positions.MID);
		RobotState.INSIDE_SCALE.set(Arm.Positions.INSIDE, BatteringRamp.Positions.SCALE);
		RobotState.INSIDE_INSIDE.set(Arm.Positions.INSIDE, BatteringRamp.Positions.INSIDE);
		
		desiredState = RobotState.NONE_NONE;
		armPosOverride = Arm.Positions.NONE;
		rampPosOverride = BatteringRamp.Positions.NONE;
	}
	
	public void setRobotState(RobotState state){
		desiredState = state;
	}
	
	public void setRampOverride(BatteringRamp.Positions pos){
		rampPosOverride = pos;
	}
	
	public void setArmOverride(Arm.Positions pos){
		armPosOverride = pos;
	}
	
	public Arm.Positions getArmPos(){
		return arm.getCurrentPos();
	}
	
	public int getArmEnc(){
		return arm.getEncoder();
	}
	
	public int getRampEnc(){
		return ramp.getEncoder();
	}
	
	public BatteringRamp.Positions getRampPos(){
		return ramp.getCurrentPos();
	}
	
	public RobotState getRobotState(){
		for(RobotState state : RobotState.values()){
			if(state.getArmPos() == getArmPos() && state.getRampPos() == getRampPos()){
				return state;
			}
		}
		return RobotState.NONE_NONE;
	}

	@Override
	public void runControlLoop() {
		Arm.Positions armPos = desiredState.getArmPos();
		BatteringRamp.Positions rampPos = desiredState.getRampPos();
		
		if(armPosOverride == Arm.Positions.MANUAL || (armPosOverride.getValue() != Constants.nullPositionValue && armPosOverride.getValue() > armPos.getValue())){
			armPos = armPosOverride;
		}
		if(rampPosOverride == BatteringRamp.Positions.MANUAL || (rampPosOverride.getValue() != Constants.nullPositionValue && rampPosOverride.getValue() > rampPos.getValue())){
			rampPos = rampPosOverride;
		}
		
		arm.setDesiredPos(armPos);
		ramp.setDesiredPos(rampPos);
	}
	
	@Override
	public void sendSmartdashInfo(){
		SmartDashboard.putBoolean("Driver Override", ((armPosOverride.getValue() != Constants.nullPositionValue) || (rampPosOverride.getValue() != Constants.nullPositionValue)));
	}

}
