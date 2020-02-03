package Receiver;

import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.housingfacilities.ElectricVehicle;

import java.util.Timer;
import java.util.TimerTask;

import Common.Constants;
import Common.Convert;
import Model.OperationMode;
import Model.OperationStatus;

/**
 * @author hoang-trung-duc
 */
public class MyElectricVehicleReceiver extends ElectricVehicle.Receiver implements ResultControllable, ContinuouslyGotable {

      private OperationStatus operationStatus;
      private OperationMode operationMode;
      private int instantaneous, currentElectricEnergy, percentCurrent;
      private OnReceiveResult OnSetEPC = null;
      private OnReceiveResult OnGetEPC = null;
      private Timer timer = new Timer();
      private TimerTask continuousTask;

      public void setContinuousTask(TimerTask continuousTask) {
            this.continuousTask = continuousTask;
      }

      public OperationStatus getOperationStatus() {
            return operationStatus;
      }

      public OperationMode getOperationMode() {
            return operationMode;
      }

      public int getInstantaneous() {
            return instantaneous;
      }

      public int getCurrentElectricEnergy() {
            return currentElectricEnergy;
      }

      public int getPercentCurrent() {
            return percentCurrent;
      }

      @Override
      public OnReceiveResult getOnGetEPC() {
            return OnGetEPC;
      }

      public OnReceiveResult getOnSetEPC() {
            return OnSetEPC;
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
      protected void onGetRemainingBatteryCapacity1(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            super.onGetRemainingBatteryCapacity1(eoj, tid, esv, property, success);
            if (success) currentElectricEnergy = Convert.byteArrayToInt(property.edt);
            if (OnGetEPC != null) OnGetEPC.controlResult(success, property);
      }

      @Override
      protected void onGetRemainingBatteryCapacity3(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            super.onGetRemainingBatteryCapacity3(eoj, tid, esv, property, success);
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
      public void setOnReceive(OnReceiveResult OnSetEPC, OnReceiveResult OnGetEPC) {
            this.OnSetEPC = OnSetEPC;
            this.OnGetEPC = OnGetEPC;
      }

      @Override
      public void startContinuousTask() {
            timer.schedule(continuousTask, 0, Constants.PERIOD);
      }

      @Override
      public void stopContinuousTask() {
            timer.cancel();
      }
}
