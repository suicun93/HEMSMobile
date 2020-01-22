/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import androidx.annotation.NonNull;

/**
 * @author hoang-trung-duc
 */
public enum OperationMode {
      Other((byte) 0x40, "その他"),
      RapidCharging((byte) 0x41, "急速充電"),
      Charging((byte) 0x42, "充電"),
      Discharging((byte) 0x43, "放電"),
      Standby((byte) 0x44, "待機"),
      Test((byte) 0x45, "テスト"),
      Automatic((byte) 0x46, "自動"),
      Idle((byte) 0x47, "アイドル状態"),
      Restart((byte) 0x48, "再起動"),
      EffectiveCapacityRecalculationProcessing((byte) 0x49, "EffectiveCapacityRecalculationProcessing");

      // <editor-fold defaultstate="collapsed" desc="//Skip this">
      public static OperationMode from(byte value) {
            for (OperationMode mode : values()) {
                  if (mode.value == value) {
                        return mode;
                  }
            }
            return null;
      }

      @NonNull
      @Override
      public String toString() {
            return japaneseName;
      }

      public final byte value;
      public final String japaneseName;

      OperationMode(byte operation, String japaneseName) {
            this.value = operation;
            this.japaneseName = japaneseName;
      }
      // </editor-fold>
}
