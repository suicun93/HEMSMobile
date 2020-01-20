package Model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author hoang-trung-duc
 */
public enum OperationStatus {
    ON((byte) 0x30),
    OFF((byte) 0x31);

    // <editor-fold defaultstate="collapsed" desc="//Skip this">
    public static OperationStatus from(byte value) {
        return value == ON.value ? ON : OFF;
    }

    public final byte value;

    OperationStatus(byte operation) {
        this.value = operation;
    }

    // </editor-fold>
}
