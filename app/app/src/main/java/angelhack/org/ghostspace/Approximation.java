package angelhack.org.ghostspace;

public class Approximation {

    public static int DEVIDER = 1;

    public static double approximate(double x){
        return ivanFunction(x);
    }

    protected static double ivanFunction(double rssi){
        double a = 30;
        double b = 10;
        double c = 20;

        double ps = Math.abs(rssi)-a;
        double pow1 = ps/b;
        //double pow2 = ps/c;
        double result = Math.pow(2, pow1);//+Math.pow(3,pow2);
        return result;
    }

    protected static double getRange(int txCalibratedPower, double rssi) {
        double ratio_db = txCalibratedPower - rssi;
        double ratio_linear = Math.pow(10, ratio_db / 10);

        double r = Math.sqrt(ratio_linear);
        return r;
    }

    protected static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0;
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }
}
