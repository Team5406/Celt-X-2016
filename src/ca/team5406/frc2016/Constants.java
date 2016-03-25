package ca.team5406.frc2016;

import ca.team5406.util.ConstantsBase;

public class Constants extends ConstantsBase {
	//Global Constants
	public static int nullPositionValue = -9999999;
	
	//Talon SRX IDs
    public static int leftDriveMotorA = 10;
    public static int leftDriveMotorB = 11;
    public static int rightDriveMotorA = 12;
    public static int rightDriveMotorB = 13;
    public static int armMotorA = 14;
    public static int armMotorB = 15;
    public static int scalerTalonA = 16;
    public static int scalerTalonB = 17;
    public static int intakeTalon = 18;
    public static int rampTalon = 19;

    //PWM Ports
    public static int intakeMotorA = 0;
    public static int intakeMotorB = 1;
    public static int batteringRampMotor = 2;
    public static int scalerMotorA = 3;
    public static int scalerMotorB = 4;
    
    //Digital Ports
    public static int ballSensor = 0;
    public static int batteringRampEncA = 1;
    public static int batteringRampEncB = 2;
    public static int scalerEncA = 3;
    public static int scalerEncB = 4;
    public static int scalerStopSensor = 5;
    public static int practiceBotIdentifier = 9;
    
    //Analog Ports
    public static int armPot = 0;
    public static int winchPot = 1;

    //Solenoid Ports
    public static int shiftUpSolenoid = 0;
    public static int shiftDownSolenoid = 1;
    
    //Auto Constants
    public static int lowBarDist = 0;
    public static int portcullisDist = 0;
    public static int chevalDist = 0;

    //Drive PID Constants
    public static double lowGearDriveTo_kP = 0.0005;
    public static double lowGearDriveTo_kI = 0.001;
    public static double lowGearDriveTo_kD = 0;
    public static double lowGearDriveTo_kF = 0;
    public static double lowGearDriveToDeadband = 100;
    
    public static double lowGearTurnTo_kP = 0.01;
    public static double lowGearTurnTo_kI = 0.007; //0.0
    public static double lowGearTurnTo_kD = 0.01;
    public static double lowGearTurnToDeadband = 2;

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
    public static double minTurnSpeed = 0.3;
    public static double minDriveSpeed = 0.1;
    
    //Arm Constants
    public static int armUpPos = 51000;			//51000
    public static int armInsidePos = 33000;		//29000
    public static int armCarryPos = 12000;		//12000
    public static int armDownPos = -1000;			//-1000

    public static double armPos_kP = 0.08; 		//0.008
    public static double armPos_kI = 0.0; 		//0.0
    public static double armPos_kD = 0.0;		//0.0
    public static int armPos_deadband = 1000;	//10000
    
    //Battering Ramp Constants
    public static int rampUpPosition = 75000;		//10500;	//33000
    public static int rampInsidePosition = 70000;	//8000;		//24000
    public static int rampDownPosition = -8000;		//-500
    public static int rampScalePosition = 50000; 	//7000;		//21000
    public static int rampMidPosition = 50000;		//4000;		//18000

    public static double rampPos_kP = 0.003;
    public static double rampPos_kI = 0.0;
    public static double rampPos_kD = 0.0;
    
    public static double rampHold_kP = 0.001;		//0.0001
    public static double rampHold_kI = 0.0;		//0.0001
    public static double rampHold_kD = 0.0;		//0.00009
    public static int rampPos_deadband = 500;		//2000
    
    //Scaler Constants
    public static int scalerInPosition = -420;
    public static int scalerStartPosition = 0;
    public static int scalerMidPosition = 1500;
    public static int scalerOutPosition = 4430;
    public static int scalerPosDeadband = 50;
    
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