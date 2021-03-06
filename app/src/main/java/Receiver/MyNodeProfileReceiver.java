/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Receiver;

import android.util.Log;

import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.DeviceObject;
import com.sonycsl.echo.eoj.profile.NodeProfile;
import com.sonycsl.echo.node.EchoNode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import Common.Constants;
import Main.EchoController;
import Receiver.OnGetSetListener.ResultControllable;
import Receiver.Thread.ContinuouslyGotable;

/**
 * @author hoang-trung-duc
 */
public class MyNodeProfileReceiver extends NodeProfile.Receiver {

      private final EchoNode node;

      public MyNodeProfileReceiver(EchoNode node) {
            this.node = node;
      }

      @Override
      protected void onGetInstanceListNotification(EchoObject eoj, short tid, byte esv, EchoProperty property, boolean success) {
            //                            System.out.println("onGetInstanceListNotification Node = " + node.getNodeProfile());
            //                            System.out.println("--------");
            // TODO: Communicate with Sony's author about this problem.
            super.onGetInstanceListNotification(eoj, tid, esv, property, success);
            if (success) {
                  if (property.edt[0] < node.getDevices().length) { // Something stopped
                        byte[] data = property.edt;

                        // List Device Available
                        short[] listDeviceAvailable = new short[data[0]];
                        for (int i = 0; i < data[0]; i++) {
                              ByteBuffer bb = ByteBuffer.allocate(2);
                              bb.order(ByteOrder.LITTLE_ENDIAN);
                              bb.put(data[i * 3 + 2]);
                              bb.put(data[i * 3 + 1]);
                              listDeviceAvailable[i] = bb.getShort(0);
                        }

                        for (DeviceObject device : EchoController.listDevice) {
                              boolean unavailable = true;
                              for (short echoClassCode : listDeviceAvailable) {
                                    if (device.getEchoClassCode() == echoClassCode) {
                                          unavailable = false;
                                          break;
                                    }
                              }
                              if (unavailable) { // Remove old node in Echo
                                    onRemoveDevice(device);
                                    break;
                              }
                        }
                  }
            }
      }

      private void onRemoveDevice(DeviceObject deviceObject) {
            // Notify
            int position = EchoController.listDevice.indexOf(deviceObject);
            if (EchoController.MY_ECHO_EVENT_LISTENER.getOnItemSetChangingListener() != null)
                  EchoController.MY_ECHO_EVENT_LISTENER.getOnItemSetChangingListener().controlResult(false, new EchoProperty((byte) position));

            // Stop current thread
            EchoObject.Receiver receiver = deviceObject.getReceiver();
            if (receiver instanceof ContinuouslyGotable)
                  ((ContinuouslyGotable) deviceObject.getReceiver()).stopUpdateTask();
            if (receiver instanceof ResultControllable &&
                      ((ResultControllable) receiver).getOnGetListener() != null) {
                  ((ResultControllable) receiver).disappear();
                  ((ResultControllable) deviceObject.getReceiver()).setOnReceiveListener(null, null);
            }

            // Remove device from Node
            deviceObject.getNode().removeDevice(deviceObject);
            deviceObject.removeNode();
            EchoController.listDevice.remove(deviceObject);
            Log.d(Constants.ECHO_TAG, "onGetInstanceListNotification: " + "Removed: " + String.format("0x%04x", deviceObject.getEchoClassCode()));
      }
}
