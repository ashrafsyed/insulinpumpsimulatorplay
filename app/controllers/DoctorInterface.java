package controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.DoctorProfiles;
import models.Patient;
import models.PatientList;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.doctor_interface_index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            DoctorProfiles doctorProfiles = DoctorProfiles.byProfileId(Long.parseLong(doctorId));
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

    public Result getPatientList (){
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();
        List<Map<String, String>> responsePatientList = new ArrayList<>();

        String doctorId = request().getQueryString("doctorId");

        if (StringUtils.isNotEmpty(doctorId)){
            DoctorProfiles profile = DoctorProfiles.byProfileId(Long.parseLong(doctorId));
            List<PatientList> patientLists = PatientList.byDoctorId(profile.doctorId);
            for (PatientList patient: patientLists){
                Map<String, String> patientMap = new HashMap<>();
                patientMap.put("patientId", patient.patientId);
                patientMap.put("deviceId", patient.deviceId);
                patientMap.put("patientFirstName", patient.patientFirstName);
                patientMap.put("patientLastName", patient.patientLastName);
                patientMap.put("patientGender", patient.patientGender);
                patientMap.put("patientGender", patient.patientGender);
                patientMap.put("patientAge", String.valueOf(patient.patientAge));
                patientMap.put("patientEmailId", patient.patientEmailId);
                patientMap.put("patientMobileNumber", patient.patientMobileNumber);
                responsePatientList.add(patientMap);
            }

            resMap.put("patientList", responsePatientList);
            resMap.put("status", "success");
            return ok(gson.toJson(resMap)).as("application/json");
        } else {
            resMap.put("status", "error");
            return ok(gson.toJson(resMap)).as("application/json");
        }

    }

    public Result addPatient (){
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();

        String doctorId = request().getQueryString("doctorId");
        String patientId = request().getQueryString("patientId");
        String deviceId = request().getQueryString("deviceId");

        if (StringUtils.isNotEmpty(doctorId) && StringUtils.isNotEmpty(patientId) && StringUtils.isNotEmpty(deviceId)){
            Patient patient = Patient.byIds(deviceId, patientId);
            DoctorProfiles doctorProfiles = DoctorProfiles.byProfileId(Long.parseLong(doctorId));
            PatientList addPatient = PatientList.createOrUpdatePatientData(doctorProfiles.doctorId, deviceId, patientId, patient.patientFirstName, patient.patientLastName,
                    patient.patientGender, patient.emailId, patient.mobileNumber, patient.patientAge, patient.patientHeight, patient.patientWeight);

            if (null != addPatient) {
                resMap.put("status", "success");
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

    public Result removePatient (){
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();

        String doctorId = request().getQueryString("doctorId");
        String patientId = request().getQueryString("patientId");
        String deviceId = request().getQueryString("deviceId");

        if (StringUtils.isNotEmpty(doctorId) && StringUtils.isNotEmpty(patientId) && StringUtils.isNotEmpty(deviceId)){
            Patient patient = Patient.byIds(deviceId, patientId);
            Boolean patientRemoved = PatientList.removePatient(deviceId, patientId);
            if (patientRemoved) {
                resMap.put("status", "success");
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
