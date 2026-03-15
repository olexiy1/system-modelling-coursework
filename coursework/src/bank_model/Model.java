package bank_model;

import bank_model.Enums.Role;

import java.util.ArrayList;

public class Model {
    private int deviceCount = 0;
    private int carCashiersCount=0;
    private int buildingCashiersCount=0;
    private int managerDevicesCount=0;
    private int wrongQueueCounter=0;
    private int changedPersonType=0;
    private int totalCreated=0;
    private double meanQueueSum = 0.0;
    private double carCashiermeanQueueSum = 0.0;
    private double buildingCashiermeanQueueSum = 0.0;
    private double managerMeanQueueSum = 0.0;
    private double averageWaitingTimeSum=0.0;
    private double carCashierWaitingTimeSum=0.0;
    private double buildingCashierWaitingTimeSum=0.0;
    private double managerWaitingTimeSum =0.0;
    private double busyDevicesTimeSum=0.0;
    private double carCashierBusyDevices=0.0;
    private double buildingCashierBusyDevices=0.0;
    private double managerBusyDevices=0.0;
    private double taskInSystemCountSum=0.0;
    private double avgTaskProcessingTime=0.0;
    private double failureProbSum=0.0;
    private double carCashierfailureProbSum=0.0;
    private double buildingCashierfailureProbSum=0.0;
    private double processedExitIntensity=0.0;
    private double taskBufferSizeSum=0.0;
    private ArrayList<Element> list;
    private ArrayList<Dispose> disposes;
    double tnext, tcurr;
    int event;

    public Model(ArrayList<Element> elements) {
        list = elements;
        tnext = 0.0;
        event = 0;
        tcurr = tnext;
        disposes = new ArrayList<Dispose>();
    }

    public void simulate(double time) {
        while (true) {
            Element eventElement = null;
            tnext = Double.MAX_VALUE;
            for (Element e : list) {
                if (e.getTnext() < tnext) {
                    tnext = e.getTnext();
                    eventElement = e;
                }
            }
            if (tnext > time) {
                break;
            }
            System.out.println("\nIt's time for event in " +
                    eventElement.getName() +
                    ", time = " + tnext);
            for (Element e : list) {
                e.doStatistics(tnext - tcurr);
            }
            tcurr = tnext;
            for (Element e : list) {
                e.setTcurr(tcurr);
            }
            eventElement.outAct();
            printInfo();
        }
        calculateStatistics();
    }
    public void printInfo() {
        for (Element e : list) {
            e.printInfo();
        }
    }

