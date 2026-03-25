package bank_model;

public class Dispose extends Element {
    private double taskProcessingTime = 0.0;
    public Dispose(String name) {
        super.setName(name);
    }

    @Override()
    public void inAct(Task task) {
        task.setTFinished(getTcurr());
        calculateTaskProcTime(task);
        super.outAct();
    }

    public double getTaskProcessingTime(){return taskProcessingTime;}
    private void calculateTaskProcTime(Task task){
        taskProcessingTime += task.getTFinished() - task.getTCreated();
    }

    @Override()
    public void outAct() {
        throw new UnsupportedOperationException("There is no output in dispose");
    }

    public void printInfo(){
        System.out.println(getName()+ " quantity = "+ getQuantity());
    }
}
