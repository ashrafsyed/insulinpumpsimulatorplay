package lib;

import models.SensorData;
import utils.commons.Enums;

import java.text.DecimalFormat;

public class CarbohydrateModule {

    /**
     * Calculate Duration (in minutes) of Action of Carbohydrate
     * @param glycemicIndex
     * @return duration
     */
    public double actionDuration(Double glycemicIndex){
        double durationMinutes = 0;
        if (glycemicIndex >0 && glycemicIndex < 20){
            durationMinutes = 150;
        } else if (glycemicIndex > 20 && glycemicIndex < 90){
            durationMinutes = 180 - 1.5 * glycemicIndex;
        } else if (glycemicIndex > 90) {
            durationMinutes = 45;
        }
        return durationMinutes;
    }

    /**
     * This method calculate Glucose Sensitivity.
     * @param bodyWeight
     * @return
     */
    public static double glucoseSensitivity(Double bodyWeight){
        double glucoseSensitivity = 0.00;
        if (bodyWeight >0 && bodyWeight <= 16){
            glucoseSensitivity = (1/bodyWeight)*17.76;
        } else if (bodyWeight > 16 && bodyWeight <= 32){
            glucoseSensitivity = (bodyWeight/175)-0.133;
        } else if (bodyWeight > 32 && bodyWeight <= 48){
            glucoseSensitivity = 0.9 - (bodyWeight * 0.010625);
        } else if (bodyWeight > 48 && bodyWeight <= 64){
            glucoseSensitivity = 0.72 - (bodyWeight * 0.006875);
        } else if (bodyWeight > 64 && bodyWeight <= 80){
            glucoseSensitivity = 0.52 - (bodyWeight * 0.00375);
        } else if (bodyWeight > 80 && bodyWeight <= 95){
            glucoseSensitivity = 0.433 - (bodyWeight * 0.000267);
        } else if (bodyWeight > 95 && bodyWeight <= 111){
            glucoseSensitivity = 0.239375 - (bodyWeight * 0.000625);
        } else if (bodyWeight > 111 && bodyWeight <= 128){
            //TODO tally once again
            glucoseSensitivity = 0.36588235294118 - (bodyWeight * 0.0017647058823529);
        }else if (bodyWeight > 111 && bodyWeight <= 128){
            glucoseSensitivity = 0.36588235294118 - (bodyWeight * 0.0017647058823529);
        } else if (bodyWeight > 128){
            glucoseSensitivity = (1/bodyWeight)*17.1;
        }

        return glucoseSensitivity;
    }

    /**
     * Calculates Carbohydrates Influence at any given time T
     * @param time
     * @param actionDuration
     * @return
     */
    public double carbInfluence(int time, double actionDuration){
        double influence = 0.00;
        influence = (Math.cos(((time/19) / (actionDuration/60)) - 3.14))/2 + 0.5;
        return influence;
    }

    /**
     * This module calculates the rise in BGL because of carbs intake at some point of time.
     * The unit of output is mmol/L.
     * @param carbs
     * @param glycemicIndex
     * @param time
     * @param carbSensitivity
     * @return  riseBGL
     */
    public static double riseInBGL(double carbs, double glycemicIndex, Integer time, double carbSensitivity) {
        CarbohydrateModule module = new CarbohydrateModule();
        DecimalFormat df = new DecimalFormat("#.##");
        double riseBGL = 0.00;
        double actionDurationValue = module.actionDuration(glycemicIndex);
        double carbInfluence =  module.carbInfluence(time, actionDurationValue);

        if(time<actionDurationValue)
            riseBGL = ((carbs * glycemicIndex)/100) * carbInfluence/(100*carbSensitivity);
        else riseBGL = 0;
        return Double.valueOf(df.format(riseBGL));
    }
}
