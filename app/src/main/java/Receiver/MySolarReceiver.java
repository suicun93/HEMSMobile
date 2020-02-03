package Receiver;

import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.housingfacilities.HouseholdSolarPowerGeneration;

import java.util.Timer;
import java.util.TimerTask;

import Common.Constants;
import Common.Convert;
import Model.OperationStatus;


/**
 * @author hoang-trung-duc
 */
public class MySolarReceiver extends HouseholdSolarPowerGeneration.Receiver implements ResultControllable, ContinuouslyGotable {
      private OperationStatus operationStatus;
      private int instantaneous, currentElectricEnergy;
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

      public int getInstantaneous() {
            return instantaneous;
      }

      public int getCurrentElectricEnergy() {
            return currentElectricEnergy;
      }

      public OnReceiveResult getOnSetEPC() {
            return OnSetEPC;
      }

      @Override
      public OnReceiveResult getOnGetEPC() {
            return OnGetEPC;
      }

      @Override
      protected void onGetOperationStatus(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            super.onGetOperationStatus(eoj, tid, esv, property, success);
            if (success) operationStatus = OperationStatus.from(property.edt[0]);
            if (OnGetEPC != null) OnGetEPC.controlResult(success, property);
      }

      @Override
      protected void onGetMeasuredInstantaneousAmountOfElectricityGenerated(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            super.onGetMeasuredInstantaneousAmountOfElectricityGenerated(eoj, tid, esv, property, success);
            if (success) instantaneous = Convert.byteArrayToInt(property.edt);
            if (OnGetEPC != null) OnGetEPC.controlResult(success, property);
      }

      @Override
      protected void onGetMeasuredCumulativeAmountOfElectricityGenerated(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            super.onGetMeasuredCumulativeAmountOfElectricityGenerated(eoj, tid, esv, property, success);
            if (success) currentElectricEnergy = Convert.byteArrayToInt(property.edt);
            if (OnGetEPC != null) OnGetEPC.controlResult(success, property);

      }

      @Override
      protected boolean onSetProperty(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            boolean result = super.onSetProperty(eoj, tid, esv, property, success);
            if (OnSetEPC != null) OnSetEPC.controlResult(success);
            return result;
      }

      @Override
      public void setOnReceive(OnReceiveResult onSetEPC, OnReceiveResult onGetEPC) {
            this.OnSetEPC = onSetEPC;
            this.OnGetEPC = onGetEPC;
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
