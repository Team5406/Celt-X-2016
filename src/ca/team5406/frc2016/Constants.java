package ca.team5406.frc2016;

import ca.team5406.util.ConstantsBase;

public class Constants extends ConstantsBase {
	//Talon SRX IDs
    public static int leftDriveMotorA = 1;
    public static int leftDriveMotorB = 2;
    public static int rightDriveMotorA = 3;
    public static int rightDriveMotorB = 4;
    public static int armMotorA = 5;
    public static int armMotorB = 6;

    //PWM Ports
    public static int intakeMotorA = 0;
    public static int intakeMotorB = 1;
    public static int batteringRampMotor = 2;
    public static int winchMotorA = 3;
    public static int winchMotorB = 4;
    
    //Digital Ports
    public static int ballSensor = 0;
    public static int batteringRampEncA = 1;
    public static int batteringRampEncB = 1;
    
    //Analog Ports
    public static int armPot = 0;
    public static int winchPot = 1;

    //Solenoid Ports
    public static int shiftUpSolenoid = 0;
    public static int shiftDownSolenoid = 1;

    //Drive PID Constants
    public static double lowGearDriveTo_kP = 0;
    public static double lowGearDriveTo_kI = 0;
    public static double lowGearDriveTo_kD = 0;
    public static double lowGearDriveTo_kF = 0;
    public static double lowGearDriveToDeadband = 0;
    
    //Drive Constants
    public static int lowGearCpr = 0;
    public static double distPerRev = (8 * Math.PI);
    
    //Arm Constants
    public static int armUpPosition = 1000;
    public static int armInsidePosition = 700;
    public static int armCarryPosition = 200;
    public static int armDownPosition = 0;
    
    //Battering Ramp Constants
    public static int rampStowedPosititon = 0;
    public static int rampUpPosition = 200;
    public static int rampMidPosition = 600;
    public static int rampDownPosition = 1000;
    
    //Controller Constants
    public static double xboxControllerDeadband = 0.15;
    
    //Debug
    public static String debugString = "Default";


    public static void reload(){
    	new Constants().loadFromFile();
    }
    
    @Override
    public String getFileLocation() {
        return "~/constants.txt";
    }

    static {
        new Constants().loadFromFile();
    }
}