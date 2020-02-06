package Receiver;

import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.housingfacilities.Battery;

import java.util.Timer;
import java.util.TimerTask;

import Common.Constants;
import Common.Convert;
import Model.OperationMode;
import Model.OperationStatus;
import Receiver.Thread.ContinuouslyGotable;
import Receiver.EPCGetter.CurrentElectricEnergyGettable;
import Receiver.EPCGetter.CurrentPercentGettable;
import Receiver.EPCGetter.InstantaneousGettable;
import Receiver.OnGetSetListener.OnReceiveResultListener;
import Receiver.EPCGetter.OperationModeGettable;
import Receiver.EPCGetter.OperationStatusGettable;
import Receiver.OnGetSetListener.ResultControllable;
import Receiver.Thread.OnException;
import Receiver.Thread.Updatable;

/**
 * @author hoang-trung-duc
 */
public class MyBatteryReceiver extends Battery.Receiver implements
          ResultControllable,
          ContinuouslyGotable,
          OperationStatusGettable,
          OperationModeGettable,
          InstantaneousGettable,
          CurrentElectricEnergyGettable,
          CurrentPercentGettable, Updatable {
      private OperationStatus operationStatus;
      private OperationMode operationMode;
      private int instantaneous, currentElectricEnergy, percentCurrent;
      private OnReceiveResultListener OnSetEPC = null;
      private OnReceiveResultListener OnGetEPC = null;
      private Timer timer = new Timer();
      private TimerTask continuousTask;
      private final Battery battery;

      public MyBatteryReceiver(Battery battery) {
            this.battery = battery;
      }

      @Override
      public void setUpdateTask(TimerTask continuousTask) {
            this.continuousTask = continuousTask;
      }

      @Override
      public OperationStatus getOperationStatus() {
            return operationStatus;
      }

      @Override
      public OperationMode getOperationMode() {
            return operationMode;
      }

      @Override
      public int getInstantaneous() {
            return instantaneous;
      }

      @Override
      public int getCurrentElectricEnergy() {
            return currentElectricEnergy;
      }

      @Override
      public int getPercentCurrent() {
            return percentCurrent;
      }

      @Override
      public OnReceiveResultListener getOnGetListener() {
            return OnGetEPC;
      }

      @Override
      protected void onGetOperationStatus(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            super.onGetOperationStatus(eoj, tid, esv, property, success);
            if (success) operationStatus = OperationStatus.from(property.edt[0]);
            if (OnGetEPC != null) OnGetEPC.controlResult(success, property);
      }

      @Override
      protected void onGetOperationModeSetting(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            super.onGetOperationModeSetting(eoj, tid, esv, property, success);
            if (success) operationMode = OperationMode.from(property.edt[0]);
            if (OnGetEPC != null) OnGetEPC.controlResult(success, property);
      }

      @Override
      protected void onGetMeasuredInstantaneousChargeDischargeElectricEnergy(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            super.onGetMeasuredInstantaneousChargeDischargeElectricEnergy(eoj, tid, esv, property, success);
            if (success) instantaneous = Convert.byteArrayToInt(property.edt);
            if (OnGetEPC != null) OnGetEPC.controlResult(success, property);
      }

      @Override
      protected void onGetRemainingStoredElectricity1(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            super.onGetRemainingStoredElectricity1(eoj, tid, esv, property, success);
            if (success) currentElectricEnergy = Convert.byteArrayToInt(property.edt);
            if (OnGetEPC != null) OnGetEPC.controlResult(success, property);
      }

      @Override
      protected void onGetRemainingStoredElectricity3(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            super.onGetRemainingStoredElectricity3(eoj, tid, esv, property, success);
            if (success) percentCurrent = Convert.byteArrayToInt(property.edt);
            if (OnGetEPC != null) OnGetEPC.controlResult(success, property);
      }

      @Override
      protected boolean onSetProperty(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            boolean result = super.onSetProperty(eoj, tid, esv, property, success);
            if (OnSetEPC != null) OnSetEPC.controlResult(success);
            return result;
      }

      @Override
      public void setOnReceiveListener(OnReceiveResultListener OnSetEPC, OnReceiveResultListener OnGetEPC) {
            this.OnSetEPC = OnSetEPC;
            this.OnGetEPC = OnGetEPC;
      }

      @Override
      public void startUpdateTask() {
            timer.schedule(continuousTask, 0, Constants.PERIOD);
      }

      @Override
      public void stopUpdateTask() {
            timer.cancel();
      }

      @Override
      public void update(OnException onException) {
            new Thread(() -> {
                  try {
                        battery.get().reqGetOperationStatus().send();
                        battery.get().reqGetOperationModeSetting().send();
                        battery.get().reqGetMeasuredInstantaneousChargeDischargeElectricEnergy().send();
                        battery.get().reqGetRemainingStoredElectricity1().send(); // E2
                        battery.get().reqGetRemainingStoredElectricity3().send(); // E4
                  } catch (Exception e) {
                        onException.handle(e);
                  }
            }).start();
      }
}
