package controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lib.TextLocalSMS;
import models.DeviceConfig;
import models.Patient;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin_interface_index;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AdminInterface extends Controller {

    public Result index() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();
        return ok(admin_interface_index.render("Your new application is ready."));
    }

    public Result adminAuthorization() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();
        String adminPin = request().getQueryString("adminpin");
        if (StringUtils.isNotEmpty(adminPin) && adminPin.equalsIgnoreCase("12345")){
            DeviceConfig config = DeviceConfig.getOrCreate();
            resMap.put("status", "success");
            resMap.put("deviceId", config.deviceId);
            resMap.put("patientId", config.patientId);
            resMap.put("message", "Howdy Admin!!");
            return ok(gson.toJson(resMap)).as("application/json");
        } else {
            resMap.put("status", "error");
            resMap.put("message", "Incorrect PIN");
            return ok(gson.toJson(resMap)).as("application/json");
        }
    }

    public Result savePatientData() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();

        Map<String, String> data = gson.fromJson(request().body().asJson().toString(), new TypeToken<Map<String, String>>() {
        }.getType());

        String deviceId = data.get("deviceId");
        String patientId = data.get("patientId");
        String firstName = data.get("patientFirstName");
        String lastName = data.get("patientLastName");
        String gender = data.get("patientGender");
        String emailId = data.get("emailId");
        String mobileNumber = data.get("mobileNumber");
        Double height = Double.valueOf(data.get("patientHeight"));
        Double weight = Double.valueOf(data.get("patientWeight"));
        String age = data.get("patientAge");
        if (StringUtils.isNotEmpty(lastName) && StringUtils.isNotEmpty(firstName) && StringUtils.isNotEmpty(gender)){
            Patient patient = Patient.createOrUpdatePatientData(deviceId, patientId, firstName, lastName,
                    gender, emailId, mobileNumber, Integer.parseInt(age), height, weight);
            resMap.put("status", "success");
            resMap.put("deviceId", patient.deviceId);
            resMap.put("patientId", patient.patientId);
            return ok(gson.toJson(resMap)).as("application/json");
        } else {
            resMap.put("status", "error");
            return ok(gson.toJson(resMap)).as("application/json");
        }
    }

    public Result saveDeviceConfigData() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();

        Map<String, String> data = gson.fromJson(request().body().asJson().toString(), new TypeToken<Map<String, String>>() {
        }.getType());

        String deviceId = data.get("deviceId");
        String patientId = data.get("patientId");
        String battery = data.get("battery");
        String insulin = data.get("insulin");
        Double glucagon = Double.valueOf(data.get("glucagon"));
        String deviceMode = data.get("deviceMode");
        Double bolusMax = Double.valueOf(data.get("bolusMax"));
        Double dailyMax = Double.valueOf(data.get("dailyMax"));
        Double targetBgl = Double.valueOf(data.get("targetBgl"));

        if (StringUtils.isNotEmpty(deviceId) && StringUtils.isNotEmpty(patientId)){
            DeviceConfig patient = DeviceConfig.createOrUpdate(deviceId, patientId, deviceMode, Double.parseDouble(battery), Double.parseDouble(insulin),
                                        glucagon, dailyMax, bolusMax, targetBgl);
            resMap.put("status", "success");
            resMap.put("deviceId", patient.deviceId);
            resMap.put("patientId", patient.patientId);
            return ok(gson.toJson(resMap)).as("application/json");
        } else {
            resMap.put("status", "error");
            return ok(gson.toJson(resMap)).as("application/json");
        }
    }

    public Result getPatientData() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        Random rand = new Random();
        int hba1c = 34 + rand.nextInt((46 - 34) + 1);

        String deviceId = request().getQueryString("deviceId");
        String patientId = request().getQueryString("patientId");
        if (StringUtils.isNotEmpty(deviceId) && StringUtils.isNotEmpty(patientId)){
            Patient patient = Patient.byIds(deviceId, patientId);
            DeviceConfig config = DeviceConfig.byIds(deviceId, patientId);
            if (null != patient){
                dataMap.put("firstName", patient.patientFirstName);
                dataMap.put("lastName", patient.patientLastName);
                dataMap.put("gender", patient.patientGender);
                dataMap.put("email", patient.emailId);
                dataMap.put("mobile", patient.mobileNumber);
                dataMap.put("age", patient.patientAge);
                dataMap.put("height", patient.patientHeight);
                dataMap.put("weight", patient.patientWeight);
                dataMap.put("bolusMax", config.bolusMax);
                dataMap.put("dailyMax", config.dailyMax);
                dataMap.put("targetBgl", config.targetBgl);
                dataMap.put("deviceMode", config.deviceMode);
                dataMap.put("hba1c", hba1c);
                resMap.put("data", dataMap);
            }
            resMap.put("status", "success");
            return ok(gson.toJson(resMap)).as("application/json");
        } else {
            resMap.put("status", "error");
            return ok(gson.toJson(resMap)).as("application/json");
        }
    }

    public Result getDeviceData() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();

        String deviceId = request().getQueryString("deviceId");
        String patientId = request().getQueryString("patientId");
        if (StringUtils.isNotEmpty(deviceId) && StringUtils.isNotEmpty(patientId)){
            DeviceConfig config = DeviceConfig.byIds(deviceId, patientId);
            if (null != config){
                dataMap.put("battery", config.batteryLevel);
                dataMap.put("insulin", config.insulinLevel);
                dataMap.put("glucagon", config.glucagonLevel);
                dataMap.put("deviceMode", config.deviceMode);
                dataMap.put("bolusMax", config.bolusMax);
                dataMap.put("dailyMax", config.dailyMax);
                dataMap.put("targetBgl", config.targetBgl);
                resMap.put("data", dataMap);
            }
            resMap.put("status", "success");
            return ok(gson.toJson(resMap)).as("application/json");
        } else {
            resMap.put("status", "error");
            return ok(gson.toJson(resMap)).as("application/json");
        }
    }

}
