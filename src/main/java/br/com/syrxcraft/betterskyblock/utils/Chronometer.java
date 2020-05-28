package br.com.syrxcraft.betterskyblock.utils;

public class Chronometer {

    private Long startTime;
    private Long endTime;
    private Long elapsedTime = 0L;

    private boolean isRunning;
    private boolean isFinished;

    public void start(){
        if(!isRunning){
            startTime = System.currentTimeMillis();
            isRunning = true;
        }
    }

    public void stop(){
        endTime = System.currentTimeMillis();
        isFinished = true;
    }

    public void pause(){
        elapsedTime += (startTime - System.currentTimeMillis());
        isRunning = false;
    }

    public void reset(){

        startTime = 0L;
        endTime = 0L;
        elapsedTime = 0L;

        isRunning = false;
        isFinished = false;

    }

    public long elapsedTime(){

        if(isFinished){
            return (endTime - startTime) + elapsedTime;
        }

        return (System.currentTimeMillis() - startTime) + elapsedTime;
    }

    public long elapsedTimeSecs(){
        return Math.abs(elapsedTime() / 1000);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isFinished() {
        return isFinished;
    }


}
