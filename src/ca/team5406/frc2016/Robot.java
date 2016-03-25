
package ca.team5406.frc2016;

import ca.team5406.frc2016.auto.*;
import ca.team5406.frc2016.subsystems.*;
import ca.team5406.util.joysticks.XboxController;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	private XboxController driverGamepad;
	private XboxController operatorGamepad;
	
	private DigitalInput practiceBotIdentifier;
	private boolean isPracticeBot;
	private Timer timer;
	private Timer autonDelayTimer;
	
	private Compressor compressor;
	private Drive drive;
	private Arm arm;
	private Intake intake;
	private BatteringRamp ramp;
	private Scaler scaler;
	private RobotStateController robotState;
	
	private SendableChooser autonSelector;
	private AutonomousRoutine selectedRoutine;
	
	private DigitalOutput statusLed;
	
	// Called once when the robot is booted
    public void robotInit() {
    	System.out.println("Robot Initializing");
    	statusLed = new DigitalOutput(8);
    	statusLed.set(false);
    	new Constants().loadFromFile();
    	
    	driverGamepad = new XboxController(0);
    	operatorGamepad = new XboxController(1);
    	
    	practiceBotIdentifier = new DigitalInput(Constants.practiceBotIdentifier);
    	isPracticeBot = practiceBotIdentifier.get();
    	
    	compressor = new Compressor();
    	compressor.setClosedLoopControl(true);
    	drive = new Drive();
    	arm = new Arm();
    	intake = new Intake(isPracticeBot);
    	ramp = new BatteringRamp(isPracticeBot);
    	scaler = new Scaler(isPracticeBot);
    	robotState = new RobotStateController(arm, ramp);
    	
    	autonSelector = new SendableChooser();
    	selectedRoutine = new DoNothing();
    	
    	statusLed.set(true);
    	timer = new Timer();
    	timer.start();
    	autonDelayTimer = new Timer();
    	System.out.println("Init Done");
    }
    
    // Called the first time the robot enters disabled mode (after init)
    public void disabledInit() {
//    	System.out.println("Disabled Start");
    	autonSelector.addDefault("Do Nothing", new DoNothing());
    	autonSelector.addObject("Cross LB or Port", new CrossLowBar(robotState, drive));
    	autonSelector.addObject("Cross LB and Score", new CrossLowAndScore(robotState, drive, intake));
    	autonSelector.addObject("Cross B or D", new CrossOther(drive, robotState));
    }
    
    // Called periodically while the robot is disabled
    public void disabledPeriodic(){  
    	updateStuff();
    }
    
    // Called each time the robot enters auton
    public void autonomousInit() {
    	autonDelayTimer.start();
    	selectedRoutine = (AutonomousRoutine) autonSelector.getSelected();
    	robotState.setRobotState(RobotStateController.RobotState.NONE_NONE);
    	scaler.setDesiredPos(Scaler.Position.NONE, 0);
    	selectedRoutine.start();
    	drive.stopMotors();
    	arm.stopMotors();
    	ramp.stopMotors();
    	drive.resetDriveTo();
    	drive.resetTurnTo();
    	firstRun = true;
    }

    private boolean firstRun = true;
	@Override
	public void autonomousPeriodic() {
//		SmartDashboard.putNumber("Auton Delay", 8.0 - autonDelayTimer.get());
//		if(autonDelayTimer.get() >= 8.0){
//			if(firstRun){
//				firstRun = false;
//				selectedRoutine.resetTimer();
//			}
//			autonDelayTimer.stop();
			selectedRoutine.run();
//		}
		selectedRoutine.sendSmartDashInfo();
		updateStuff();
	}

    // Called each time the robot enters tele-op
    public void teleopInit(){
//    	System.out.println("Teleop Start");
    	if(selectedRoutine.isRunning()){
    		selectedRoutine.stop();
    	}

    	drive.setControlMode(Drive.ControlMode.POWER);
    	robotState.setRobotState(RobotStateController.RobotState.NONE_NONE);
    	scaler.setDesiredPos(Scaler.Position.NONE, 0);
    	
//    	drive.resetEncoder();
    	drive.stopMotors();
    	arm.stopMotors();
    	ramp.stopMotors();
    	intake.stopMotors();
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
        
//        Ramp Override
        if(driverGamepad.getRightTriggerPressed()){
        	robotState.setRampOverride(BatteringRamp.Positions.SCALE);
        }
        else{
        	robotState.setRampOverride(BatteringRamp.Positions.NONE);
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

        updateStuff();
    }

	public void updateStuff() {
		for(Subsystem sub: Subsystem.registeredSubsystems){
			if(isEnabled() || isAutonomous() || isTest()){
				sub.run();
			}
			sub.sendSmartdashInfo();
		}
		
//      SmartDashboard
		operatorGamepad.updateButtons();
		driverGamepad.updateButtons();
        
		if(timer.get() >= 1.0){
			try{
//				autonSelector = (SendableChooser) SmartDashboard.getData("Auton");
		    	selectedRoutine = (AutonomousRoutine) autonSelector.getSelected();
				SmartDashboard.putString("Selected Auto", selectedRoutine.getName());
			}
			catch(Exception ex){
				System.out.println("Error reading auto");
				ex.printStackTrace();
			}
			timer.reset();
		}
		
        SmartDashboard.putBoolean("Practice Bot", this.isPracticeBot);
    	SmartDashboard.putData("Auton", autonSelector);
    	SmartDashboard.putNumber("Random", Math.random());
    	SmartDashboard.putNumber("DPad", operatorGamepad.getPOV());
	}
}
