package ca.team5406.util;

import java.util.ArrayList;

/**
 * MultiLooper.java
 * 
 * Runs several Loopables simultaneously with one Looper.
 * Useful for running a bunch of control loops
 * with only one Thread worth of overhead.
 * 
 * @author Tom Bottiglieri
 */
public class MultiLooper implements Loopable {
  private Looper looper;
  private ArrayList<Loopable> loopables = new ArrayList<>();
  public MultiLooper(double period) {
    looper = new Looper(this, period);
  }

  @Override
  public void runControlLoop() {
    int i;
    for (i = 0; i < loopables.size(); ++i) {
      Loopable c = (Loopable) loopables.get(i);
      if (c != null) {
          c.runControlLoop();
          c.sendSmartdashInfo();
      }
    }
  }

  public void start() {
    looper.start();    
  }
  
  public void stop() {
    looper.stop();
  }

  public void addLoopable(Loopable c) {
    loopables.add(c);
  }
  
  public void sendSmartdashInfo(){}

}