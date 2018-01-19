package controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.DoctorProfiles;
import models.Patient;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.doctor_interface_index;

import java.util.HashMap;
import java.util.Map;

public class DoctorInterface extends Controller {

    public Result index() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();
        return ok(doctor_interface_index.render("Your new application is ready."));
    }

    public Result registerDoctor() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();

        Map<String, String> data = gson.fromJson(request().body().asJson().toString(), new TypeToken<Map<String, String>>() {
        }.getType());

        String firstName = data.get("doctorFirstName");
        String lastName = data.get("doctorLastName");
        String doctorId = data.get("doctorId");
        String password = data.get("password");
        String doctorGender = data.get("doctorGender");
        String emailId = data.get("emailId");
        String mobileNumber = data.get("mobileNumber");
        if (StringUtils.isNotEmpty(lastName) && StringUtils.isNotEmpty(password) && StringUtils.isNotEmpty(doctorId)){
            DoctorProfiles doctor = DoctorProfiles.createOrUpdate(emailId, doctorId, password, firstName, lastName,
                    doctorGender, mobileNumber);
            resMap.put("status", "success");
            resMap.put("firstName", doctor.firstName);
            resMap.put("lastName", doctor.lastName);
            return ok(gson.toJson(resMap)).as("application/json");
        } else {
            resMap.put("status", "error");
            return ok(gson.toJson(resMap)).as("application/json");
        }
    }

    public Result doctorLogin (){
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();

        String doctorId = request().getQueryString("doctorId");
        String password = request().getQueryString("password");

        if (StringUtils.isNotEmpty(doctorId) && StringUtils.isNotEmpty(password)){
            DoctorProfiles doctorProfiles = DoctorProfiles.byDoctorIdPassword(doctorId, password);
            if (null != doctorProfiles) {
                resMap.put("status", "success");
                resMap.put("doctorId", doctorProfiles.id);
                return ok(gson.toJson(resMap)).as("application/json");
            } else {
                resMap.put("status", "error");
                return ok(gson.toJson(resMap)).as("application/json");
            }
        } else {
            resMap.put("status", "error");
            return ok(gson.toJson(resMap)).as("application/json");
        }

    }

    public Result fetchDoctorProfile (){
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();

        String doctorId = request().getQueryString("doctorId");

        if (StringUtils.isNotEmpty(doctorId)){
            DoctorProfiles doctorProfiles = DoctorProfiles.byId(Long.parseLong(doctorId));
            if (null != doctorProfiles) {
                resMap.put("status", "success");
                resMap.put("doctorName", doctorProfiles.lastName);
                return ok(gson.toJson(resMap)).as("application/json");
            } else {
                resMap.put("status", "error");
                return ok(gson.toJson(resMap)).as("application/json");
            }
        } else {
            resMap.put("status", "error");
            return ok(gson.toJson(resMap)).as("application/json");
        }

    }
}
