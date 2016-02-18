
package ca.team5406.frc2016;

import ca.team5406.frc2016.subsystems.*;
import ca.team5406.util.Util;
import ca.team5406.util.joysticks.XboxController;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;

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
        
//      Operator Gamepad
//        if(operatorGamepad.getButtonOnce(XboxController.A_BUTTON)){
//        	arm.setDesiredPos(Arm.Positions.DOWN);
//        }
//        else if(operatorGamepad.getButtonOnce(XboxController.X_BUTTON)){
//        	arm.setDesiredPos(Arm.Positions.CARRY);
//        }
//        else if(operatorGamepad.getButtonOnce(XboxController.B_BUTTON)){
//        	arm.setDesiredPos(Arm.Positions.INSIDE);
//        }
//        else if(operatorGamepad.getButtonOnce(XboxController.Y_BUTTON)){
//        	arm.setDesiredPos(Arm.Positions.UP);
//        	if(ramp.getCurrentPos() == BatteringRamp.Positions.UP || ramp.getDesiredPos() == BatteringRamp.Positions.UP){
//        		ramp.setDesiredPos(BatteringRamp.Positions.SCALE);
//        	}
//        }
        
//        if(operatorGamepad.getDirectionPad() == 0){ //DOWN
//        	ramp.setDesiredPos(BatteringRamp.Positions.DOWN);
//        }
//        else if(operatorGamepad.getDirectionPad() == 90){ //MID
//        	ramp.setDesiredPos(BatteringRamp.Positions.MID);
//        }
//        else if(operatorGamepad.getDirectionPad() == 180){ //UP
//        	if(arm.getDesiredPos() == Arm.Positions.UP || arm.getCurrentPos() == Arm.Positions.UP){
//        		ramp.setDesiredPos(BatteringRamp.Positions.UP);
//        	}
//        	else{
//        		ramp.setDesiredPos(BatteringRamp.Positions.SCALE);
//        	}
//        }
//        else if(operatorGamepad.getDirectionPad() == 270){ //UP
//        	ramp.setDesiredPos(BatteringRamp.Positions.SCALE);
//        }
        
        arm.setDesiredPos(Arm.Positions.MANUAL);
        arm.joystickControl(operatorGamepad.getLeftY());
        ramp.setDesiredPos(BatteringRamp.Positions.MANUAL);
        ramp.joystickControl(operatorGamepad.getRightY());
        
        
        intake.runControlLoop(operatorGamepad.getRightTriggerPressed(), operatorGamepad.getLeftTriggerPressed());
//        ramp.runControlLoop();
        
        
//        drive.sendSmartdashInfo();
//        arm.sendSmartdashInfo();
//        ramp.sendSmartdashInfo();
//        SmartDashboard.putString("Const", Constants.debugString);
    }
    

    private Timer waveTimer = new Timer();
    public void testInit(){
    	waveTimer.start();
    }
    
    public void testPeriodic(){
    	double wave = Util.squareWave(waveTimer, 2.0, 0, 1);
    	if(wave == 0){
    		ramp.setDesiredPos(BatteringRamp.Positions.SCALE);
    	}
    	else{
    		ramp.setDesiredPos(BatteringRamp.Positions.MID);
    	}
    	
    	if(wave == 0){
    		arm.setDesiredPos(Arm.Positions.INSIDE);
    	}
    	else{
    		arm.setDesiredPos(Arm.Positions.CARRY);
    	}
    }
    
}
