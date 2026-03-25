package bank_model;

import bank_model.Enums.Role;

import java.util.ArrayList;
import org.jfree.chart.ChartFactory;

public class Model {
    private int deviceCount = 0;
    private int carCashiersCount=0;
    private int buildingCashiersCount=0;
    private int wrongQueueCounter=0;
    private int changedPersonType=0;
    private int totalCreated=0;
    private double meanQueueSum = 0.0;
    private double meanQueueSumV = 0.0;
    private double meanQueueV=0.0;
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
    private double buildingCashierfailureProbSum=0.0;
    private double taskBufferSizeSum=0.0;
    private ArrayList<Element> list;
    double tnext, tcurr;
    int event;
    private boolean showStatistic = false;
    private boolean performExperiment = true;
    private ArrayList<Double> timeSeries = new ArrayList<>();
    private ArrayList<Double> meanQueueSeries = new ArrayList<>();
    private double transitionTimeEnd = 2500;

    public Model(ArrayList<Element> elements) {
        list = elements;
        tnext = 0.0;
        event = 0;
        tcurr = tnext;
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
            if(showStatistic) {
                System.out.println("\nIt's time for event in " +
                        eventElement.getName() +
                        ", time = " + tnext);
            }
            if(tcurr > transitionTimeEnd){
                for (Element e : list) {
                    e.doStatistics(tnext - tcurr);
                }
            }
            tcurr = tnext;
            for (Element e : list) {
                e.setTcurr(tcurr);
            }
            eventElement.outAct();
            if(showStatistic)
                printInfo();

//            if(performExperiment && tcurr > transitionTimeEnd) {
//                calculateMeanQueueLength();
//            }
        }
        //calculateMeanQueueLength();
        //calculateStatistics();
        calculateMeanQueueStatistics();
    }
    public void printInfo() {
        for (Element e : list) {
            e.printInfo();
        }
    }
    public void calculateMeanQueueLength() {
        int smoCount=0;
        for (Element e : list) {
            if (e instanceof SMO) {
                smoCount++;
                SMO smo = (SMO) e;
                meanQueueSumV += smo.getSumQueue() / tcurr;
            }
        }
        meanQueueV =  meanQueueSumV / smoCount;
        meanQueueSumV = 0.0;
        timeSeries.add(tcurr);
        meanQueueSeries.add(meanQueueV);
    }
    public void calculateMeanQueueStatistics() {
        int smoCount=0;
        for (Element e : list) {
            if (e instanceof SMO) {
                smoCount++;
                SMO smo = (SMO) e;
                meanQueueSumV += smo.getSumQueue() / (tcurr-transitionTimeEnd);
            }
        }
        meanQueueV =  meanQueueSumV / smoCount;
        if(performExperiment)
            printVidguk();
    }

    public void calculateStatistics(){
        int smoCount=0;
        int failureSmoCount=0;
        int totalExistQuantity=0;
        for (Element e : list) {
            if(showStatistic)
                e.printResult();
            if (e instanceof SMO) {
                smoCount++;
                SMO smo = (SMO) e;
                meanQueueSum += smo.getSumQueue() / tcurr;
                averageWaitingTimeSum += smo.getWaitingTime() / smo.getServedByQueueCount();
                taskInSystemCountSum += smo.getSystemArea() / tcurr;
                if(smo.getRole() != Role.Manager){
                    failureSmoCount++;
                    failureProbSum += smo.getQuantity() == 0 ?
                            0:
                            (double)smo.getFailures() / smo.getTasksEntered();
                }
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
                managerBusyDevices+= smo.getBusyArea();
            }
            else if (e instanceof Dispose){
                Dispose d = (Dispose) e;
                avgTaskProcessingTime += d.getTaskProcessingTime();
                totalExistQuantity += d.getQuantity();
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
        if(showStatistic)
            printResult(smoCount, failureSmoCount, totalExistQuantity);

    }
    public void printResult(int smoCount, int failureSmoCount, int totalExistQuantity) {
        System.out.println("\n-------------RESULTS-------------");
        System.out.println("total_arrived: " + totalCreated);
        System.out.println("left_car_count " + changedPersonType);
        System.out.println("tryied_enter_wrong_queue_count " + wrongQueueCounter);
        System.out.println("avg_queue_lgth: " + meanQueueSum / smoCount);
        System.out.println("avg_car_queue_lgth: " + carCashiermeanQueueSum / 2);
        System.out.println("avg_build_queue_lgth: " + buildingCashiermeanQueueSum);
        System.out.println("avg_manager_queue_lgth: " + managerMeanQueueSum);
        System.out.println("avg_waiting_queue_time: " + averageWaitingTimeSum / smoCount);
        System.out.println("avg_waiting_car_queue_time: " + carCashierWaitingTimeSum / 2);
        System.out.println("avg_waiting_build_queue_time: " + buildingCashierWaitingTimeSum);
        System.out.println("avg_waiting_manager_queue_time: " + managerWaitingTimeSum);
        System.out.println("avg_time_worker_busy: " + busyDevicesTimeSum / deviceCount / tcurr);
        System.out.println("avg_time_car_cashier_busy: " + carCashierBusyDevices / carCashiersCount / tcurr);
        System.out.println("avg_time_build_cashier_busy: " + buildingCashierBusyDevices / buildingCashiersCount / tcurr);
        System.out.println("avg_time_manager_busy: " + managerBusyDevices / tcurr);
        System.out.println("avg_people_in_bank: " + taskInSystemCountSum / smoCount);
        System.out.println("avg_people_credit_process_at_1_time: " + taskBufferSizeSum / tcurr);
        System.out.println("avg_failure_prob: " + failureProbSum / failureSmoCount);
        System.out.println("avg_build_cashier_failure_prob: " + buildingCashierfailureProbSum);
        System.out.println("avg_time_person_spend_in_bank: " + avgTaskProcessingTime / totalExistQuantity);
    }

    public void printVidguk(){
        System.out.println("avg_queue_lgth: " + meanQueueV);
    }

    public ArrayList<Double> getTimeSeries() {
        return timeSeries;
    }

    public ArrayList<Double> getMeanQueueSeries() {
        return meanQueueSeries;
    }
}
