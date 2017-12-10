package controllers;

import com.google.gson.Gson;
import models.User;
import org.apache.commons.lang3.StringUtils;
import play.data.validation.Constraints;
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


    public Result login() {
/*
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        Gson gson = new Gson();
        Map<String, String> resMap = new HashMap<>();
        if (loginForm.hasErrors()) {
            return badRequest(loginForm.errorsAsJson());
        }
        Login loggingInUser = loginForm.get();
        User user = User.byUserName("Ashraf");
        if(user == null) {
            return badRequest(gson.toJson(resMap.put("error", "Incorrect email or password"))).as("application/json");
        } else {
            session().clear();
//            session("username", loggingInUser.email);

            resMap.put("message", "Logged in successfully");
            resMap.put("success", "Logged in successfully");
            return ok(gson.toJson(resMap)).as("application/json");
        }
*/

        return ok();
    }

    public Result logout() {
        session().clear();
        Gson gson = new Gson();
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("success", "Logged out Successfully");
        return ok(gson.toJson(responseMap)).as("application/json");
    }

    public Result isAuthenticated() {
        if(session().get("username") == null) {
            return unauthorized();
        } else {
            Gson gson = new Gson();
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("message", "User is logged in already");
            responseMap.put("user", session().get("username"));
            responseMap.put("success", "Authenticated");
            return ok(gson.toJson(responseMap)).as("application/json");
        }
    }

    public static class UserForm {
        @Constraints.Required
        @Constraints.Email
        public String email;
    }

    public static class Login extends UserForm {
        @Constraints.Required
        public String password;
    }
}
