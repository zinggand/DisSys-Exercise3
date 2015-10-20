package ch.ethz.inf.vs.a3.vsjgigerchat.clock;

/**
 * Created by Andreas on 20.10.2015.
 */
public class LamportClock implements Clock {


    private int time;

    public LamportClock(){
        time = 0;
    }

    public void setTime(int time){
        this.time = time;
    }

    public int getTime(){
        return time;
    }


    @Override
    public String toString(){
        return Integer.toString(getTime());
    }

    @Override
    public void update(Clock other) {
        if (other != null) {
            LamportClock otherClock = (LamportClock) other;
            int otherTime = otherClock.getTime();
            if(otherTime >= getTime())
                setTime(otherTime + 1);
        } // else (other clock is null, and thus nothing needs to be done
    }

    @Override
    public void setClock(Clock other) {
        LamportClock otherClock = (LamportClock) other;
        setTime(otherClock.getTime());
    }

    @Override
    public void tick(Integer pid) {
        setTime(getTime() + 1);
    }

    @Override
    public boolean happenedBefore(Clock other) {
        if(other != null) {
            LamportClock otherClock = (LamportClock) other;
            int otherTime = otherClock.getTime();
            if (otherTime > getTime())
                return true;
            return false;
        } else
            return true; // if we compare with "nothing" then that nothing did happen before, since we interpret it's time to be 0
    }

    @Override
    public void setClockFromString(String clock) {
        int value;
        if( clock.matches("\\d+")) {
            value = Integer.parseInt(clock);
            if( value >= 0 )
                setTime(value);
            // else: input was invalid, and time should not be changed
        }
    }
}
