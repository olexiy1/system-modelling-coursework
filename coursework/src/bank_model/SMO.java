package bank_model;

import bank_model.Enums.ManagerAct;
import bank_model.Enums.Role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SMO extends Element {
    private int servedByQueueCount;
    private int tasksEntered;
    private double sumQueue, totalWaitingTime;
    int busyDevices;
    double systemArea = 0.0;
    private ArrayList<Device> parallelDevices;
    private ArrayList<Device> alternativeDevices;
    private TaskQueue queue;
    private ArrayList<NextElement> nextElements;
    private Device deviceServer;
    private Dispose refusalDispose;
    private Dispose finishedDispose;
    private static final Random RNG = new Random(1);
    private double startDelay;
    private boolean creditApproved = false;
    private Role role;

    public SMO(String name, TaskQueue queue, Role _role) {
        setName(name);
        this.queue = queue;
        sumQueue = 0.0;
        nextElements = null;
        tasksEntered = 0;
        startDelay =0;
        parallelDevices = null;
        alternativeDevices = null;
        role = _role;
    }

    @Override
    public void inAct(Task task) {
        if(super.getTcurr() > startDelay) {
            super.inAct(task);
            tasksEntered++;
            //якщо пристрої стоять альтернативно то встановлюємо подальшу дію менеджера
            if(alternativeDevices != null){
                if(!task.isByServerProcessed()){
                    task.setManagerAct(ManagerAct.getInformation);
                }
                else{
                    if (approveCredit()){
                        task.setManagerAct(ManagerAct.giveCreditCard);
                    }
                    else{
                        task.setManagerAct(ManagerAct.refuseCredit);
                    }
                }
                //якщо управляючий вільний, то вибираємо підходящий пристрій для опрацювання завдання
                if(super.getState() == 0){
                    for (Device d : alternativeDevices) {
                        if (d.getDevicePurpose() == task.getManagerAct()) {
                            d.inAct(task);
                            d.setTnext(getTcurr() + d.getDelay());
                            super.setState(1);
                            busyDevices++;
                            if (d.getTnext() <= super.getTnext()) {
                                super.setTnext(d.getTnext());
                            }
                            return;
                        }
                    }
                }
                task.arrivalTime = getTcurr();
                queue.addTask(task);
                return;
            }

            for (Device d : parallelDevices) {
                if (d.getState() == 0) {
                    d.inAct(task);
                    d.setTnext(getTcurr() + d.getDelay());
                    busyDevices++;
                    if (d.getTnext() <= super.getTnext()) {
                        super.setTnext(d.getTnext());
                    }
                    return;
                }
            }
        }
        task.arrivalTime = getTcurr();
        queue.addTask(task);
    }
    @Override
    public void outAct() {
        super.setTnext(Double.MAX_VALUE);
        if(alternativeDevices != null){
            if(deviceServer == null || refusalDispose == null || finishedDispose == null){
                throw new IllegalStateException("Not all elements are set in Manager " + getName());
            }
            for (Device d : alternativeDevices){
                if(d.getState() == 0)
                    continue;
                sendTask(d);
            }
        }
        else if (parallelDevices != null){
            if(getNextElements() == null){
                throw new IllegalStateException("No next element in " + getName());
            }
            for(Device d: parallelDevices){
                if(d.getState() == 0)
                    continue;
                sendTask(d);
            }
        }
    }
    private boolean approveCredit(){
        double r = RNG.nextDouble();
        creditApproved =  r > 0.05;
        return creditApproved;
    }
    private void sendTask(Device device){
        if(device.getTnext() <= super.getTcurr()){
            Task task = device.returnTask();
            if (task == null) {
                throw new IllegalStateException("Device has no task during send");
            }
            Element nextElement = selectNextElement(task);
            sendToNext(device, nextElement, task);
            super.setState(0);
            if (queue.hasTask()){
                startNextFromQueue(device);
            }
        }
        if(device.getTnext() <= super.getTnext()){
            super.setTnext(device.getTnext()); //ставим найближчий час закінчення обробки
        }
    }
    private Element selectNextElement(Task task) {
        if(alternativeDevices != null){
            if(task.getManagerAct() == ManagerAct.getInformation && !task.isByServerProcessed())
                return deviceServer;
            else if (task.getManagerAct() == ManagerAct.giveCreditCard)
                return finishedDispose;
            else if(task.getManagerAct() == ManagerAct.refuseCredit)
                return refusalDispose;
            else{
                throw new IllegalStateException();
            }
        }
        NextElementDecider neDecider = new NextElementDecider();
        return neDecider.ChooseElement(getNextElements(), RNG);
    }
    private void sendToNext(Device d, Element nextElement, Task task) {
        nextElement.inAct(task);
        busyDevices--;
        System.out.println(d.getName() + " sent task to " + nextElement.getName());
        if(alternativeDevices!=null){
            if(!task.isByServerProcessed())
                return;
        }
        super.outAct();
    }
    private void startNextFromQueue(Device d) {
        Task removedTask = queue.removeTask();
        calculateTaskQueueTime(removedTask); // для статистики
        d.setTnext(getTcurr() + d.getDelay());
        busyDevices++;
        System.out.println(d.getName() +
                " takes task, time = " + super.getTcurr());
        d.inAct(removedTask);
    }
    //середній час очікування завдання в черзі
    public void calculateTaskQueueTime(Task task){
        task.serviceStartTime = getTcurr();
        double wait = task.serviceStartTime - task.arrivalTime;
        totalWaitingTime += wait;
        servedByQueueCount++;
    }
    //середня довжина черги
    public void calculateAvgQueueSize(double delta){
        sumQueue = getSumQueue() + queue.getCurrLength() * delta;
    }
    //середня кількість завдань в системі
    public void calculateTaskInSystemCount(double delta){
        int systemSize = queue.getCurrLength() + busyDevices;
        systemArea += systemSize * delta;
    }

    public TaskQueue getQueue(){return queue;}

    public ArrayList<Device> getDevices(){
        if(alternativeDevices != null){
            return alternativeDevices;
        }
        else{
            return parallelDevices;
        }
    }
    public void setNextElements(NextElement... elements){
        nextElements = new ArrayList<>(Arrays.asList(elements));
    }
    public void setNextCreditElements(Device _deviceServer,
                                Dispose _refusalDispose,
                                Dispose _finishedDispose){
        deviceServer = _deviceServer;
        refusalDispose = _refusalDispose;
        finishedDispose = _finishedDispose;
    }
    public void setParallelDevices(Device... parDevices){
        parallelDevices = new ArrayList<>(Arrays.asList(parDevices));
    }
    public void setAlternativeDevices(Device... altDevices){
        alternativeDevices = new ArrayList<>(Arrays.asList(altDevices));
    }
    public void setStartDelay(double delay){startDelay = delay;}
    public double getStartDelay(){return startDelay;}
    public Role getRole(){return role;}
    public ArrayList<NextElement> getNextElements(){
        return this.nextElements;
    }
    //перевірка чи СМО активне
    public boolean isActive(){return super.getTcurr() > startDelay;}
    //statistics
    public double getWaitingTime(){return totalWaitingTime;}
    public int getServedByQueueCount(){return servedByQueueCount;}
    public double getSystemArea(){return systemArea;}
    public int getFailures() {
        return queue.getFailures();
    }
    public int getTasksEntered(){
        return tasksEntered;
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("failure = " + this.getFailures());
        System.out.println("queue = " + queue.getCurrLength());
    }
    @Override
    public void doStatistics(double delta) {
        calculateAvgQueueSize(delta);
        calculateTaskInSystemCount(delta);
        if(alternativeDevices != null){
            for(Device d : alternativeDevices){
                d.doStatistics(delta);
            }
        }
        else if(parallelDevices != null){
            for(Device d : parallelDevices){
                d.doStatistics(delta);
            }
        }
    }
    public double getSumQueue() {
        return sumQueue;
    }
}
