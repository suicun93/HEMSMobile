/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import com.sonycsl.echo.eoj.device.DeviceObject;
import com.sonycsl.echo.eoj.device.housingfacilities.Battery;
import com.sonycsl.echo.eoj.device.housingfacilities.ElectricVehicle;
import com.sonycsl.echo.eoj.device.housingfacilities.GeneralLighting;
import com.sonycsl.echo.eoj.device.housingfacilities.HouseholdSolarPowerGeneration;

import AView.R;

/**
 * @author hoang-trung-duc
 */
public enum MyEchoDevices {
      EV(ElectricVehicle.ECHO_CLASS_CODE, "ev") {
            @Override
            public int image() {
                  return R.drawable.ev;
            }
      },
      BATTERY(Battery.ECHO_CLASS_CODE, "battery") {
            @Override
            public int image() {
                  return R.drawable.battery;
            }
      },
      SOLAR(HouseholdSolarPowerGeneration.ECHO_CLASS_CODE, "solar") {
            @Override
            public int image() {
                  return R.drawable.solar;
            }
      },
      LIGHT(GeneralLighting.ECHO_CLASS_CODE, "light") {
            @Override
            public int image() {
                  return R.drawable.light;
            }
      },
      UNKNOWN((short) 0x0000, "unknown") {
            @Override
            public int image() {
                  return R.drawable.logo;
            }
      };


      public static MyEchoDevices from(DeviceObject device) {
            for (MyEchoDevices myDevice : values()) {
                  if (myDevice.classcode == device.getEchoClassCode()) {
                        return myDevice;
                  }
            }
            return UNKNOWN;
      }

      // <editor-fold defaultstate="collapsed" desc="// Skip this">
      public final short classcode;
      public String name;

      abstract public int image();

      MyEchoDevices(short classcode, String type) {
            this.classcode = classcode;
            this.name = type;
      }
      // </editor-fold>
}
