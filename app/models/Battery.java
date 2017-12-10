package models;

import io.ebean.Finder;
import io.ebean.Model;
import utils.commons.Enums.CapacityStatus;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Battery extends Model {

    @Id
    public long id;

    private boolean turnOff;
    private int charge;
    private static int Rate = 1;
    private CapacityStatus status;
    private static int MaxBatteryAmount = 100;

    public static Finder<String, Battery> find = new Finder<>(Battery.class);

    public Battery(int charge){
        this.charge = charge;
        this.status = CapacityStatus.FULL;
        this.turnOff = Boolean.FALSE;
    }

    public int getMaxBatteryAmount(){
        return MaxBatteryAmount;
    }

    public static void setMaxBatteryAmount(int amount){
        if (amount > 0)
        {
            MaxBatteryAmount = amount;
        }else{
            MaxBatteryAmount = 100;
        }
    }

    public void run(){
        try {
            charge = charge - (1*Rate);
            Thread.sleep(1000);
            adjustStatus();
            if (charge <0){
                charge = 0;
            }
            if (!turnOff){
                run();
            }
            else{
                System.out.println("Battery Terminated");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setCharge(int charge) {
        if (charge < 0)
            charge = 0;
        if (charge >= MaxBatteryAmount)
        {
            this.charge = MaxBatteryAmount;
        }
        else{
            this.charge = charge;
        }
        adjustStatus();
    }
    public int getCharge() {
        return charge;
    }
    public CapacityStatus getStatus() {
        return status;
    }

    public int getRate() {
        return Rate;
    }
    public void setTurnOff(){
        turnOff = true;
    }
    private void adjustStatus(){
        if (charge >= 0 && charge < 0.2*MaxBatteryAmount){
            status = CapacityStatus.VERY_LOW;
        }
        if (charge >= 0.2*MaxBatteryAmount && charge < 0.4*MaxBatteryAmount){
            status = CapacityStatus.LOW;
        }
        if (charge >= 0.4*MaxBatteryAmount && charge < 0.6*MaxBatteryAmount){
            status = CapacityStatus.MEDIUM;
        }
        if (charge >= 0.6*MaxBatteryAmount && charge < 0.8*MaxBatteryAmount){
            status = CapacityStatus.HIGH;
        }
        if (charge >= 0.8*MaxBatteryAmount){
            status = CapacityStatus.FULL;
        }
    }
}