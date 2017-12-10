package controllers;

import com.google.gson.Gson;
import models.User;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.util.HashMap;
import java.util.Map;

public class SensorController extends Controller {

    public Result bglHistory(Long deviceId, Long patientId) {

    return ok();
    }

    public Result getCurrentBGL(Long deviceId, Long patientId) {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();

        return ok(gson.toJson(resMap)).as("application/json");
    }

    public Result recordBGL() {

        return ok();
    }
}
