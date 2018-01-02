package controllers;

import com.google.gson.Gson;
import models.User;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.util.HashMap;
import java.util.Map;

public class Application extends Controller {

    public Result index() {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();
        User currentUser = User.byUserName("Ashraf");
        return ok(index.render("Your new application is ready."));
    }

    public Result userInterface(String selectedInterface) {
        Gson gson = new Gson();
        Map<String, Object> resMap = new HashMap<>();
        if (StringUtils.isNotEmpty(selectedInterface)){
            if (selectedInterface.equalsIgnoreCase("DOCTOR")) {
                return redirect(routes.DoctorInterface.index());
            } else if (selectedInterface.equalsIgnoreCase("NURSE")){
                return redirect(routes.NurseInterface.index());
            } else if (selectedInterface.equalsIgnoreCase("ADMIN")){
                return redirect(routes.AdminInterface.index());
            } else {
                return redirect(routes.PatientInterface.index());
            }
        }
        return redirect(routes.NurseInterface.index());
    }

}
