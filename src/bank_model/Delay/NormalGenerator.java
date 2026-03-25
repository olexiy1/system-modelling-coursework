package bank_model.Delay;

public class NormalGenerator implements DelayGenerator {
    private double timeMean;
    private double timeDeviation;
    public NormalGenerator(double timeMean, double timeDeviation){
        this.timeMean = timeMean;
        this.timeDeviation = timeDeviation;
    }
    public double getDelay(){
        return FunRand.Norm(timeMean, timeDeviation);
    }
}
