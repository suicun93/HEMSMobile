package Main;

import android.util.Log;

import Common.Constants;
import Receiver.MyBatteryReceiver;
import Receiver.MyEchoEventListener;
import Receiver.MyElectricVehicleReceiver;
import Receiver.MyLightReceiver;
import Receiver.MyNodeProfileReceiver;
import Receiver.MySolarReceiver;

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
                  if (onItemSetChanging != null)
                        onItemSetChanging.controlResult(true, new EchoProperty((byte) (listDevice.size() - 1))); // new device = true, lost device = false
            }

            @Override
            public void onNewBattery(Battery battery) {
                  super.onNewBattery(battery);
//                  System.out.println("\t   Device = " + battery);
                  Log.d(Constants.ECHO_TAG, "onNewBattery: " + battery);

                  // Set up
                  MyBatteryReceiver myBatteryReceiver = new MyBatteryReceiver();
                  myBatteryReceiver.setContinuousTask(new TimerTask() {
                        @Override
                        public void run() {
                              try {
                                    battery.get().reqGetRemainingStoredElectricity1().send();
                              } catch (Exception e) {
                                    Log.e(Constants.ECHO_TAG, "Battery Adapter: Device Disconnected" + e.getMessage());
                                    if (myBatteryReceiver != null && myBatteryReceiver.getOnGetEPC() != null)
                                          myBatteryReceiver.getOnGetEPC().controlResult(false, new EchoProperty(battery.EPC_REMAINING_STORED_ELECTRICITY1));
                              }
                              try {
                                    battery.get().reqGetRemainingStoredElectricity3().send();
                              } catch (Exception e) {
                                    Log.e(Constants.ECHO_TAG, "Battery Adapter: Device Disconnected" + e.getMessage());
                                    if (myBatteryReceiver != null && myBatteryReceiver.getOnGetEPC() != null)
                                          myBatteryReceiver.getOnGetEPC().controlResult(false, new EchoProperty(battery.EPC_REMAINING_STORED_ELECTRICITY3));
                              }
                        }
                  });
                  battery.setReceiver(myBatteryReceiver);
                  myBatteryReceiver.startContinuousTask();
                  // Fire signal to update information
                  try {
                        battery.get().reqGetOperationStatus().send();
                        battery.get().reqGetOperationModeSetting().send();
                        battery.get().reqGetMeasuredInstantaneousChargeDischargeElectricEnergy().send();
                        battery.get().reqGetRemainingStoredElectricity1().send(); // E2
                        battery.get().reqGetRemainingStoredElectricity3().send(); // E4
                  } catch (IOException e) {
                        Log.e(Constants.ECHO_TAG, "Battery Adapter: Device Disconnected", e);
                  }
            }

            @Override
            public void onNewElectricVehicle(ElectricVehicle ev) {
                  super.onNewElectricVehicle(ev);
//                  System.out.println("\t   Device = " + ev);
                  Log.d(Constants.ECHO_TAG, "onNewBattery: " + ev);

                  // Setup
                  MyElectricVehicleReceiver myElectricVehicleReceiver = new MyElectricVehicleReceiver();
                  myElectricVehicleReceiver.setContinuousTask(new TimerTask() {
                        @Override
                        public void run() {
                              try {
                                    ev.get().reqGetRemainingBatteryCapacity1().send();
                              } catch (Exception e) {
                                    Log.e(Constants.ECHO_TAG, "EV Adapter: Device Disconnected" + e.getMessage());
                                    if (myElectricVehicleReceiver != null && myElectricVehicleReceiver.getOnGetEPC() != null)
                                          myElectricVehicleReceiver.getOnGetEPC().controlResult(false, new EchoProperty(ev.EPC_REMAINING_BATTERY_CAPACITY1));
                              }
                              try {
                                    ev.get().reqGetRemainingBatteryCapacity3().send();
                              } catch (Exception e) {
                                    Log.e(Constants.ECHO_TAG, "EV Adapter: Device Disconnected" + e.getMessage());
                                    if (myElectricVehicleReceiver != null && myElectricVehicleReceiver.getOnGetEPC() != null)
                                          myElectricVehicleReceiver.getOnGetEPC().controlResult(false, new EchoProperty(ev.EPC_REMAINING_BATTERY_CAPACITY3));
                              }
                        }
                  });
                  ev.setReceiver(myElectricVehicleReceiver);
                  myElectricVehicleReceiver.startContinuousTask();

                  // Fire signal to update information
                  try {
                        ev.get().reqGetOperationStatus().send();
                        ev.get().reqGetOperationModeSetting().send();
                        ev.get().reqGetMeasuredInstantaneousChargeDischargeElectricEnergy().send();
                        ev.get().reqGetRemainingBatteryCapacity1().send(); // E2
                        ev.get().reqGetRemainingBatteryCapacity3().send(); // E4
                  } catch (IOException e) {
                        Log.e(Constants.ECHO_TAG, "EV Adapter: Device Disconnected", e);
                  }
            }

            @Override
            public void onNewHouseholdSolarPowerGeneration(HouseholdSolarPowerGeneration solar) {
                  super.onNewHouseholdSolarPowerGeneration(solar); //To change body of generated methods, choose Tools | Templates.
//                  System.out.println("\t   Device = " + solar);
                  Log.d(Constants.ECHO_TAG, "onNewBattery: " + solar);

                  // Set up
                  MySolarReceiver mySolarReceiver = new MySolarReceiver();
                  mySolarReceiver.setContinuousTask(new TimerTask() {
                        @Override
                        public void run() {
                              try {
                                    solar.get().reqGetMeasuredCumulativeAmountOfElectricityGenerated().send();
                              } catch (Exception e) {
                                    Log.e(Constants.ECHO_TAG, "Solar Adapter: Device Disconnected" + e.getMessage());
                                    if (mySolarReceiver != null && mySolarReceiver.getOnGetEPC() != null)
                                          mySolarReceiver.getOnGetEPC().controlResult(false, new EchoProperty(solar.EPC_MEASURED_CUMULATIVE_AMOUNT_OF_ELECTRICITY_GENERATED));
                              }
                        }
                  });
                  solar.setReceiver(mySolarReceiver);
                  mySolarReceiver.startContinuousTask();
                  // Fire signal to update information
                  try {
                        solar.get().reqGetOperationStatus().send();
                        solar.get().reqGetMeasuredInstantaneousAmountOfElectricityGenerated().send();
                        solar.get().reqGetMeasuredCumulativeAmountOfElectricityGenerated().send(); // E1
                  } catch (IOException e) {
                        Log.e(Constants.ECHO_TAG, "Solar Adapter: Device Disconnected", e);
                  }
            }

            @Override
            public void onNewGeneralLighting(GeneralLighting light) {
                  super.onNewGeneralLighting(light); //To change body of generated methods, choose Tools | Templates.
//                  System.out.println("\t   Device = " + light);
                  Log.d(Constants.ECHO_TAG, "onNewBattery: " + light);

                  // Set up
                  light.setReceiver(new MyLightReceiver());

                  // Fire signal to update information
                  try {
                        light.get().reqGetOperationStatus().send();
                  } catch (IOException e) {
                        Log.e(Constants.ECHO_TAG, "Light Adapter: Device Disconnected", e);
                  }
            }
      };

      public static void startController() throws IOException {
            if (!Echo.isStarted()) {
                  addEvent();
                  Echo.start(NODE_PROFILE, new DeviceObject[]{CONTROLLER});
                  NodeProfile.informG().reqInformInstanceListNotification().send();
            }
      }

      public static void stopController() throws IOException {
            Echo.clear();
      }

      // Add Event
      public static void addEvent() {
            Echo.addEventListener(MY_ECHO_EVENT_LISTENER);
      }
}
