package sample.substances;

import java.util.Map;

public class Ftor extends Substance{

    Map<Double, Double> currentEquivalents;
    final double k1 = 0.95;
    final double k2 = 0.038;
    final double k3 = 3.0;
    final double k7 = 0.9;
    double h = 0.05;
    double d = 1.512;

    public Ftor() {
        super();
    }
    public Ftor(Double currentTon, Integer windSpeed) {
        this.currentTon = currentTon;
        this.windSpeed = windSpeed;
        switch (windSpeed) {
            case (1) -> {
                currentEquivalents = this.equivalentsWindSpeed1;
                fi = 180;
            }
            case (2) -> {
                currentEquivalents = this.equivalentsWindSpeed2;
                fi = 90;
            }
            case (3) -> {
                currentEquivalents = this.equivalentsWindSpeed3;
                fi = 45;
            }
            case (4) -> {
                currentEquivalents = this.equivalentsWindSpeed4;
                fi = 45;
            }
        }
    }

    @Override
    public double T() {
        return (h*d)/(k2 * K4(windSpeed) * k7);
    }

    @Override
    public double Qe() {
        return 20 * K4(windSpeed) * k5 * (k2 * k3 * Math.pow(T(), 0.8) * k7 * (currentTon / d));
    }

    @Override
    public double G() {
        double left = findLeft(Qe());
        double right = findRight(Qe());

        return currentEquivalents.get(left)
                + ( (currentEquivalents.get(right) - currentEquivalents.get(left)) / (right - left) )
                * (Qe() - left);
    }

    @Override
    public double Gp() {
        Integer GPredel = N * v.get(windSpeed);
        Double GCurrent = G();
        if (GPredel > GCurrent) return GCurrent;
        else return GPredel;
    }
    @Override
    public double PossibleSquare() {
        double gp = Gp();
        return 8.72 * Math.pow(10, -3)*gp*gp*fi;
    }
    @Override
    public double FactSquare() {
        double gp = Gp();
        return k8*gp*gp*Math.pow(N, 0.2);
    }

}
