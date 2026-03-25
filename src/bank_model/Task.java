package bank_model;

import bank_model.Delay.DelayGenerator;
import bank_model.Delay.NormalGenerator;
import bank_model.Enums.ManagerAct;

public class Task {
    private double tCreated;
    private double tFinished;
    private int type;
    public double arrivalTime;
    public double serviceStartTime;
    public DelayGenerator dg;
    private boolean byServerProcessed;
    private ManagerAct managerAct;
    private double timeOut;

    public Task(double tCreated){
        this(tCreated, -1);
        dg = null;
        managerAct = ManagerAct.getInformation;
    }
    public Task(double tCreated, int type){
        this.tCreated = tCreated;
        this.type = type;
        dg = null;
    }
    public Task(double tCreated, NormalGenerator ng){
        this(tCreated, -1);
        this.dg = ng;
    }

    public void setManagerAct(ManagerAct act){
        managerAct = act;
    }

    public ManagerAct getManagerAct(){
        return managerAct;
    }

    public boolean isByServerProcessed(){return byServerProcessed;}
    public void setByServerProcessed(){
        byServerProcessed = true;}
    public int getType(){return type;}
    public double getTCreated(){
        return tCreated;
    }
    public void setTFinished(double time){
        tFinished = time;
    }
    public void setType(int newType){type = newType;}
    public double getTFinished(){
        return tFinished;
    }
    public void setTimeOut(double _timeOut){timeOut = _timeOut;}
    public double getTimeOut() {return timeOut;}
}
