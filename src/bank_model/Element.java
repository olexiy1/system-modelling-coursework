package bank_model;

import bank_model.Delay.DelayGenerator;

public abstract class Element {
    private String name;
    private double tnext;
    private int quantity;
    private double tcurr;
    private int state;
    private Element nextElement;
    private static int nextId=0;
    private int id;
    public DelayGenerator delayGen;

    public Element(){
        tnext = Double.MAX_VALUE;
        tcurr = tnext;
        state=0;
        nextElement = null;
        id = nextId;
        nextId++;
        name = "element"+id;
    }

    public Element(String name){
        tnext = Double.MAX_VALUE;
        tcurr = tnext;
        state=0;
        nextElement = null;
        id = nextId;
        nextId++;
        this.name = name;
    }

    public Element(String nameOfElement, DelayGenerator delayGen){
        name = nameOfElement;
        tnext = Double.MAX_VALUE;
        tcurr = tnext;
        state=0;
        nextElement=null;
        id = nextId;
        nextId++;
        this.delayGen = delayGen;
    }
    public double getDelay(){
        if(delayGen != null)
            return delayGen.getDelay();
        else
            throw new IllegalStateException("The delay generator is not set");
    }

    public Element getNextElement() {
        return nextElement;
    }
    public void setNextElement(Element nextElement) {
        this.nextElement = nextElement;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTcurr() {
        return tcurr;
    }
    public void setTcurr(double tcurr) {
        this.tcurr = tcurr;
    }
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }

    public void inAct(Task task) {
    }
    public void outAct(){
        quantity++;
    }

    public double getTnext() {
        return tnext;
    }
    public void setTnext(double tnext) {
        this.tnext = tnext;
    }
    public void printResult(){
        System.out.println(getName()+ " quantity = "+ quantity);
    }

    public void printInfo(){
        System.out.println(getName()+ " state= " +state+
                " quantity = "+ quantity+
                " tnext= "+tnext);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void doStatistics(double delta){

    }
}