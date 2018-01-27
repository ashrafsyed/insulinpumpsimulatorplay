package lib;

import java.text.DecimalFormat;

public class InsulinModule {
    public static final double INSULIN_PEAK_VALUE = 32.50;

    public double coefficient(double bodyWeight, double insulinUnit){
        double coefficientValue = 0.0;
        coefficientValue = (insulinUnit / bodyWeight) * INSULIN_PEAK_VALUE;
        return  coefficientValue;
    }

    public double multiplier(double bodyWeight, double bloodVolume){
        double multiplierValue = 0.00;
        multiplierValue = 0.16 + (0.55 *bloodVolume)/bodyWeight;
        return multiplierValue;
    }

    public double insulinInfluence(Integer time){
        double insulinInfluenceValue = 0.00;
        if (time >= 0 && time <= 100){
            insulinInfluenceValue = (0.0109*time) + (0.0463);
        } else if (time > 100 && time <= 160){
            insulinInfluenceValue = (1.3) - (0.00314*time);
        } else if (time > 160 && time <= 280){
            insulinInfluenceValue = (1.47) - (0.005*time);
        } else if (time > 280){
            insulinInfluenceValue = (0.218) - (0.0006*time);
        }
        return insulinInfluenceValue;
    }

    /**
     * Compute Insulin Dose before every iteration.
     * @param carbs
     * @param glycemicIndex
     * @param glucoseSensitivity
     * @param startBgl
     * @param targetBgl
     * @return
     */
    public static double computeInsulinDose(double carbs, double glycemicIndex, double glucoseSensitivity,
                                            double startBgl, double targetBgl){
        double insulinDose = 0.00;
        double bglRiseDueToCarb = 0.00;
        double correctionFactor = 0.00;

        bglRiseDueToCarb = ((glycemicIndex*carbs*glucoseSensitivity)/100)*18; //multiplying by 18 to convert mmol to mg/dl
        if (startBgl >= targetBgl){
            correctionFactor=startBgl-targetBgl;
        } else {
            correctionFactor=(-1*(targetBgl-startBgl));
        }
        insulinDose = (bglRiseDueToCarb + correctionFactor)/25;

        return insulinDose; //TODO check if calculated is greater than recommended i.e. BolusMax;
    }

    public static double changeInBgl(Integer time, double insulinUnit, double bodyWeight, double bloodVolume){
        Double changeInBglValue = 0.00;
        DecimalFormat df = new DecimalFormat("#.##");
        InsulinModule insulinModule = new InsulinModule();
        double coeff = insulinModule.coefficient(bodyWeight, insulinUnit);
        double influence = insulinModule.insulinInfluence(time);
        double multiplier = insulinModule.multiplier(bodyWeight, bloodVolume);
        changeInBglValue = (influence * coeff * bodyWeight * multiplier)/(180.15588 * bloodVolume);

        return Double.valueOf(df.format(changeInBglValue));
    }
}
