package sample.substances;

import java.util.*;

public abstract class Substance {

    int windSpeed;
    double currentTon;
    int weight = 30;

    //время от начал аварии
    Integer N = 2;

    ArrayList<Double> list = new ArrayList<>() {{add(1.0);add(0.23);add(0.08);}};

//    double k4 = 1.67;


//    double k6 = 1;

    public double K4(Integer windSpeed) {
     return 1 + (0.33 * (windSpeed - 1));
    }

    //list.get(new Random().nextInt(list.size()))
    double k5 = 1;

    double h = 0.05;


    Map<Double, Double> equivalents = new HashMap<Double, Double>() {{
        put(0.01, 0.22);put(0.05, 0.48);put(0.1, 0.68);put(0.5, 1.53);put(1.0, 2.17);
        put(3.0, 3.99);put(5.0, 5.34);put(10.0, 7.96);put(20.0, 11.94);put(30.0, 15.18);
        put(50.0, 20.59);put(70.0, 25.21);put(100.0, 31.30);put(300.0,61.47);put(500.0, 84.50);
        put(700.0, 104.0); put(1000.0, 130.0);put(2000.0, 202.0);
    }};
    Map<Double, Double> equivalentsWindSpeed1 = new HashMap<Double, Double>() {{
        put(0.01, 0.38);put(0.05, 0.85);put(0.1, 1.25);put(0.5, 3.16);put(1.0, 4.75);
        put(3.0, 9.18);put(5.0, 12.53);put(10.0, 19.20);put(20.0, 29.56);put(30.0, 38.13);
        put(50.0, 52.67);put(70.0, 65.23);put(100.0, 81.91);put(300.0, 166.0);put(500.0, 231.0);
        put(700.0, 288.0); put(1000.0, 363.0);put(2000.0, 572.0);
    }};
    Map<Double, Double> equivalentsWindSpeed2 = new HashMap<Double, Double>() {{
        put(0.01, 0.26);put(0.05, 0.59);put(0.1, 0.84);put(0.5, 1.92);put(1.0, 2.84);
        put(3.0, 5.35);put(5.0, 7.20);put(10.0, 10.83);put(20.0, 16.44);put(30.0, 21.02);
        put(50.0, 28.73);put(70.0, 35.35);put(100.0, 44.09);put(300.0, 87.79);put(500.0, 121.0);
        put(700.0, 150.0); put(1000.0, 189.0);put(2000.0, 295.0);
    }};
    Map<Double, Double> equivalentsWindSpeed3 = new HashMap<Double, Double>() {{
        put(0.01, 0.22);put(0.05, 0.48);put(0.1, 0.68);put(0.5, 1.53);put(1.0, 2.17);
        put(3.0, 3.99);put(5.0, 5.34);put(10.0, 7.96);put(20.0, 11.94);put(30.0, 15.18);
        put(50.0, 20.59);put(70.0, 25.21);put(100.0, 31.30);put(300.0,61.47);put(500.0, 84.50);
        put(700.0, 104.0); put(1000.0, 130.0);put(2000.0, 202.0);
    }};
    Map<Double, Double> equivalentsWindSpeed4 = new HashMap<Double, Double>() {{
        put(0.01, 0.19);put(0.05, 0.42);put(0.1, 0.59);put(0.5, 1.33);put(1.0, 1.88);
        put(3.0, 3.28);put(5.0, 4.36);put(10.0, 6.46);put(20.0, 9.62);put(30.0, 12.18);
        put(50.0, 16.43);put(70.0, 20.05);put(100.0, 24.80);put(300.0, 48.18);put(500.0, 65.92);
        put(700.0, 81.17); put(1000.0, 101.0);put(2000.0, 157.0);
    }};
    Map<Double, Double> equivalentsWindSpeed5 = new HashMap<Double, Double>() {{
        put(0.01, 0.17);put(0.05, 0.38);put(0.1, 0.53);put(0.5, 1.19);put(1.0, 1.68);
        put(3.0, 2.91);put(5.0, 3.75);put(10.0, 5.53);put(20.0, 8.19);put(30.0, 10.33);
        put(50.0, 13.88);put(70.0, 16.89);put(100.0, 20.82);put(300.0, 40.11);put(500.0, 54.67);
        put(700.0, 67.15); put(1000.0, 83.60);put(2000.0, 129.0);
    }};
    Map<Integer, Integer> v = new HashMap<>() {{
        put(1, 5); put(2, 10); put(3, 16); put(4, 21);
    }};

    double[] ton = new double[] {0.01, 0.05, 0.1, 0.5, 1, 3, 5, 10, 20, 30, 50, 70,
            100, 300, 500, 700, 1000, 2000};

    public Substance() {

    }
    public Substance(Double currentTon, Integer windSpeed) {
        this.windSpeed = windSpeed;
        this.currentTon = currentTon;
    }

    public double findLeft(double value) {
        for (int i = 1; i < ton.length; i++) {
            if(ton[i] > value) {
                return ton[i-1];
            }
        }
        return 0;
    }
    public double findRight(double value) {
        for (double v : ton) {
            if (v > value) {
                return v;
            }
        }
        return 0;
    }

    public double T() {
        return 0;
    }

    public double Qe() {
        return 0;
    }

    public double G() {
        return 0;
    }

    public double Gp() {
        return 0;
    }

    public void setCurrentTon(double currentTon) {
        this.currentTon = currentTon;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }
}
