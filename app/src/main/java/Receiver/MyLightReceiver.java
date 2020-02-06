package Receiver;

import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.housingfacilities.GeneralLighting;

import Model.OperationStatus;
import Receiver.Thread.OnException;
import Receiver.OnGetSetListener.OnReceiveResultListener;
import Receiver.EPCGetter.OperationStatusGettable;
import Receiver.OnGetSetListener.ResultControllable;
import Receiver.Thread.Updatable;

/**
 * @author hoang-trung-duc
 */
public class MyLightReceiver extends GeneralLighting.Receiver implements
          ResultControllable,
          OperationStatusGettable,
          Updatable {
      private OperationStatus operationStatus;
      private OnReceiveResultListener OnSetEPC = null;
      private OnReceiveResultListener OnGetEPC = null;
      private final GeneralLighting light;

      public MyLightReceiver(GeneralLighting light) {
            this.light = light;
      }

      @Override
      public OnReceiveResultListener getOnGetListener() {
            return OnGetEPC;
      }

      @Override
      public OperationStatus getOperationStatus() {
            return operationStatus;
      }

      @Override
      protected void onGetOperationStatus(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            super.onGetOperationStatus(eoj, tid, esv, property, success);
            if (success) operationStatus = OperationStatus.from(property.edt[0]);
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
      public void update(OnException onException) {
            new Thread(() -> {
                  try {
                        light.get().reqGetOperationStatus().send();
                  } catch (Exception e) {
                        onException.handle(e);
                  }
            }).start();
      }
}
