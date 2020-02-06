package Receiver;

import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.housingfacilities.HouseholdSolarPowerGeneration;

import java.util.Timer;
import java.util.TimerTask;

import Common.Constants;
import Common.Convert;
import Model.OperationStatus;
import Receiver.Thread.ContinuouslyGotable;
import Receiver.EPCGetter.CurrentElectricEnergyGettable;
import Receiver.EPCGetter.InstantaneousGettable;
import Receiver.Thread.OnException;
import Receiver.OnGetSetListener.OnReceiveResultListener;
import Receiver.EPCGetter.OperationStatusGettable;
import Receiver.OnGetSetListener.ResultControllable;
import Receiver.Thread.Updatable;

/**
 * @author hoang-trung-duc
 */
public class MySolarReceiver extends HouseholdSolarPowerGeneration.Receiver implements
          ResultControllable,
          ContinuouslyGotable,
          OperationStatusGettable,
          InstantaneousGettable,
          CurrentElectricEnergyGettable,
          Updatable {
      private OperationStatus operationStatus;
      private int instantaneous, currentElectricEnergy;
      private OnReceiveResultListener OnSetEPC = null;
      private OnReceiveResultListener OnGetEPC = null;
      private Timer timer = new Timer();
      private TimerTask continuousTask;
      private final HouseholdSolarPowerGeneration solar;

      public MySolarReceiver(HouseholdSolarPowerGeneration solar) {
            this.solar = solar;
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
      public int getInstantaneous() {
            return instantaneous;
      }

      @Override
      public int getCurrentElectricEnergy() {
            return currentElectricEnergy;
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
      public void setOnReceiveListener(OnReceiveResultListener onSetEPC, OnReceiveResultListener onGetEPC) {
            this.OnSetEPC = onSetEPC;
            this.OnGetEPC = onGetEPC;
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
            new Thread(new Runnable() {
                  @Override
                  public void run() {
                        try {
                              solar.get().reqGetOperationStatus().send();
                              solar.get().reqGetMeasuredInstantaneousAmountOfElectricityGenerated().send();
                              solar.get().reqGetMeasuredCumulativeAmountOfElectricityGenerated().send(); // E1
                        } catch (Exception e) {
                              onException.handle(e);
                        }
                  }
            }).start();
      }
}
