package ca.team5406.frc2016;

import ca.team5406.frc2016.auto.*;
import ca.team5406.frc2016.subsystems.*;
import ca.team5406.util.Loopable;
import ca.team5406.util.MultiLooper;
import ca.team5406.util.joysticks.XboxController;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot implements Loopable {

	private XboxController driverGamepad;
	private XboxController operatorGamepad;
	
	private boolean isPracticeBot;
	
	private Compressor compressor;
	private Drive drive;
	private Arm arm;
	private Intake intake;
	private PortRoller roller;
	private BatteringRamp ramp;
	private Scaler scaler;
	private RobotStateController robotState;
	
	private SendableChooser autonSelector;
	private AutonomousRoutine selectedRoutine;
	
	private DigitalOutput statusLed;
	
	private MultiLooper subsystemUpdater;
	private MultiLooper slowUpdater;
	
	//Comp Bot TODO: Switch Status LED to port 9
	
	// Called once when the robot is booted
    public void robotInit() {
    	System.out.println("Robot Initializing");
    	
    	statusLed = new DigitalOutput(9);
    	statusLed.set(false);

    	driverGamepad = new XboxController(0);
    	operatorGamepad = new XboxController(1);
    	
    	isPracticeBot = true;

    	compressor = new Compressor();
    	compressor.setClosedLoopControl(true);
    	drive = new Drive();
    	arm = new Arm(isPracticeBot);
    	intake = new Intake(isPracticeBot);
    	roller = new PortRoller();
    	ramp = new BatteringRamp(isPracticeBot);
    	scaler = new Scaler(isPracticeBot);
    	robotState = new RobotStateController(arm, ramp);
    	
    	autonSelector = new SendableChooser();
    	selectedRoutine = new DoNothing();

    	statusLed.set(true);
    	
    	subsystemUpdater = new MultiLooper(1.0 / 100.0);
    	subsystemUpdater.addLoopable(drive);
    	subsystemUpdater.addLoopable(intake);
    	subsystemUpdater.addLoopable(ramp);
    	subsystemUpdater.addLoopable(scaler);
    	subsystemUpdater.addLoopable(arm);
    	subsystemUpdater.addLoopable(robotState);
    	
    	slowUpdater = new MultiLooper(1.0 / 50.0);
    	slowUpdater.addLoopable(this);
    	slowUpdater.start();

//    	SmartDashboard.putNumber("100turn_kP", Constants.lowGearTurnTo_kP*100);
//    	SmartDashboard.putNumber("100turn_kI", Constants.lowGearTurnTo_kI*100);
//    	SmartDashboard.putNumber("100turn_kD", Constants.lowGearTurnTo_kD*100);
    	
//    	SmartDashboard.putNumber("100drive_kP", Constants.highGearDriveTo_kP*100);
//    	SmartDashboard.putNumber("100drive_kI", Constants.highGearDriveTo_kI*100);
//    	SmartDashboard.putNumber("100drive_kD", Constants.highGearDriveTo_kD*100);
    	
//    	autonDelayTimer = new Timer();
    	System.out.println("Init Done");
    }
    
    // Called the first time the robot enters disabled mode (after init)
    public void disabledInit() {
    	System.out.println("Disabled Start");
    	
    	subsystemUpdater.stop();
    	
    	autonSelector.addDefault("Do Nothing", new DoNothing());
    	autonSelector.addObject("Cross LB and Score (F)", new CrossLowAndScore(drive, robotState, intake));
    	autonSelector.addObject("Cross LB (F)", new CrossLowBar(drive, robotState));
    	autonSelector.addObject("Cross Port (F)", new CrossPort(drive, robotState, roller));
    	autonSelector.addObject("Cross Cheval (F)", new CrossCheval(drive, robotState));
    	autonSelector.addObject("Cross Port (F)", new CrossCheval(drive, robotState));
    	autonSelector.addObject("Cross B or D (R)", new CrossOther(drive, robotState));
    	
//    	autonSelector.addObject("Drive Reverse 100in", new DistanceTest(drive, robotState));
//    	autonSelector.addObject("Gather Drive Data", new GatherLinearizeData(drive));
//    	autonSelector.addObject("PID Drive Test", new DrivePidTuneTest(drive));
//    	autonSelector.addObject("PID Turn Test", new TurnPidTuneTest(drive));
    }
    
    // Called each time the robot enters auton
    public void autonomousInit() {
    	subsystemUpdater.start();
    	
		selectedRoutine = (AutonomousRoutine) autonSelector.getSelected();
		selectedRoutine.start();
		
    	robotState.setRobotState(RobotStateController.RobotState.NONE_NONE);
    	scaler.setDesiredPos(Scaler.Position.START, 0);
    	
    	drive.resetDriveTo();
    	drive.resetTurnTo();
    	drive.resetEncoders();
    	drive.resetGyro();
    }

	@Override
	public void autonomousPeriodic() {
		selectedRoutine.run();
		selectedRoutine.sendSmartDashInfo();
	}

    // Called each time the robot enters tele-op
    public void teleopInit(){
    	System.out.println("Teleop Start");
    	
    	subsystemUpdater.start();
    	
    	if(selectedRoutine.isRunning()){
    		selectedRoutine.stop();
    	}

    	robotState.setRobotState(RobotStateController.RobotState.NONE_NONE);
    	scaler.setDesiredPos(Scaler.Position.START, 0);
    	
    	drive.resetEncoders();
    	drive.stopMotors();
    	drive.shiftUp();
    	arm.stopMotors();
    	ramp.stopMotors();
    	intake.stopMotors();
    	scaler.stopMotors();
    	drive.resetDriveTo();
    	drive.resetTurnTo();
    }
    
    // Called periodically while the robot is enabled in tele-op
    public void teleopPeriodic() {
//    	Driver Gamepad
//    	  Movement
        drive.arcadeDrive(driverGamepad.getLeftY(), driverGamepad.getLeftX());
        
//        Shifting
        if(driverGamepad.getButtonHeld(XboxController.Y_BUTTON)){
        	drive.shiftUp();
        }
        else if(driverGamepad.getButtonHeld(XboxController.X_BUTTON)){
        	drive.shiftDown();
        }
        
//        Roller Control
        if(driverGamepad.getRightTriggerPressed()){
        	roller.setIntakeSpeed(1.0);
        }
        else{
        	roller.setIntakeSpeed(0.0);
        }
        
//      Operator Gamepad
//        Robot State Control
        if(operatorGamepad.getButtonHeld(XboxController.A_BUTTON)){
        	robotState.setRobotState(RobotStateController.RobotState.DOWN_DOWN);
        }
        else if(operatorGamepad.getButtonHeld(XboxController.B_BUTTON)){
        	robotState.setRobotState(RobotStateController.RobotState.CARRY_UP);
        }
        else if(operatorGamepad.getButtonHeld(XboxController.X_BUTTON)){
        	robotState.setRobotState(RobotStateController.RobotState.INSIDE_INSIDE);
        }
        else if(operatorGamepad.getButtonHeld(XboxController.Y_BUTTON)){
        	robotState.setRobotState(RobotStateController.RobotState.UP_SCALE);
        }
        
//        Scaler Control
        if(operatorGamepad.getButtonHeld(XboxController.LEFT_BUMPER)){
        	scaler.setDesiredPos(Scaler.Position.OUT, arm.getEncoder());
        	if(scaler.getDesiredPosition() == Scaler.Position.OUT){
        		intake.setIntakeButtons(false, true);
        	}
        }
        else if(operatorGamepad.getButtonHeld(XboxController.RIGHT_BUMPER)){
        	scaler.setDesiredPos(Scaler.Position.IN, arm.getEncoder());
        }
        
//        Trigger Manual Control
        if(operatorGamepad.getButtonHeld(XboxController.START_BUTTON)){
        	scaler.setDesiredPos(Scaler.Position.MANUAL, arm.getEncoder());
        	robotState.setRobotState(RobotStateController.RobotState.MANUAL_MANUAL);
        }
        
//      	Arm Manual Control
    	arm.joystickControl(operatorGamepad.getLeftY());
        if(operatorGamepad.getButtonHeld(XboxController.LEFT_STICK)){
        	arm.resetEncoders();
        }

//        	Ramp Manual Control
    	ramp.joystickControl(operatorGamepad.getRightY());
        if(operatorGamepad.getButtonHeld(XboxController.RIGHT_STICK)){
        	ramp.resetEncoder();
        }
        
//		      Scaler Manual Control
		if(operatorGamepad.getDirectionPad() == XboxController.DirectionPad.UP){
		 	scaler.joystickControl(1.0);
		}
		else if(operatorGamepad.getDirectionPad() == XboxController.DirectionPad.DOWN){
		  	scaler.joystickControl(-1.0);
		}
		else{
			scaler.joystickControl(0.0);
		}
        
//        	Intake Control
        intake.setIntakeButtons(operatorGamepad.getRightTriggerPressed(), operatorGamepad.getLeftTriggerPressed());
    }

	@Override
	public void runControlLoop(){
		operatorGamepad.updateButtons();
		driverGamepad.updateButtons();
	}
	
	@Override
	public void sendSmartdashInfo(){
		if(isDisabled()){
			try{
		    	selectedRoutine = (AutonomousRoutine) autonSelector.getSelected();
				SmartDashboard.putString("Selected Auto", selectedRoutine.getName());
			}
			catch(Exception ex){
				System.out.println("Error Reading Auto - This is probably okay.");
			}
			for(Subsystem s: Subsystem.registeredSubsystems){
				if(isOperatorControl() || isAutonomous()){
					s.runControlLoop();
				}
				s.sendSmartdashInfo();
			}
		}

    	SmartDashboard.putData("Auton", autonSelector);
        SmartDashboard.putBoolean("Practice Bot", isPracticeBot);
    	SmartDashboard.putNumber("Random", Math.random());
	}
}
