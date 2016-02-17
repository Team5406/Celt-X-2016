
package ca.team5406.frc2016;

import ca.team5406.frc2016.subsystems.*;
import ca.team5406.util.joysticks.XboxController;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	private XboxController driverGamepad;
	private XboxController operatorGamepad;
	
	private Compressor compressor;
	private Drivetrain drive;
	private Arm arm;
	private Intake intake;
	private BatteringRamp ramp;
	
	
	// Called once when the robot is booted
    public void robotInit() {
    	driverGamepad = new XboxController(0);
    	operatorGamepad = new XboxController(1);
    	
    	compressor = new Compressor();
    	compressor.setClosedLoopControl(true);
    	drive = new Drivetrain();
    	arm = new Arm();
    	intake = new Intake();
    	ramp = new BatteringRamp();
    }
    
    // Called the first time the robot enters disabled mode (after init)
    public void disabledInit() {
    	Constants.reload();
    }
    
    // Called periodically while the robot is disabled
    public void disabledPeriodic(){
    	driverGamepad.updateButtons();
    	
    	if(driverGamepad.getButtonOnce(XboxController.X_BUTTON)){
    		System.out.println("Reloading");
    		Constants.reload();
    		System.out.println(Constants.debugString);
    	}
    }
    
    // Caled each time the robot enters auton
    public void autonomousInit() {
    	
    }

    // Called periodically while the robot is enabled in auton
    public void autonomousPeriodic() {
    	
    }

    // Called each time the robot enters tele-op
    public void teleopInit(){
    	
    }
    
    // Called periodically while the robot is enabled in tele-op
    public void teleopPeriodic() {
    	driverGamepad.updateButtons();
    	operatorGamepad.updateButtons();
    	
    	//Driver Gamepad
        drive.arcadeDrive(driverGamepad.getLeftY(), driverGamepad.getLeftX());
        if(driverGamepad.getRawButton(XboxController.Y_BUTTON)){
        	drive.shiftUp();
        }
        else if(driverGamepad.getRawButton(XboxController.X_BUTTON)){
        	drive.shiftDown();
        }
        
        //Operator Gamepad
        arm.joystickControl(operatorGamepad.getLeftY());
        ramp.joystickControl(operatorGamepad.getRightY());
        if(Math.abs(operatorGamepad.getRightTrigger()) >= Constants.xboxControllerDeadband){
        	intake.intakeUntilBall();
        }
        else if(Math.abs(operatorGamepad.getLeftTrigger()) >= Constants.xboxControllerDeadband){
        	intake.reverseIntake();
        }
        else{
        	intake.stopIntake();
        }
        
        
        
        drive.sendSmartdashInfo();
        SmartDashboard.putString("Const", Constants.debugString);
    }
    
}
