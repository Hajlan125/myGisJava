package sample.substances;

import java.util.HashMap;
import java.util.Map;

public class Chlorine extends Substance{

    Map<Double, Double> currentEquivalents;
    final double k1 = 0.18;
    final double k2 = 0.052;
    final double k3 = 1;
    double h = 0.05;
    double d = 1.553;

    //коэф по температуре
    final double k7 = 1;

    public Chlorine() {
        super();
    }
    public Chlorine(Double currentTon, Integer windSpeed) {
        this.currentTon = currentTon;
        this.windSpeed = windSpeed;
        switch (windSpeed) {
            case (1) -> currentEquivalents = this.equivalentsWindSpeed1;
            case (2) -> currentEquivalents = this.equivalentsWindSpeed2;
            case (3) -> currentEquivalents = this.equivalentsWindSpeed3;
            case (4) -> currentEquivalents = this.equivalentsWindSpeed4;
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

    public void setCurrentEquivalents() {
        switch (windSpeed) {
            case (1) -> currentEquivalents = this.equivalentsWindSpeed1;
            case (2) -> currentEquivalents = this.equivalentsWindSpeed2;
            case (3) -> currentEquivalents = this.equivalentsWindSpeed3;
            case (4) -> currentEquivalents = this.equivalentsWindSpeed4;
            case (5) -> currentEquivalents = this.equivalentsWindSpeed5;
        }
    }
}
