package bank_model.Delay;

public class TriangularGenerator implements DelayGenerator {
    private double timeMin;
    private double timeMax;
    private double timeMode;
    public TriangularGenerator(double timeMin, double timeMax, double timeMode){
        this.timeMin = timeMin;
        this.timeMax = timeMax;
        this.timeMode = timeMode;
    }
    public double getDelay(){
        return FunRand.Triangular(timeMin, timeMax, timeMode);
    }
}
