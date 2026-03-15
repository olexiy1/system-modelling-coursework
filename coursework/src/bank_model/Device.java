package bank_model;

import bank_model.Delay.DelayGenerator;
import bank_model.Enums.ManagerAct;

import java.util.ArrayList;

public class Device extends Element {
    private Task curTask;
    private double busyArea=0;
    private double bufferSizeSum=0.0;
    private ManagerAct devicePurpose;
    private ArrayList<Task> taskBuffer;
    private boolean operatesWithBuffer;
    private Task nextOut;

    public Device(String name, DelayGenerator generator) {
        super(name, generator);
        setName(name);
        curTask = null;
        setState(0);
        devicePurpose = null;
        operatesWithBuffer = false;
    }
    public Device(String name, DelayGenerator generator, boolean _operateWithBuffer) {
        super(name, generator);
        setName(name);
        if(_operateWithBuffer)
            taskBuffer = new ArrayList<Task>();
        nextOut = null;
        operatesWithBuffer = _operateWithBuffer;
    }

    //Встановлення функціоналу для альтернативних пристроїв
    public Device(String name, DelayGenerator generator, ManagerAct act) {
        super(name, generator);
        setName(name);
        curTask = null;
        setState(0);
        devicePurpose = act;
        operatesWithBuffer = false;
    }

    @Override
    public void inAct(Task task){
        if(operatesWithBuffer){
            task.setTimeOut(super.getTcurr() + super.getDelay());
            if(task.getTimeOut() < super.getTnext()){
                super.setTnext(task.getTimeOut());
                nextOut = task;
            }
            taskBuffer.add(task);
        }
        else{
            curTask = task;
            setState(1);
        }
    }
    @Override
    public void outAct(){
        if(nextOut == null)
            throw new IllegalStateException("nextOut is null");
        taskBuffer.remove(nextOut);
        super.outAct();
        nextOut.setByServerProcessed(); //оброблено сервером
        super.getNextElement().inAct(nextOut);

        nextOut = null;
        double min = Double.MAX_VALUE;

        for(Task taskInst : taskBuffer){
            if(taskInst.getTimeOut() < min){
                min = taskInst.getTimeOut();
                nextOut = taskInst;
            }
        }
        System.out.println("server buffer size is " + taskBuffer.size());
        super.setTnext(min);
    }

    public Task returnTask(){
        if (curTask == null) {
            throw new IllegalStateException("returnTask on empty device");
        }
        Task t = curTask;
        curTask = null;
        setState(0);
        super.setTnext(Double.MAX_VALUE);
        super.outAct();
        return t;
    }
    public ManagerAct getDevicePurpose(){return devicePurpose;}

    public double getBusyArea(){return busyArea;}
    public double getBufferSizeSum(){return bufferSizeSum;}
    @Override
    public void doStatistics(double dt) {
        if (getState() == 1) {
            busyArea += dt;
        }
        if(taskBuffer != null)
            bufferSizeSum += taskBuffer.size() * dt;
    }

}
