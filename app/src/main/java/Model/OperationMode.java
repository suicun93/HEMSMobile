/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import AView.R;

/**
 * @author hoang-trung-duc
 */
public enum OperationMode {
      Other(
                (byte) 0x40,
                R.string.operation_name_other),
      RapidCharging(
                (byte) 0x41,
                R.string.operation_name_rapid_charging),
      Charging(
                (byte) 0x42,
                R.string.operation_name_charging),
      Discharging(
                (byte) 0x43,
                R.string.operation_name_discharging),
      Standby(
                (byte) 0x44,
                R.string.operation_name_standby),
      Test(
                (byte) 0x45,
                R.string.operation_name_test),
      Automatic(
                (byte) 0x46,
                R.string.operation_name_automatic),
      Idle(
                (byte) 0x47,
                R.string.operation_name_idle),
      Restart(
                (byte) 0x48,
                R.string.operation_name_restart);
      // Too long parameters makes my source code ugly.
//      EffectiveCapacityRecalculationProcessing((byte) 0x49, "EffectiveCapacityRecalculationProcessing");

      public final int name;

      public final byte value;

      OperationMode(byte operation, int name) {
            this.value = operation;
            this.name = name;
      }

      // <editor-fold defaultState="collapsed" desc="//Skip this">
      public static OperationMode from(byte value) {
            for (OperationMode mode : values()) {
                  if (mode.value == value) {
                        return mode;
                  }
            }
            return null;
      }
      // </editor-fold>
}
