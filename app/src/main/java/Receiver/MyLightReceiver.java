package Receiver;

import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.housingfacilities.GeneralLighting;

import Model.OperationStatus;

/**
 * @author hoang-trung-duc
 */
public class MyLightReceiver extends GeneralLighting.Receiver implements ResultControllable {
      private OperationStatus operationStatus;
      private OnReceiveResultListener OnSetEPC = null;
      private OnReceiveResultListener OnGetEPC = null;

      public OnReceiveResultListener getOnSetEPC() {
            return OnSetEPC;
      }

      @Override
      public OnReceiveResultListener getOnGetListener() {
            return OnGetEPC;
      }

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
}
