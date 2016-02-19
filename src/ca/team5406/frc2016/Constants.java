package ca.team5406.frc2016;

import ca.team5406.util.ConstantsBase;

public class Constants extends ConstantsBase {
	//Talon SRX IDs
    public static int leftDriveMotorA = 10;
    public static int leftDriveMotorB = 11;
    public static int rightDriveMotorA = 12;
    public static int rightDriveMotorB = 13;
    public static int armMotorA = 14;
    public static int armMotorB = 15;

    //PWM Ports
    public static int intakeMotorA = 0;
    public static int intakeMotorB = 1;
    public static int batteringRampMotor = 2;
    public static int winchMotorA = 3;
    public static int winchMotorB = 4;
    
    //Digital Ports
    public static int ballSensor = 0;
    public static int batteringRampEncA = 1;
    public static int batteringRampEncB = 2;
    
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

    public static double lowGearSpeed_kP = 0;
    public static double lowGearSpeed_kI = 0;
    public static double lowGearSpeed_kD = 0;
    public static double lowGearSpeed_kF = 0;
    public static double lowGearSpeed_deadband = 0;
    
    //Drive Constants
    public static int lowGearCpr = 0;
    public static double distPerRev = (8 * Math.PI);
    public static double maxSpeed = 17.67; //rev / 10ms
    public static double highGearMult = 3.63;
    
    //Arm Constants
    public static int armUpPos = 243000+290000;
    public static int armInsidePos = 290000;
    public static int armCarryPos = 120000;//-170000;
    public static int armDownPos = 0;//-290000;

    public static double armPos_kP = 0.008;
    public static double armPos_kI = 0.0;
    public static double armPos_kD = 0.000;
    public static int armPos_deadband = 10000;
    
    //Battering Ramp Constants
    public static int rampUpPosition = 60000;
    public static int rampInsidePosition = 45000;
    public static int rampDownPosition = 0;
    public static int rampScalePosition = 38000;
    public static int rampMidPosition = 16000;

    public static double rampPos_kP = 0.0001;
    public static double rampPos_kI = 0.0001;
    public static double rampPos_kD = 0.00009;
    public static double rampHold_kP = 0.0001;
    public static double rampHold_kI = 0.0001;
    public static double rampHold_kD = 0.00009;
    public static int rampPos_deadband = 2000;
    
    //Controller Constants
    public static double xboxControllerDeadband = 0.2;


    public static void reload(){
    	new Constants().loadFromFile();
    }
    
    @Override
    public String getFileLocation() {
        return "~/constants.json";
    }

    static {
        new Constants().loadFromFile();
    }
}