package ca.team5406.util;

import java.util.ArrayList;

import ca.team5406.frc2016.auto.actions.Action;
import ca.team5406.frc2016.auto.actions.Action.ActionState;

public class AutoBuilder {
	
	private int step;
	private ArrayList<Action> actions;
	
	public AutoBuilder(){
		step = 0;
		actions = new ArrayList<Action>();
	}
	
	public void reset(){
		step = 0;
	}
	
	public void addStep(Action action){
		actions.add(action);
	}
	
	public void nextAction(){
		step++;
	}
	
	public void stop(){
		step = -1;
	}
	
	public void iterActions(){
		if(step < 0){
			return;
		}
		Action currentStep = actions.get(step);
		ActionState currentState = currentStep.getState();
		if(currentState == ActionState.PRE){
			currentStep.start();
		}
		else if(currentState == ActionState.RUNNING){
			currentStep.run();
		}
		else if(currentState == ActionState.FINISHED ||
		   (currentStep.shouldContAfterTimeout() && currentState == ActionState.TIMED_OUT)){
			nextAction();
		}
		//Otherwise
		else{
			stop();
		}
		
	}
	
}
