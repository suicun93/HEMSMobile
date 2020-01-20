/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Receiver;

import Common.Convert;
import static Model.MyEchoDevices.SOLAR;
import Model.OperationStatus;
import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.housingfacilities.HouseholdSolarPowerGeneration;

/**
 *
 * @author hoang-trung-duc
 */
public class MySolarReceiver extends HouseholdSolarPowerGeneration.Receiver {

    @Override
    protected void onGetOperationStatus(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
        super.onGetOperationStatus(eoj, tid, esv, property, success);
        if (!success) {
            System.out.println("onGetProperty " + SOLAR.name() + " Failed: EPC = " + Convert.byteToHex(property.epc));
        } else {
            SOLAR.operationStatus = OperationStatus.from(property.edt[0]);
        }
    }

    @Override
    protected void onGetMeasuredInstantaneousAmountOfElectricityGenerated(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
        super.onGetMeasuredInstantaneousAmountOfElectricityGenerated(eoj, tid, esv, property, success);
        if (!success) {
            System.out.println("onGetProperty " + SOLAR.name() + " Failed: EPC = " + Convert.byteToHex(property.epc));
        } else {
            SOLAR.e0 = Convert.byteArrayToInt(property.edt);
        }
    }

    @Override
    protected void onGetMeasuredCumulativeAmountOfElectricityGenerated(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
        synchronized (SOLAR) {
            super.onGetMeasuredCumulativeAmountOfElectricityGenerated(eoj, tid, esv, property, success);
            if (!success) {
                System.out.println("onGetProperty " + SOLAR.name() + " Failed: EPC = " + Convert.byteToHex(property.epc));
            } else {
                SOLAR.e1 = Convert.byteArrayToInt(property.edt);
            }
            SOLAR.notify();
        }

    }

    @Override
    protected boolean onSetProperty(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
        synchronized (out) {
            boolean result = super.onSetProperty(eoj, tid, esv, property, success);
            if (success) {
                out.print("success");
            } else {
                out.print("{\n"
                        + "\"Failed\":\"" + "Wrong EPC,EDT" + "\"\n"
                        + "}");
            }
            out.notify();
            return result;
        }
    }
}
