/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import com.sonycsl.echo.eoj.device.DeviceObject;

import AView.R;

/**
 * @author hoang-trung-duc
 */
public enum MyEchoDevices {
      EV((short) 0x027E, "ev") {
            @Override
            public int image() {
                  return R.drawable.ev;
            }
      },
      BATTERY((short) 0x027D, "battery") {
            @Override
            public int image() {
                  return R.drawable.battery;
            }
      },
      SOLAR((short) 0x0279, "solar") {
            @Override
            public int image() {
                  return R.drawable.solar;
            }
      },
      LIGHT((short) 0x0290, "light") {
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

      abstract public int image();

      public static MyEchoDevices from(DeviceObject device) {
            for (MyEchoDevices myDevice : values()) {
                  if (myDevice.classcode == device.getEchoClassCode()) {
                        return myDevice;
                  }
            }
            return UNKNOWN;
      }

      public static void loadConfig(String[] nameConfig) {
            String evName, solarName, batteryName, lightName;
            evName = nameConfig[0];
            batteryName = nameConfig[1];
            solarName = nameConfig[2];
            lightName = nameConfig[3];

            EV.name = evName;
            BATTERY.name = batteryName;
            SOLAR.name = solarName;
            LIGHT.name = lightName;
      }

      // <editor-fold defaultstate="collapsed" desc="// Skip this">
      public final short classcode;
      public final String type;
      public String name;
      public String address;
      public OperationStatus operationStatus = OperationStatus.OFF;
      public OperationMode operationMode = OperationMode.Other;
      public int d3, e2, e4;
      public int e0, e1;

      MyEchoDevices(short classcode, String type) {
            this.classcode = classcode;
            this.type = type;
            this.name = type;
      }

      // </editor-fold>
}
