package controllers;

import play.mvc.Controller;
import utils.commons.Enums;

public class BatteryController extends Controller {

    private boolean turnOff;
    private int charge;
    private static int Rate = 1;
    private Enums.CapacityStatus status;
    private static int MaxBatteryAmount = 100;


}
