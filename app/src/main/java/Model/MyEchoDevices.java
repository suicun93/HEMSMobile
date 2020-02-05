package Model;

import com.sonycsl.echo.eoj.device.DeviceObject;
import com.sonycsl.echo.eoj.device.housingfacilities.Battery;
import com.sonycsl.echo.eoj.device.housingfacilities.ElectricVehicle;
import com.sonycsl.echo.eoj.device.housingfacilities.GeneralLighting;
import com.sonycsl.echo.eoj.device.housingfacilities.HouseholdSolarPowerGeneration;

import static AView.R.drawable.battery;
import static AView.R.drawable.ev;
import static AView.R.drawable.light;
import static AView.R.drawable.logo;
import static AView.R.drawable.solar;
import static AView.R.string.battery_name;
import static AView.R.string.ev_name;
import static AView.R.string.light_name;
import static AView.R.string.solar_name;
import static AView.R.string.unknown_name;

/**
 * @author hoang-trung-duc
 */
public enum MyEchoDevices {
      BATTERY(Battery.ECHO_CLASS_CODE,
                battery_name,
                battery),
      EV(ElectricVehicle.ECHO_CLASS_CODE,
                ev_name,
                ev),
      LIGHT(GeneralLighting.ECHO_CLASS_CODE,
                light_name,
                light),
      SOLAR(HouseholdSolarPowerGeneration.ECHO_CLASS_CODE,
                solar_name,
                solar),
      UNKNOWN((short) 0x0000,
                unknown_name,
                logo);

      // <editor-fold defaultState="collapsed" desc="// Skip this">
      public final short classCode;
      public int name;
      public int image;

      MyEchoDevices(short classCode, int name, int image) {
            this.classCode = classCode;
            this.name = name;
            this.image = image;
      }

      public static MyEchoDevices from(DeviceObject device) {
            for (MyEchoDevices myDevice : values()) {
                  if (myDevice.classCode == device.getEchoClassCode()) {
                        return myDevice;
                  }
            }
            return UNKNOWN;
      }
      // </editor-fold>
}
