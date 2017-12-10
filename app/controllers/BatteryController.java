package controllers;

import com.google.gson.Gson;
import models.User;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;
import utils.commons.Enums;
import views.html.index;

import java.util.HashMap;
import java.util.Map;

public class BatteryController extends Controller {

    private boolean turnOff;
    private int charge;
    private static int Rate = 1;
    private Enums.CapacityStatus status;
    private static int MaxBatteryAmount = 100;


}
