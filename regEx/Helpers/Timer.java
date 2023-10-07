package regEx.Helpers;

public class Timer {
  int start = 0;

  public Timer() { start = (int)System.currentTimeMillis(); }
  public int getElapsedTime() {
    return (int)System.currentTimeMillis() - start;
  }

  public int getElapsedTimeAndReset() {
    int elapsed = getElapsedTime();
    reset();
    return elapsed;
  }
  public void reset() { start = (int)System.currentTimeMillis(); }
}