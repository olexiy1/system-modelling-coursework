package bank_model.Delay;

public class UniformGenerator implements DelayGenerator {
    private double timeMin;
    private double timeMax;
    public UniformGenerator(double timeMin, double timeMax){
        this.timeMin = timeMin;
        this.timeMax = timeMax;
    }
    public double getDelay(){
        return FunRand.Unif(timeMin, timeMax);
    }
}
