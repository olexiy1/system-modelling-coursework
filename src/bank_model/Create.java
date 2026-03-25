package bank_model;

import bank_model.Delay.DelayGenerator;

import java.util.Random;

public class Create extends Element {
    TaskTypeObj taskTypeObj1;
    TaskTypeObj taskTypeObj2;
    private int taskType;
    private static final Random RNG = new Random(1);

    public Create(String name, DelayGenerator generator) {
        super(name, generator);
        super.setTnext(0.0);
        this.taskType = -1;
    }
    public Create(String name, DelayGenerator generator, int taskType) {
        super(name, generator);
        super.setTnext(0.0);
        this.taskType = taskType;
    }
    public Create(String name, DelayGenerator generator, TaskTypeObj _taskTypeObj1, TaskTypeObj _taskTypeObj2) {
        super(name, generator);
        super.setTnext(0.0);
        taskTypeObj1 = _taskTypeObj1;
        taskTypeObj2 = _taskTypeObj2;
    }
    @Override
    public void outAct() {
        if(taskTypeObj1 != null && taskTypeObj2 != null){
            taskType = DecideTaskType(taskTypeObj1, taskTypeObj2);
        }
        Task task = new Task(getTcurr(), taskType);
        super.outAct();
        super.setTnext(getTcurr() + getDelay());
        super.getNextElement().inAct(task);
    }

    private int DecideTaskType(TaskTypeObj taskTypeObj1, TaskTypeObj taskTypeObj2){
        double r = RNG.nextDouble();
        if(r < taskTypeObj1.getProbability())
            return taskTypeObj1.getType();
        else
            return taskTypeObj2.getType();
    }

    public void setTNext(double t){super.setTnext(t);}
}
