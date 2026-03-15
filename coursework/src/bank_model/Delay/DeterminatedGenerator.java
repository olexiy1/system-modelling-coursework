package bank_model.Delay;

public class DeterminatedGenerator implements DelayGenerator {
    private double timeDelay;
    public DeterminatedGenerator(double timeDelay){
        this.timeDelay = timeDelay;
    }
    public double getDelay(){
        return timeDelay;
    }
}
