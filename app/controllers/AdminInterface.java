package controllers;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin_interface_index;
import views.html.patient_interface_index;

import java.util.HashMap;
import java.util.Map;

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
            resMap.put("status", "success");
            resMap.put("message", "Howdy Admin!!");
            return ok(gson.toJson(resMap)).as("application/json");
        } else {
            resMap.put("status", "error");
            resMap.put("message", "Incorrect PIN");
            return ok(gson.toJson(resMap)).as("application/json");
        }
    }

    public Result saveConfig() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();
        String adminPin = request().getQueryString("adminpin");
        if (StringUtils.isNotEmpty(adminPin) && adminPin.equalsIgnoreCase("12345")){
            resMap.put("status", "success");
            resMap.put("message", "Howdy Admin!!");
            return ok(gson.toJson(resMap)).as("application/json");
        } else {
            resMap.put("status", "error");
            resMap.put("message", "Incorrect PIN");
            return ok(gson.toJson(resMap)).as("application/json");
        }
    }

}
