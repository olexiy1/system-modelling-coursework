package bank_model.Experiments;

import bank_model.*;
import bank_model.Delay.ExponentialGenerator;
import bank_model.Delay.NormalGenerator;
import bank_model.Delay.TriangularGenerator;
import bank_model.Delay.UniformGenerator;
import bank_model.Enums.ManagerAct;
import bank_model.Enums.QueuePullRule;
import bank_model.Enums.Role;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ExperimentSimModel {
    static List<Double> finalMeans = new ArrayList<>();

    public static void main(String[] args) {
        runWithoutGraph();
    }
    public static void runWithoutGraph(){
        int runs = 1;

        ArrayList<ArrayList<Double>> allTime = new ArrayList<>();
        ArrayList<ArrayList<Double>> allValues = new ArrayList<>();
        for (int i = 0; i < runs; i++) {
            Model model = createModelModification();
            model.simulate(10000.0);
        }
    }
    public static void runWithGraph(){
        int runs = 1;

        ArrayList<ArrayList<Double>> allTime = new ArrayList<>();
        ArrayList<ArrayList<Double>> allValues = new ArrayList<>();
        for (int i = 0; i < runs; i++) {
            Model model = createModelModification();
            model.simulate(10000.0);

            allTime.add(model.getTimeSeries());
            allValues.add(model.getMeanQueueSeries());

            List<Double> series = model.getMeanQueueSeries();
            double lastValue = series.get(series.size() - 1);
            finalMeans.add(lastValue);
        }
        calculateStd();

        ChardDrawerMultiple chart = new ChardDrawerMultiple(
                "Multiple Runs",
                allTime,
                allValues
        );

        chart.setSize(800, 600);
        chart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chart.setVisible(true);
    }

    public static void calculateStd(){
        double mean = 0.0;
        for (double v : finalMeans) {
            mean += v;
        }
        mean /= finalMeans.size();

        double variance = 0.0;

        for (double v : finalMeans) {
            variance += Math.pow(v - mean, 2);
        }

        variance /= (finalMeans.size() - 1);

        double stdDev = Math.sqrt(variance);
        System.out.println("Std queue length: " + stdDev);
        System.out.println("Mean queue length: " + mean);
    }
    public static Model createModel() {
        ExponentialGenerator egC = new ExponentialGenerator(0.75);
        Create carCreator = new Create("CREATOR for car", egC,
                new TaskTypeObj(1, 0.9),
                new TaskTypeObj(2, 0.1));

        ExponentialGenerator egB = new ExponentialGenerator(0.5);
        Create buildingCreator = new Create("CREATOR for building", egB, 2);

        EnterRouter carRouter = new EnterRouter("CarRouter");

        TaskQueue cashier1Q = new TaskQueue(3, QueuePullRule.FIFO);
        SMO cashier1SMO = new SMO("Cashier1SMO", cashier1Q, Role.CarCasier);

        TaskQueue cashier2Q = new TaskQueue(4, QueuePullRule.FIFO);
        SMO cashier2SMO = new SMO("Cashier2SMO", cashier2Q, Role.CarCasier);

        Dispose carDispose = new Dispose("CarExit");

        TaskQueue buildingQ = new TaskQueue(7, QueuePullRule.FIFO);
        SMO buildCashiersSMO = new SMO("BuildingCashiersSMO", buildingQ, Role.BuildingCashier);

        Dispose buildingDispose = new Dispose("BuildingExit");

        NormalGenerator egCashier1 = new NormalGenerator(0.5, 0.25);
        UniformGenerator egCashier2 = new UniformGenerator(0.2, 1);

        cashier1SMO.setParallelDevices(new Device("Cashier1", egCashier1));
        cashier2SMO.setParallelDevices(new Device("Cashier2", egCashier2));

        buildCashiersSMO.setParallelDevices(
                new Device("BuildingCashier1", new TriangularGenerator(0.1, 1.2, 0.4)),
                new Device("BuildingCashier2", new TriangularGenerator(0.1, 1.2, 0.4))
        );
        carCreator.setNextElement(carRouter);
        carRouter.setNextElements(
                new NextElement(cashier1SMO, 0.5),
                new NextElement(cashier2SMO, 0.5),
                new NextElement(buildCashiersSMO));

        cashier1SMO.setNextElements(new NextElement(carDispose));
        cashier2SMO.setNextElements(new NextElement(carDispose));

        buildingCreator.setNextElement(buildCashiersSMO);
        buildCashiersSMO.setNextElements(new NextElement(buildingDispose));

        buildingCreator.setTnext(60);
        ArrayList<Element> list = new ArrayList<>();
        list.add(carCreator);
        list.add(buildingCreator);
        list.add(cashier1SMO);
        list.add(cashier2SMO);
        list.add(buildCashiersSMO);
        list.add(carRouter);
        list.add(carDispose);
        list.add(buildingDispose);

        return new Model(list);
    }

    public static Model createModelModification() {
        ExponentialGenerator egC = new ExponentialGenerator(0.75);
        Create carCreator = new Create("CREATOR for car", egC,
                new TaskTypeObj(1, 0.9),
                new TaskTypeObj(2, 0.1));

        ExponentialGenerator egB = new ExponentialGenerator(0.5);
        Create buildingCreator = new Create("CREATOR for building", egB, 2);

        EnterRouter carRouter = new EnterRouter("CarRouter");

        TaskQueue cashier1Q = new TaskQueue(3, QueuePullRule.FIFO);
        SMO cashier1SMO = new SMO("Cashier1SMO", cashier1Q, Role.CarCasier);

        TaskQueue cashier2Q = new TaskQueue(4, QueuePullRule.FIFO);
        SMO cashier2SMO = new SMO("Cashier2SMO", cashier2Q, Role.CarCasier);

        Dispose carDispose = new Dispose("CarExit");

        TaskQueue buildingQ = new TaskQueue(7, QueuePullRule.FIFO);
        SMO buildCashiersSMO = new SMO("BuildingCashiersSMO", buildingQ, Role.BuildingCashier);

        Dispose buildingDispose = new Dispose("BuildingExit");

        NormalGenerator egCashier1 = new NormalGenerator(0.5, 0.25);
        UniformGenerator egCashier2 = new UniformGenerator(0.2, 1);

        cashier1SMO.setParallelDevices(new Device("Cashier1", egCashier1));
        cashier2SMO.setParallelDevices(new Device("Cashier2", egCashier2));

        buildCashiersSMO.setParallelDevices(
                new Device("BuildingCashier1", new TriangularGenerator(0.1, 1.2, 0.4)),
                new Device("BuildingCashier2", new TriangularGenerator(0.1, 1.2, 0.4))
        );

        TaskQueue mamangerQ = new TaskQueue(QueuePullRule.FIFO);
        SMO manager = new SMO("SMOManager", mamangerQ, Role.Manager);
        Dispose dRefuse = new Dispose("RefuseCreditExit");
        Dispose dSuccess = new Dispose("ApproveCreditExit");
        UniformGenerator gatherInfoDelay = new UniformGenerator(2, 5);
        ExponentialGenerator makeCreditDelay = new ExponentialGenerator(5);
        ExponentialGenerator giveCreditCardDelay = new ExponentialGenerator(1);
        ExponentialGenerator refuseCreditDelay = new ExponentialGenerator(10);
        Device server = new Device("Server", makeCreditDelay, true);

        manager.setAlternativeDevices(new Device("ManagerGatherInfo", gatherInfoDelay, ManagerAct.getInformation),
                new Device("ManagerGiveCredit", giveCreditCardDelay, ManagerAct.giveCreditCard),
                new Device("ManagerRefuseCredit", refuseCreditDelay, ManagerAct.refuseCredit));

        carCreator.setNextElement(carRouter);
        carRouter.setNextElements(
                new NextElement(cashier1SMO, 0.5),
                new NextElement(cashier2SMO, 0.5),
                new NextElement(buildCashiersSMO));

        cashier1SMO.setNextElements(new NextElement(carDispose));
        cashier2SMO.setNextElements(new NextElement(carDispose));

        buildingCreator.setNextElement(buildCashiersSMO);
        buildCashiersSMO.setNextElements(new NextElement(buildingDispose, 0.9), new NextElement(manager, 0.1));

        manager.setNextCreditElements(server, dRefuse, dSuccess);
        server.setNextElement(manager);

        buildingCreator.setTnext(60);

        ArrayList<Element> list = new ArrayList<>();
        list.add(carCreator);
        list.add(buildingCreator);
        list.add(cashier1SMO);
        list.add(cashier2SMO);
        list.add(buildCashiersSMO);
        list.add(carRouter);
        list.add(carDispose);
        list.add(buildingDispose);
        list.add(manager);
        list.add(server);
        list.add(dRefuse);
        list.add(dSuccess);

        return new Model(list);
    }
}
