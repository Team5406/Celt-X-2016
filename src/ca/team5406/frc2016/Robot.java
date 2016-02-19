
package ca.team5406.frc2016;

import ca.team5406.frc2016.auto.*;
import ca.team5406.frc2016.subsystems.*;
import ca.team5406.util.joysticks.XboxController;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends IterativeRobot {

	private XboxController driverGamepad;
	private XboxController operatorGamepad;
	
	private Compressor compressor;
	private Drivetrain drive;
	private Arm arm;
	private Intake intake;
	private BatteringRamp ramp;
	
	private SendableChooser autonSelector;	
	private AutonomousRoutine selectedRoutine;
	
	// Called once when the robot is booted
    public void robotInit() {
    	new Constants().loadFromFile();
    	
    	driverGamepad = new XboxController(0);
    	operatorGamepad = new XboxController(1);
    	
    	compressor = new Compressor();
    	compressor.setClosedLoopControl(true);
    	drive = new Drivetrain();
    	arm = new Arm();
    	intake = new Intake();
    	ramp = new BatteringRamp();
    	
    	autonSelector = new SendableChooser();
    }
    
    // Called the first time the robot enters disabled mode (after init)
    public void disabledInit() {
    	autonSelector.addDefault("Do Nothing", new DoNothing());
    }
    
    // Called periodically while the robot is disabled
    public void disabledPeriodic(){  	
        drive.sendSmartdashInfo();
        arm.sendSmartdashInfo();
        ramp.sendSmartdashInfo();
    }
    
    // Caled each time the robot enters auton
    public void autonomousInit() {
    	selectedRoutine = (AutonomousRoutine) autonSelector.getSelected();
    	selectedRoutine.start();
    }

    // Called each time the robot enters tele-op
    public void teleopInit(){
    	selectedRoutine.kill();
    }
    
    // Called periodically while the robot is enabled in tele-op
    public void teleopPeriodic() {
    	//Driver Gamepad
        drive.arcadeDrive(driverGamepad.getLeftY(), driverGamepad.getLeftX());
        if(driverGamepad.getRawButton(XboxController.Y_BUTTON)){
        	drive.shiftUp();
        }
        else if(driverGamepad.getRawButton(XboxController.X_BUTTON)){
        	drive.shiftDown();
        }
        if((arm.getCurrentPos() == Arm.Positions.DOWN || arm.getDesiredPos() == Arm.Positions.DOWN)&& driverGamepad.getRightTriggerPressed()){
        	arm.setDesiredPos(Arm.Positions.CARRY);
        }
        
//      Operator Gamepad
//        Arm Control
        if(operatorGamepad.getButtonHeld(XboxController.A_BUTTON)){
        	arm.setDesiredPos(Arm.Positions.DOWN);
        }
        else if(operatorGamepad.getButtonHeld(XboxController.B_BUTTON)){
        	arm.setDesiredPos(Arm.Positions.CARRY);
        }
        else if(operatorGamepad.getButtonHeld(XboxController.X_BUTTON)){
        	arm.setDesiredPos(Arm.Positions.INSIDE);
        }
        else if(operatorGamepad.getButtonHeld(XboxController.Y_BUTTON)){
        	arm.setDesiredPos(Arm.Positions.UP);
        	if(ramp.getCurrentPos() == BatteringRamp.Positions.UP || ramp.getDesiredPos() == BatteringRamp.Positions.UP){
        		ramp.setDesiredPos(BatteringRamp.Positions.SCALE);
        	}
            else if(operatorGamepad.getButtonOnce(XboxController.RIGHT_STICK)){
            	arm.setDesiredPos(Arm.Positions.MANUAL);
            }
        }
        else if(arm.getCurrentPos() == Arm.Positions.MANUAL){
        	arm.joystickControl(operatorGamepad.getRightY());
        }

//        Battering Ramp Control
        if(operatorGamepad.getDirectionPad() == 180){
        	ramp.setDesiredPos(BatteringRamp.Positions.DOWN);
        }
        else if(operatorGamepad.getDirectionPad() == 315){
        	if(arm.getDesiredPos() == Arm.Positions.UP || arm.getCurrentPos() == Arm.Positions.UP){
        		ramp.setDesiredPos(BatteringRamp.Positions.SCALE);
        	}
        	else{
        		ramp.setDesiredPos(BatteringRamp.Positions.UP);
        	}
        }
        else if(operatorGamepad.getDirectionPad() == 270){
        	ramp.setDesiredPos(BatteringRamp.Positions.MID);
        }
        else if(operatorGamepad.getDirectionPad() == 90){
        	ramp.setDesiredPos(BatteringRamp.Positions.SCALE);
        }
        else if(operatorGamepad.getButtonOnce(XboxController.LEFT_STICK)){
        	ramp.setDesiredPos(BatteringRamp.Positions.MANUAL);
        }
        else if(ramp.getCurrentPos() == BatteringRamp.Positions.MANUAL){
        	ramp.joystickControl(operatorGamepad.getLeftY());
        }
        
//        Control Loops
        intake.setIntakeButtons(operatorGamepad.getRightTriggerPressed(), operatorGamepad.getLeftTriggerPressed());
        
//      SmartDash
        drive.sendSmartdashInfo();
        arm.sendSmartdashInfo();
        ramp.sendSmartdashInfo();
    }
}
