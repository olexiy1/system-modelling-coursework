package bank_model.Delay;

public class ExponentialGenerator implements DelayGenerator {
    private double timeMean;
    public ExponentialGenerator(double timeMean){
        this.timeMean = timeMean;
    }
    public double getDelay(){
        return FunRand.Exp(timeMean);
    }
}
