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

    public Result savePatientData() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();
        String firstName = request().getQueryString("patientFirstName");
        String lastName = request().getQueryString("patientLastName");
        String gender = request().getQueryString("patientGender");
        String emailId = request().getQueryString("emailId");
        String mobileNumber = request().getQueryString("mobileNumber");
        String height = (request().getQueryString("patientHeight"));
        String weight = request().getQueryString("patientWeight");
        String age = request().getQueryString("patientAge");
        if (StringUtils.isNotEmpty(lastName) && StringUtils.isNotEmpty(firstName) && StringUtils.isNotEmpty(gender)){
            System.out.println(firstName);
            resMap.put("status", "success");
            return ok(gson.toJson(resMap)).as("application/json");
        } else {
            resMap.put("status", "error");
            return ok(gson.toJson(resMap)).as("application/json");
        }
    }

}
