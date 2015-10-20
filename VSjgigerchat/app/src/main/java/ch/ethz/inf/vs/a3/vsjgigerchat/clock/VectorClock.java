package ch.ethz.inf.vs.a3.vsjgigerchat.clock;

/**
 * Created by Andreas on 20.10.2015.
 */
public class VectorClock implements Clock {


    public void setTime(){

    }

    public void addProcess(Integer pid, int time){

    }

    public int getTime(int pid) {
        return 0;
    }

    @Override
    public void update(Clock other) {

    }

    @Override
    public void setClock(Clock other) {

    }

    @Override
    public void tick(Integer pid) {

    }

    @Override
    public boolean happenedBefore(Clock other) {
        return false;
    }

    @Override
    public void setClockFromString(String clock) {

    }


}
