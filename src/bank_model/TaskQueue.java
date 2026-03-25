package bank_model;

import bank_model.Enums.QueuePullRule;

import java.util.ArrayList;

public class TaskQueue{
    private ArrayList<Task> taskQueuePrior1;
    private ArrayList<Task> taskQueuePrior2;
    private int maxLength;
    private int currLength;
    private int failure;
    private QueuePullRule rule;

    public TaskQueue(QueuePullRule rule){
        maxLength = Integer.MAX_VALUE;
        currLength=0;
        failure=0;
        this.rule = rule;
        taskQueuePrior1 = new ArrayList<Task>();
        taskQueuePrior2 = new ArrayList<Task>();
    }
    public TaskQueue(int maxLength, QueuePullRule rule){
        this.maxLength = maxLength;
        currLength=0;
        failure=0;
        this.rule = rule;
        taskQueuePrior1 = new ArrayList<Task>();
        taskQueuePrior2 = new ArrayList<Task>();
    }

    public void addTask(Task task){
        if(currLength + 1 <= maxLength){
            insertTask(task);
            incrCurrLength();
        }
        else{
            incrFailures();
        }
    }

    public Task removeTask(){
        decrCurrLength();
        if(getRule() == QueuePullRule.FIFO){
            if(!taskQueuePrior1.isEmpty()){
                return taskQueuePrior1.removeFirst();
            }
            else{
                return taskQueuePrior2.removeFirst();
            }
        }

        else{
            if(!taskQueuePrior1.isEmpty()){
                return taskQueuePrior1.removeLast();
            }
            else{
                return taskQueuePrior2.removeLast();
            }
        }

    }
    //Вставити елемент всередину черги якщо інформацію зібрали
    private void insertTask(Task task){
        if(task.isByServerProcessed()){
            taskQueuePrior1.addLast(task);
        }
        else{
            taskQueuePrior2.addLast(task);
        }
    }

    public boolean hasTask(){
        if(taskQueuePrior1.isEmpty() && taskQueuePrior2.isEmpty())
            return false;
        return true;
    }

    public QueuePullRule getRule(){return rule;}
    public int getMaxLength(){
        return maxLength;
    }
    public int getCurrLength(){
        return currLength;
    }
    public void incrCurrLength(){currLength++;}
    public void decrCurrLength(){currLength--;}
    public int getFailures(){return failure;}
    public void incrFailures(){failure++;}

}
