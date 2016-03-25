package ca.team5406.frc2016.auto;

public class DoNothing extends AutonomousRoutine{

	public DoNothing() {
		super("Do Nothing");
	}

	@Override
	public void init() {
		System.out.println("Doing Nothing on Porpose");
	}

	@Override
	public void execute() {
		end();
	}

	@Override
	public void end() {
		
	}

	@Override
	public void resetTimer(){
		
	}
}
