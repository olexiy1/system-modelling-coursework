package bank_model;

public class TaskTypeObj {
    private int type;
    private double probability;
    TaskTypeObj(int _type, double _probability){
        type = _type;
        probability = _probability;
    }
    public int getType(){return type;}
    public double getProbability(){return probability;}
}