    public void calculateStatistics(){
        int smoCount=0;
        for (Element e : list) {
            e.printResult();
            if (e instanceof SMO) {
                smoCount++;
                SMO smo = (SMO) e;
                meanQueueSum += smo.getSumQueue() / tcurr;
                averageWaitingTimeSum += smo.getWaitingTime() / smo.getServedByQueueCount();
                taskInSystemCountSum += smo.getSystemArea() / tcurr;
                failureProbSum += smo.getQuantity() == 0 ?
                        0:
                        (double)smo.getFailures() / smo.getTasksEntered();
                for(Device d : smo.getDevices()){
                    busyDevicesTimeSum += d.getBusyArea();
                }
                deviceCount += smo.getDevices().size();
            }
            //статистика для касирів що обслуговують авто
            if (e instanceof SMO && ((SMO) e).getRole() == Role.CarCasier) {
                SMO smo = (SMO) e;
                carCashiermeanQueueSum += smo.getSumQueue() / tcurr;
                carCashierWaitingTimeSum += smo.getWaitingTime() / smo.getServedByQueueCount();

                carCashierfailureProbSum += smo.getQuantity() == 0 ?
                        0:
                        (double)smo.getFailures() / smo.getTasksEntered();
                for(Device d : smo.getDevices()){
                    carCashierBusyDevices+= d.getBusyArea();
                }
                carCashiersCount += smo.getDevices().size();
            }
            //статистика для касирів що обслуговують в приміщенні
            if (e instanceof SMO && ((SMO) e).getRole() == Role.BuildingCashier) {
                SMO smo = (SMO) e;
                buildingCashiermeanQueueSum += smo.getSumQueue() / tcurr;
                buildingCashierWaitingTimeSum += smo.getWaitingTime() / smo.getServedByQueueCount();

                buildingCashierfailureProbSum += smo.getQuantity() == 0 ?
                        0:
                        (double)smo.getFailures() / smo.getTasksEntered();
                for(Device d : smo.getDevices()){
                    buildingCashierBusyDevices+= d.getBusyArea();
                }
                buildingCashiersCount += smo.getDevices().size();
            }
            //статистика для менеджера
            if (e instanceof SMO && ((SMO) e).getRole() == Role.Manager) {
                SMO smo = (SMO) e;
                managerMeanQueueSum += smo.getSumQueue() / tcurr;
                managerWaitingTimeSum += smo.getWaitingTime() / smo.getServedByQueueCount();

                for(Device d : smo.getDevices()){
                    managerBusyDevices+= d.getBusyArea();
                }
                //managerDevicesCount += smo.getDevices().size();
            }
            else if (e instanceof Dispose){
                Dispose d = (Dispose) e;
                disposes.add(d);
            }
            else if(e instanceof Create){
                totalCreated += e.getQuantity();
            }
            else if(e instanceof EnterRouter){
                wrongQueueCounter += ((EnterRouter) e).getWrongQueueCounter();
                changedPersonType += ((EnterRouter) e).getChangedPersonType();
            }
            else if(e instanceof Device){
                taskBufferSizeSum += ((Device) e).getBufferSizeSum();
            }
        }
        printResult(smoCount);
    }
    public void printResult(int smoCount) {
        System.out.println("\n-------------RESULTS-------------");
        System.out.println("total people arrived: " + totalCreated);
        System.out.println("people that left car and went into building " + changedPersonType);
        System.out.println("people without car that tried to get into car queue " + wrongQueueCounter);
        System.out.println("mean queue length: " + meanQueueSum / smoCount);
        System.out.println("average car cashier queue length: " + carCashiermeanQueueSum / 2);
        System.out.println("average building cashier queue length: " + buildingCashiermeanQueueSum);
        System.out.println("average manager queue length: " + managerMeanQueueSum);
        System.out.println("average waiting in queue time: " + averageWaitingTimeSum / smoCount);
        System.out.println("average waiting in car queue time: " + carCashierWaitingTimeSum / 2);
        System.out.println("average waiting in building queue time: " + buildingCashierWaitingTimeSum);
        System.out.println("average waiting in manager queue time: " + managerWaitingTimeSum);
        System.out.println("average time worker was busy: " + busyDevicesTimeSum / deviceCount / tcurr);
        System.out.println("average time car cashier was busy: " + carCashierBusyDevices / carCashiersCount / tcurr);
        System.out.println("average time building cashier was busy: " + buildingCashierBusyDevices / buildingCashiersCount / tcurr);
        System.out.println("average time manager was busy: " + managerBusyDevices / tcurr);
        System.out.println("average people in bank amount: " + taskInSystemCountSum / smoCount);
        System.out.println("average people on credit processing at one time: " + taskBufferSizeSum / tcurr);
        System.out.println("average failure probability: " + failureProbSum / smoCount);
        System.out.println("average car cashier failure probability: " + carCashierfailureProbSum / 2);
        System.out.println("average building cashier failure probability: " + buildingCashierfailureProbSum);
        for(Dispose d : disposes){
            avgTaskProcessingTime = d.getTaskProcessingTime() / d.getQuantity();
            System.out.println("average time for task to process for " + d.getName() + ": " + avgTaskProcessingTime);
            processedExitIntensity = d.getQuantity() / tcurr;
            System.out.println("dispose of processed tasks intensity for" + d.getName() + ": " + processedExitIntensity);
        }
    }
}
