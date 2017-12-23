package controllers;

import com.google.gson.Gson;
import models.DeviceConfig;
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
        DeviceConfig config = DeviceConfig.getOrCreate();
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

}
