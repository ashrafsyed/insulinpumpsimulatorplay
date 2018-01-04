package controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lib.CarbohydrateModule;
import lib.InsulinModule;
import lib.NexmoSMS;
import lib.SimpleEmailSender;
import models.DeviceConfig;
import models.Patient;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Controller;
import play.mvc.Result;
import utils.commons.Enums;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Simulator extends Controller {

    public Result runSimulator() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();
        List<Double> bglMapList = new ArrayList<>();
        Double[] carbsArray = new Double[3];
        Double[] glycemicIndexArray = new Double[3];
        Map<String, Object> data = gson.fromJson(request().body().asJson().toString(), new TypeToken<Map<String, Object>>() {
        }.getType());

        String deviceMode = (String) data.get("deviceMode");
        if (StringUtils.isNotEmpty(deviceMode) && 0==deviceMode.compareToIgnoreCase(Enums.deviceMode.AUTO.value)){
            Double startBgl = (Double) data.get("startBgl");
            carbsArray[0] = (Double) data.get("cho1");
            carbsArray[1] = (Double) data.get("cho2");
            carbsArray[2] = (Double) data.get("cho3");
            glycemicIndexArray[0] = (Double) data.get("breakfastGI");
            glycemicIndexArray[1] = (Double) data.get("lunchGI");
            glycemicIndexArray[2] = (Double) data.get("dinnerGI");
            String exercise = (String) data.get("exercise");
            String deviceId = (String) data.get("deviceId");
            String patientId = (String) data.get("patientId");
            Integer duration = ((Double) data.get("duration")).intValue();
            bglMapList = startSimulation(startBgl, carbsArray, glycemicIndexArray, deviceId, patientId, Enums.deviceMode.AUTO);

            DeviceConfig config = DeviceConfig.byIds(deviceId,patientId);

            resMap.put("batteryStatus", config.batteryLevel);   //TODO reduce battery and insulin level after simulation execution
            resMap.put("insulinStatus", config.insulinLevel);
            resMap.put("glucagonStatus", config.glucagonLevel);
            resMap.put("status","success");
            resMap.put("bglData",bglMapList);
        } else {
            Double startBgl = (Double) data.get("startBgl");
            Double manualModeCHO = (Double) data.get("manualModeCHO");
            Double manualModeGI = (Double) data.get("manualModeGI");
            Double manualModeInsulinUnit = (Double) data.get("manualModeInsulinUnit");
            String exercise = (String) data.get("exercise");
            String deviceId = (String) data.get("deviceId");
            String patientId = (String) data.get("patientId");
            Integer duration = ((Double) data.get("duration")).intValue();
            bglMapList = startSimulation(startBgl, carbsArray, glycemicIndexArray, deviceId, patientId, Enums.deviceMode.MANUAL);

            DeviceConfig config = DeviceConfig.byIds(deviceId,patientId);

            resMap.put("batteryStatus", config.batteryLevel);   //TODO reduce battery and insulin level after simulation execution
            resMap.put("insulinStatus", config.insulinLevel);
            resMap.put("glucagonStatus", config.glucagonLevel);
            resMap.put("status","success");
            resMap.put("bglData",bglMapList);
        }

        return ok(gson.toJson(resMap)).as("application/json");
    }

    public List<Double> startSimulation(double startBgl, Double[] carbsArray, Double[] glycemicIndex, String deviceId, String patientId, Enums.deviceMode deviceMode){
        List<Double> bglList = new ArrayList<>();
        double computedInsulin = 0.00;
        Patient patient = Patient.byIds(deviceId, patientId);
        DeviceConfig config = DeviceConfig.byIds(deviceId, patientId);

        DecimalFormat df = new DecimalFormat("#.##");

        if (deviceMode.value=="AUTO"){
            Double startBglForIteration = startBgl;
            for (int i= 0; i<carbsArray.length; i++) {
                List<Double> bglIterationValue = new ArrayList<>();
                computedInsulin = InsulinModule.computeInsulinDose(carbsArray[i], glycemicIndex[i], patient.glucoseSensitivity,
                        startBglForIteration, config.targetBgl);
                bglIterationValue = bglIterator(startBglForIteration, computedInsulin, carbsArray[i], glycemicIndex[i], patient.patientWeight, patient.bloodVolume,
                        patient.glucoseSensitivity);
                startBglForIteration = bglIterationValue.get(bglIterationValue.size()-1);
                bglList.addAll(bglIterationValue);
            }
        }

/*TODO
        if (deviceMode.value=="MANUAL"){
            computedInsulin = InsulinModule.computeInsulinDose(breakfastCHO, breakfastGI, patient.glucoseSensitivity,
                    startBgl, config.targetBgl);
            bglList = bglIterator(startBgl, computedInsulin, breakfastCHO, breakfastGI, patient.patientWeight, patient.bloodVolume,
                    patient.glucoseSensitivity);
        }

*/
    return bglList;
    }


    public static List<Double> bglIterator (Double currentBgl, Double insulin, Double carbs, Double gI, Double weight, Double bloodVol,
                                    Double glucoseSensitivity){
        Double insulinChangeInBgl; Double riseInBglCarb;
        List<Double> bglList = new ArrayList<>();
        for (Integer i=0; i<360; i++){
            insulinChangeInBgl = 0.00; riseInBglCarb = 0.00;
            insulinChangeInBgl = InsulinModule.changeInBgl(i, insulin, weight, bloodVol);
            riseInBglCarb = CarbohydrateModule.riseInBGL(carbs, gI, i, glucoseSensitivity);
            bglList.add(currentBgl);
            currentBgl = currentBgl - insulinChangeInBgl + riseInBglCarb;
            System.out.println("BGL Breakfast:"+currentBgl+" at time: "+i+" insulinChange "+insulinChangeInBgl+" riseCarb " + riseInBglCarb);
        }

        return bglList;
    }

    /**
     * This method sends notification to the emergency contact person
     * @return
     */
    public Result sos(){
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();

        Patient patient = Patient.getPatient();
        NexmoSMS.sendSms("Emergency Alert! Patient need assistance", "15217158915");
        AlertNotificationHandler.dispatchPushNotification();
        SimpleEmailSender sender = new SimpleEmailSender();
        sender.sendSimpleEmail(patient.emailId, "SOS: Patient needs assistance", "Hi, The patient needs assistance");

        resMap.put("status","success");
        resMap.put("message","Please do not panic. They will be here in no time.");
        return ok(gson.toJson(resMap)).as("application/json");
    }
}
