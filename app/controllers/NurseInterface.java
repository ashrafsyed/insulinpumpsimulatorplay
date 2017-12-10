package controllers;

import com.google.gson.Gson;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.nurse_interface_index;

import java.util.HashMap;
import java.util.Map;

public class NurseInterface extends Controller {

    public Result index() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();
        return ok(nurse_interface_index.render("Your new application is ready."));
    }

}
