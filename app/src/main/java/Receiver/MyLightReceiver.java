/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Receiver;

import Common.Convert;
import static Model.MyEchoDevices.LIGHT;
import Model.OperationStatus;
import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.housingfacilities.GeneralLighting;

/**
 *
 * @author hoang-trung-duc
 */
public class MyLightReceiver extends GeneralLighting.Receiver {

    @Override
    protected void onGetOperationStatus(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
        super.onGetOperationStatus(eoj, tid, esv, property, success); //To change body of generated methods, choose Tools | Templates.
        if (!success) {
            System.out.println("onGetProperty " + LIGHT.name() + " Failed: EPC = " + Convert.byteToHex(property.epc));
        } else {
            LIGHT.operationStatus = OperationStatus.from(property.edt[0]);
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
