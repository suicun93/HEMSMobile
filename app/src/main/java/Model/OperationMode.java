/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author hoang-trung-duc
 */
public enum OperationMode {
    Other((byte) 0x40),
    RapidCharging((byte) 0x41),
    Charging((byte) 0x42),
    Discharging((byte) 0x43),
    Standby((byte) 0x44),
    Test((byte) 0x45),
    Automatic((byte) 0x46),
    Idle((byte) 0x47),
    Restart((byte) 0x48),
    EffectiveCapacityRecalculationProcessing((byte) 0x49);

    // <editor-fold defaultstate="collapsed" desc="//Skip this">
    public static OperationMode from(byte value) {
        for (OperationMode mode : values()) {
            if (mode.value == value) {
                return mode;
            }
        }
        return null;
    }

    public final byte value;

    OperationMode(byte operation) {
        this.value = operation;
    }
    // </editor-fold>
}
