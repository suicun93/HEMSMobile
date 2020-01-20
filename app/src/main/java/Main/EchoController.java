/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import static Model.MyEchoDevices.EV;
import static Model.MyEchoDevices.BATTERY;
import static Model.MyEchoDevices.SOLAR;
import static Model.MyEchoDevices.LIGHT;

import Receiver.MyBatteryReceiver;
import Receiver.MyElectricVehicleReceiver;
import Receiver.MyLightReceiver;
import Receiver.MyNodeProfileReceiver;
import Receiver.MySolarReceiver;
import com.sonycsl.echo.Echo;
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
import java.util.Arrays;

/**
 *
 * @author hoang-trung-duc
 */
public class EchoController {

    // Init nodeProfile, controller, ev, battery, ...
    private static final DefaultNodeProfile NODE_PROFILE = new DefaultNodeProfile();
    private static final Controller CONTROLLER = new DefaultController();

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

    public static ArrayList<DeviceObject> listDevice() {
        ArrayList<DeviceObject> listDevice = new ArrayList<>();
        for (EchoNode node : Echo.getNodes()) {
            if (!node.isSelfNode()) {
                listDevice.addAll(Arrays.asList(node.getDevices()));
            }
        }
        return listDevice;
    }

    // Add Event
    public static void addEvent() {
        Echo.addEventListener(new Echo.EventListener() {
            @Override
            public void onNewNode(EchoNode node) {
                super.onNewNode(node);
                if (!node.isSelfNode()) {
                    node.getNodeProfile().setReceiver(new MyNodeProfileReceiver(node));
                }
            }

            @Override
            public void onNewBattery(Battery battery) {
                super.onNewBattery(battery);
                System.out.println("\t   Device = " + battery);

                // Set up
                BATTERY.address = battery.getNode().getAddressStr();
                battery.setReceiver(new MyBatteryReceiver());

                // Fire signal to update information
                try {
                    battery.get().reqGetOperationStatus().send();
                    battery.get().reqGetOperationModeSetting().send();
                    battery.get().reqGetMeasuredInstantaneousChargeDischargeElectricEnergy().send();
                    battery.get().reqGetRemainingStoredElectricity1().send(); // E2
                    battery.get().reqGetRemainingStoredElectricity3().send(); // E4
                } catch (IOException e) {
                    System.out.println("Get Property " + BATTERY.name() + " Failed: " + e.getMessage());
                }
            }

            @Override
            public void onNewElectricVehicle(ElectricVehicle ev) {
                super.onNewElectricVehicle(ev);
                System.out.println("\t   Device = " + ev);

                // Setup
                EV.address = ev.getNode().getAddressStr();
                ev.setReceiver(new MyElectricVehicleReceiver());

                // Fire signal to update information
                try {
                    ev.get().reqGetOperationStatus().send();
                    ev.get().reqGetOperationModeSetting().send();
                    ev.get().reqGetMeasuredInstantaneousChargeDischargeElectricEnergy().send();
                    ev.get().reqGetRemainingBatteryCapacity1().send(); // E2
                    ev.get().reqGetRemainingBatteryCapacity3().send(); // E4
                } catch (IOException e) {
                    System.out.println("Get Property " + EV.name() + " Failed: " + e.getMessage());
                }
            }

            @Override
            public void onNewHouseholdSolarPowerGeneration(HouseholdSolarPowerGeneration solar) {
                super.onNewHouseholdSolarPowerGeneration(solar); //To change body of generated methods, choose Tools | Templates.
                System.out.println("\t   Device = " + solar);

                // Set up
                SOLAR.address = solar.getNode().getAddressStr();
                solar.setReceiver(new MySolarReceiver());

                // Fire signal to update information
                try {
                    solar.get().reqGetOperationStatus().send();
                    solar.get().reqGetMeasuredInstantaneousAmountOfElectricityGenerated().send();
                    solar.get().reqGetMeasuredCumulativeAmountOfElectricityGenerated().send(); // E1
                } catch (IOException e) {
                    System.out.println("Get Property " + SOLAR.name() + " Failed: " + e.getMessage());
                }
            }

            @Override
            public void onNewGeneralLighting(GeneralLighting light) {
                super.onNewGeneralLighting(light); //To change body of generated methods, choose Tools | Templates.
                System.out.println("\t   Device = " + light);

                // Set up
                LIGHT.address = light.getNode().getAddressStr();
                light.setReceiver(new MyLightReceiver());

                // Fire signal to update information
                try {
                    light.get().reqGetOperationStatus().send();
                } catch (IOException e) {
                    System.out.println("Get Property " + LIGHT.name() + " Failed: " + e.getMessage());
                }
            }
        });
    }
}
