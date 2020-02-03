/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Receiver;

import com.sonycsl.echo.EchoProperty;
import com.sonycsl.echo.eoj.EchoObject;
import com.sonycsl.echo.eoj.device.DeviceObject;
import com.sonycsl.echo.eoj.profile.NodeProfile;
import com.sonycsl.echo.node.EchoNode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import Main.EchoController;

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

                        int count = 0;
                        for (DeviceObject device : EchoController.listDevice) {
                              boolean unavailable = true;
                              for (short echoClassCode : listDeviceAvailable) {
                                    if (device.getEchoClassCode() == echoClassCode) {
                                          unavailable = false;
                                          break;
                                    }
                              }
                              if (unavailable) { // Remove old node in Echo
                                    System.out.println("Removing: " + String.format("0x%04x", device.getEchoClassCode()));
                                    node.removeDevice(device);
                                    device.removeNode();
                                    EchoController.listDevice.remove(count);
                                    if (EchoController.MY_ECHO_EVENT_LISTENER.onGetEPC != null)
                                          EchoController.MY_ECHO_EVENT_LISTENER.onGetEPC.handleResult(false, new EchoProperty((byte) count));
                                    break;
                              }
                              count++;
                        }
                  }
            }
      }
}
