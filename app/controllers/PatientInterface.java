package controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lib.InsulinModule;
import models.DeviceConfig;
import models.Patient;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.patient_interface_index;

import java.util.HashMap;
import java.util.Map;

public class PatientInterface extends Controller {

    public Result index() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();
        return ok(patient_interface_index.render("Your new application is ready."));
    }

    public Result getDeviceConfig() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();
        DeviceConfig config = DeviceConfig.getDevice();
        if (config != null) {
            resMap.put("status", "success");
            resMap.put("deviceId", config.deviceId);
            resMap.put("patientId", config.patientId);
            resMap.put("deviceMode", config.deviceMode);
            return ok(gson.toJson(resMap)).as("application/json");
        } else {
            resMap.put("status", "error");
            resMap.put("message", "No Configuration Available! Login as Admin to configure Device!");
            return ok(gson.toJson(resMap)).as("application/json");
        }
    }

    public Result getComputedInsulin() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();


        String deviceId = request().getQueryString("deviceId");
        String patientId = request().getQueryString("patientId");
        Double startBgl = Double.valueOf(request().getQueryString("startBgl"));
        Double manualModeCHO = Double.valueOf(request().getQueryString("manualModeCHO"));
        Double manualModeGI = Double.valueOf(request().getQueryString("manualModeGI"));

        Patient patient = Patient.byIds(deviceId, patientId);
        DeviceConfig config = DeviceConfig.byIds(deviceId, patientId);
        Double computedInsulin = InsulinModule.computeInsulinDose(manualModeCHO, manualModeGI, patient.glucoseSensitivity, startBgl, config.targetBgl);
        if (computedInsulin != null) {
            resMap.put("status", "success");
            resMap.put("computedInsulin", computedInsulin);
            resMap.put("bolusMax", config.bolusMax);
            return ok(gson.toJson(resMap)).as("application/json");
        } else {
            resMap.put("status", "error");
            return ok(gson.toJson(resMap)).as("application/json");
        }
    }

}
