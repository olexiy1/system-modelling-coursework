package bank_model.Delay;

public class ErlangGenerator implements DelayGenerator {
    private double timeMean;
    private int k;
    public ErlangGenerator(double timeMean, int k){
        this.timeMean = timeMean;
        this.k = k;
    }
    public double getDelay(){
        return FunRand.Erlang(timeMean, k);
    }
}
