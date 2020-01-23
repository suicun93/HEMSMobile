/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Receiver;

import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.housingfacilities.GeneralLighting;

import Common.Convert;
import Model.OperationStatus;

import static Model.MyEchoDevices.LIGHT;

/**
 * @author hoang-trung-duc
 */
public class MyLightReceiver extends GeneralLighting.Receiver implements ResultHandlable {

      @Override
      protected void onGetOperationStatus(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            super.onGetOperationStatus(eoj, tid, esv, property, success); //To change body of generated methods, choose Tools | Templates.
            if (!success) {
                  System.out.println("onGetProperty " + LIGHT.name() + " Failed: EPC = " + Convert.byteToHex(property.epc));
            } else {
                  LIGHT.operationStatus = OperationStatus.from(property.edt[0]);
            }
      }

      ResultHandle resultHandle;

      @Override
      protected boolean onSetProperty(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            boolean result = super.onSetProperty(eoj, tid, esv, property, success);
            if (success) {
                  resultHandle.successRun();
            } else {
                  resultHandle.failRun();
            }
            return result;
      }

      @Override
      public void setResultHandle(ResultHandle resultHandle) {
            this.resultHandle = resultHandle;
      }
}
