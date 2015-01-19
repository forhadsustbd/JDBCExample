package com.tigerit.jdbc;

/**
 * Created by forhad on 12/29/14.
 */
public class Packet {

    private int key, value;

    public Packet(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
