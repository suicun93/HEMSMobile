package Main;

import android.util.Log;

import com.sonycsl.echo.Echo;
import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.eoj.device.DeviceObject;
import com.sonycsl.echo.eoj.device.housingfacilities.Battery;
import com.sonycsl.echo.eoj.device.housingfacilities.ElectricVehicle;
import com.sonycsl.echo.eoj.device.housingfacilities.GeneralLighting;
import com.sonycsl.echo.eoj.device.housingfacilities.HouseholdSolarPowerGeneration;
import com.sonycsl.echo.eoj.device.managementoperation.Controller;
import com.sonycsl.echo.eoj.profile.NodeProfile;
import com.sonycsl.echo.node.EchoNode;
import com.sonycsl.echo.processing.defaults.DefaultController;
import com.sonycsl.echo.processing.defaults.DefaultNodeProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

import Common.Constants;
import Receiver.MyBatteryReceiver;
import Receiver.MyEchoEventListener;
import Receiver.MyElectricVehicleReceiver;
import Receiver.MyLightReceiver;
import Receiver.MyNodeProfileReceiver;
import Receiver.MySolarReceiver;

/**
 * @author hoang-trung-duc
 */
public class EchoController {

      // Init nodeProfile, controller, ev, battery, ...
      private static final DefaultNodeProfile NODE_PROFILE = new DefaultNodeProfile();
      private static final Controller CONTROLLER = new DefaultController();
      public static final ArrayList<DeviceObject> listDevice = new ArrayList<>();
      public static final MyEchoEventListener MY_ECHO_EVENT_LISTENER = new MyEchoEventListener() {
            @Override
            public void onNewNode(EchoNode node) {
                  super.onNewNode(node);
                  if (!node.isSelfNode()) {
                        node.getNodeProfile().setReceiver(new MyNodeProfileReceiver(node));
                  }
            }

            @Override
            public void onNewDeviceObject(DeviceObject device) {
                  super.onNewDeviceObject(device);
                  if (device.getNode().isSelfNode()) {
                        return; // Skip
                  }
                  listDevice.add(device);
                  if (getOnItemSetChangingListener() != null)
                        getOnItemSetChangingListener().controlResult(true, new EchoProperty((byte) (listDevice.size() - 1))); // new device = true, lost device = false
            }

            @Override
            public void onNewBattery(Battery battery) {
                  super.onNewBattery(battery);
                  Log.d(Constants.ECHO_TAG, "onNewBattery: " + battery);

                  // Set up
                  MyBatteryReceiver myBatteryReceiver = new MyBatteryReceiver(battery);
                  myBatteryReceiver.setUpdateTask(new TimerTask() {
                        @Override
                        public void run() {
                              try {
                                    battery.get().reqGetRemainingStoredElectricity1().send();
                              } catch (Exception e) {
                                    Log.e(Constants.ECHO_TAG, "Battery Adapter: Device Disconnected" + e.getMessage());
                                    if (myBatteryReceiver.getOnGetListener() != null)
                                          myBatteryReceiver.getOnGetListener().controlResult(false, new EchoProperty(Battery.EPC_REMAINING_STORED_ELECTRICITY1));
                              }
                              try {
                                    battery.get().reqGetRemainingStoredElectricity3().send();
                              } catch (Exception e) {
                                    Log.e(Constants.ECHO_TAG, "Battery Adapter: Device Disconnected" + e.getMessage());
                                    if (myBatteryReceiver.getOnGetListener() != null)
                                          myBatteryReceiver.getOnGetListener().controlResult(false, new EchoProperty(Battery.EPC_REMAINING_STORED_ELECTRICITY3));
                              }
                        }
                  });
                  battery.setReceiver(myBatteryReceiver);

                  // Fire signal to update information
                  myBatteryReceiver.update(exception -> {
                        Log.e(Constants.ECHO_TAG, "onNewBattery: Device disconnected.", exception);
                  });
                  myBatteryReceiver.startUpdateTask();
            }

            @Override
            public void onNewElectricVehicle(ElectricVehicle ev) {
                  super.onNewElectricVehicle(ev);
                  Log.d(Constants.ECHO_TAG, "onNewBattery: " + ev);

                  // Setup
                  MyElectricVehicleReceiver myElectricVehicleReceiver = new MyElectricVehicleReceiver(ev);
                  myElectricVehicleReceiver.setUpdateTask(new TimerTask() {
                        @Override
                        public void run() {
                              try {
                                    ev.get().reqGetRemainingBatteryCapacity1().send();
                              } catch (Exception e) {
                                    Log.e(Constants.ECHO_TAG, "EV Adapter: Device Disconnected" + e.getMessage());
                                    if (myElectricVehicleReceiver.getOnGetListener() != null)
                                          myElectricVehicleReceiver.getOnGetListener().controlResult(false, new EchoProperty(ElectricVehicle.EPC_REMAINING_BATTERY_CAPACITY1));
                              }
                              try {
                                    ev.get().reqGetRemainingBatteryCapacity3().send();
                              } catch (Exception e) {
                                    Log.e(Constants.ECHO_TAG, "EV Adapter: Device Disconnected" + e.getMessage());
                                    if (myElectricVehicleReceiver.getOnGetListener() != null)
                                          myElectricVehicleReceiver.getOnGetListener().controlResult(false, new EchoProperty(ElectricVehicle.EPC_REMAINING_BATTERY_CAPACITY3));
                              }
                        }
                  });
                  ev.setReceiver(myElectricVehicleReceiver);

                  // Fire signal to update information
                  myElectricVehicleReceiver.update(exception -> {
                        Log.e(Constants.ECHO_TAG, "onNewElectricVehicle: Device disconnected.", exception);
                  });
                  myElectricVehicleReceiver.startUpdateTask();
            }

            @Override
            public void onNewHouseholdSolarPowerGeneration(HouseholdSolarPowerGeneration solar) {
                  super.onNewHouseholdSolarPowerGeneration(solar); //To change body of generated methods, choose Tools | Templates.
                  Log.d(Constants.ECHO_TAG, "onNewBattery: " + solar);

                  // Set up
                  MySolarReceiver mySolarReceiver = new MySolarReceiver(solar);
                  mySolarReceiver.setUpdateTask(new TimerTask() {
                        @Override
                        public void run() {
                              try {
                                    solar.get().reqGetMeasuredCumulativeAmountOfElectricityGenerated().send();
                              } catch (Exception e) {
                                    Log.e(Constants.ECHO_TAG, "Solar Adapter: Device Disconnected" + e.getMessage());
                                    if (mySolarReceiver.getOnGetListener() != null)
                                          mySolarReceiver.getOnGetListener().controlResult(false, new EchoProperty(HouseholdSolarPowerGeneration.EPC_MEASURED_CUMULATIVE_AMOUNT_OF_ELECTRICITY_GENERATED));
                              }
                        }
                  });
                  solar.setReceiver(mySolarReceiver);

                  // Fire signal to update information
                  mySolarReceiver.update(exception -> {
                        Log.e(Constants.ECHO_TAG, "onNewHouseholdSolarPowerGeneration: Device disconnected.", exception);
                  });
                  mySolarReceiver.startUpdateTask();
            }

            @Override
            public void onNewGeneralLighting(GeneralLighting light) {
                  super.onNewGeneralLighting(light); //To change body of generated methods, choose Tools | Templates.
                  Log.d(Constants.ECHO_TAG, "onNewBattery: " + light);

                  // Set up
                  MyLightReceiver myLightReceiver = new MyLightReceiver(light);
                  light.setReceiver(myLightReceiver);

                  // Fire signal to update information
                  myLightReceiver.update(exception -> {
                        Log.e(Constants.ECHO_TAG, "onNewGeneralLighting: Device disconnected.", exception);
                  });
            }
      };

      public static void startController() throws IOException {
            if (!Echo.isStarted()) {
                  addEvent();
                  Echo.start(NODE_PROFILE, new DeviceObject[]{CONTROLLER});
                  NodeProfile.informG().reqInformInstanceListNotification().send();
            }
      }

//      Unused method
//      public static void stopController() throws IOException {
//            Echo.clear();
//      }

      // Add Event
      private static void addEvent() {
            Echo.addEventListener(MY_ECHO_EVENT_LISTENER);
      }
}
