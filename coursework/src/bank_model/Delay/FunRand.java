package bank_model.Delay;

import java.util.Random;

public class FunRand {
    /**
     * Generates a random value according to an exponential
     distribution
     *
     * @param timeMean mean value
     * @return a random value according to an exponential
    distribution
     */
    public static double Exp(double timeMean) {
        double a = 0;
        while (a == 0) {
            a = Math.random();
        }
        a = -timeMean * Math.log(a);
        return a;
    }
    /**
     * Generates a random value according to a uniform
     distribution
     *
     * @param timeMin
     * @param timeMax
     * @return a random value according to a uniform distribution
     */
    public static double Unif(double timeMin, double timeMax) {
        double a = 0;
        while (a == 0) {
            a = Math.random();
        }
        a = timeMin + a * (timeMax - timeMin);
        return a;
    }
    /**
     * Generates a random value according to a normal (Gauss)
     distribution
     *
     * @param timeMean
     * @param timeDeviation
     * @return a random value according to a normal (Gauss)
    distribution
     */
    public static double Norm(double timeMean, double
            timeDeviation) {
        double a;
        Random r = new Random();
        a = timeMean + timeDeviation * r.nextGaussian();
        return a;
    }
    /**
     * Generates a random value according to an Erlang
     distribution
     *
     * @param timeMean
     * @param k
     * @return a random value according to an Erlang
    distribution
     */
    public static double Erlang(double timeMean, int k) {
        double lambda = k / timeMean;

        double product = 1.0;

        for (int i = 0; i < k; i++) {
            double a = 0;
            while (a == 0) {
                a = Math.random();
            }
            product *= a;
        }

        return -Math.log(product) / lambda;
    }
    /**
     * Generates a random value according to a Triangular
     distribution
     *
     * @param timeMin
     * @param timeMax
     * @param timeMode
     * @return a random value according to a Triangular
    distribution
     */
    public static double Triangular(double timeMin, double timeMax, double timeMode) {

        double u = 0;
        while (u == 0) {
            u = Math.random();
        }

        double Fc = (timeMode - timeMin) / (timeMax - timeMin);

        if (u < Fc) {
            return timeMin + Math.sqrt(u * (timeMax - timeMin) * (timeMode - timeMin));
        } else {
            return timeMax - Math.sqrt((1 - u) * (timeMax - timeMin) * (timeMax - timeMode));
        }
    }

}
