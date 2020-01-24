package Receiver;

import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.housingfacilities.ElectricVehicle;

import Common.Convert;
import Model.OperationMode;
import Model.OperationStatus;

import static Model.MyEchoDevices.EV;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author hoang-trung-duc
 */
public class MyElectricVehicleReceiver extends ElectricVehicle.Receiver implements ResultHandlable {

      @Override
      protected void onGetOperationStatus(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            super.onGetOperationStatus(eoj, tid, esv, property, success);
            if (!success) {
                  System.out.println("onGetProperty " + EV.name() + " Failed: EPC = " + Convert.byteToHex(property.epc));
            } else {
                  EV.operationStatus = OperationStatus.from(property.edt[0]);
            }
      }

      @Override
      protected void onGetOperationModeSetting(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            super.onGetOperationModeSetting(eoj, tid, esv, property, success);
            if (!success) {
                  System.out.println("onGetProperty " + EV.name() + " Failed: EPC = " + Convert.byteToHex(property.epc));
            } else {
                  EV.operationMode = OperationMode.from(property.edt[0]);
            }
      }

      @Override
      protected void onGetMeasuredInstantaneousChargeDischargeElectricEnergy(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            super.onGetMeasuredInstantaneousChargeDischargeElectricEnergy(eoj, tid, esv, property, success);
            if (!success) {
                  System.out.println("onGetProperty " + EV.name() + " Failed: EPC = " + Convert.byteToHex(property.epc));
            } else {
                  EV.d3 = Convert.byteArrayToInt(property.edt);
            }
      }

      @Override
      protected void onGetRemainingBatteryCapacity1(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            synchronized (EV) {
                  super.onGetRemainingBatteryCapacity1(eoj, tid, esv, property, success);
                  if (!success) {
                        System.out.println("onGetProperty " + EV.name() + " Failed: EPC = " + Convert.byteToHex(property.epc));
                  } else {
                        EV.e2 = Convert.byteArrayToInt(property.edt);
                  }
                  EV.notify();
            }
      }

      @Override
      protected void onGetRemainingBatteryCapacity3(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            synchronized (EV) {
                  super.onGetRemainingBatteryCapacity3(eoj, tid, esv, property, success);
                  if (!success) {
                        System.out.println("onGetProperty " + EV.name() + " Failed: EPC = " + Convert.byteToHex(property.epc));
                  } else {
                        EV.e4 = Convert.byteArrayToInt(property.edt);
                  }
                  EV.notify();
            }
      }

      public Handlable handlable;

      @Override
      protected boolean onSetProperty(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            boolean result = super.onSetProperty(eoj, tid, esv, property, success);
            handlable.handle(success);
            return result;
      }

      @Override
      public void setResultHandle(Handlable handlable) {
            this.handlable = handlable;
      }
}
