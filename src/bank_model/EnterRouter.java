package bank_model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class EnterRouter extends Element {
    private NextElement carCashier1;
    private NextElement carCashier2;
    private NextElement buildingCashiers;
    private TaskQueue cashier1Q;
    private TaskQueue cashier2Q;
    private static final Random RNG = new Random(1);

    //використати для статистики
    private int wrongQueueCounter;
    private int changedPersonType;

    public EnterRouter(String name){
        super(name, null);
        wrongQueueCounter = 0;
        changedPersonType = 0;
    }

    @Override
    public void inAct(Task task){
        if(carCashier1 == null || carCashier2 == null || buildingCashiers == null)
            throw new IllegalStateException("next elements are not set up in router");
        if(task.getType() == 1){
            if(cashier1Q.getCurrLength() == cashier1Q.getMaxLength() &&
                    cashier2Q.getCurrLength() == cashier2Q.getMaxLength()){
                //змінюємо тип людини на пішохода
                task.setType(2);
                changedPersonType++;
                buildingCashiers.getElement().inAct(task);
            }
            //якщо черга каси 1 заповнена то людина їде до каси 2
            else if(cashier1Q.getCurrLength() == cashier1Q.getMaxLength()){
                carCashier2.getElement().inAct(task);
            }
            //якщо черга каси 2 заповнена то людина їде до каси 1
            else if(cashier2Q.getCurrLength() == cashier2Q.getMaxLength()){
                carCashier1.getElement().inAct(task);
            }
            else{
            NextElementDecider neDecider = new NextElementDecider();
            Element elementToSend =
                    neDecider.ChooseElement(new ArrayList<NextElement>(Arrays.asList(carCashier1,carCashier2)), RNG);
            elementToSend.inAct(task);
            }
            super.outAct();
        }
        else{
            wrongQueueCounter++;
            buildingCashiers.getElement().inAct(task);
            super.outAct();
        }
    }

    public void setNextElements(NextElement _carCashier1, NextElement _carCashier2, NextElement _buildingCashiers){
        this.carCashier1 = _carCashier1;
        this.carCashier2 = _carCashier2;
        this.buildingCashiers = _buildingCashiers;
        setQueues();
    }

    public void setQueues(){
        SMO bankCasse1 = (SMO)carCashier1.getElement();
        cashier1Q = bankCasse1.getQueue();
        SMO bankCasse2 = (SMO)carCashier2.getElement();
        cashier2Q = bankCasse2.getQueue();
    }
    public int getWrongQueueCounter(){return wrongQueueCounter;}
    public int getChangedPersonType(){return changedPersonType;}
    @Override
    public void doStatistics(double delta) {

    }
}
