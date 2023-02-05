package com.roboworks.gui;

import jssc.SerialPort;
import jssc.SerialPortException;

import static com.roboworks.gui.BluetoothController.Commands.*;

public class BluetoothController {
    private SerialPort serialPort;

    public BluetoothController(String port) {
        serialPort = new SerialPort(port);
    }

    public void open() {
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        byte[] buffer = new byte[5];
        buffer[0] = START;
        buffer[4] = END;

        switch (message.getType()) {
            case DELIVER:
                buffer[1] = DELIVER;
                break;
            case RETRIEVE:
                buffer[1] = RETRIEVE;
                break;
        }
        if (message.getPosition().getX() > Byte.MAX_VALUE || message.getPosition().getY() > Byte.MAX_VALUE ||
                message.getPosition().getX() < Byte.MIN_VALUE || message.getPosition().getY() < Byte.MIN_VALUE)
            throw new IllegalArgumentException("Coordinates too large");

        buffer[2] = (byte) message.getPosition().getX();
        buffer[3] = (byte) message.getPosition().getY();

        try {
            serialPort.writeBytes(buffer);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    protected static class Commands {
        public static final byte START = ~0;
        public static final byte END = 0;
        public static final byte EMERGENCY_STOP = 1;
        public static final byte EMERGENCY_RESUME = 2;
        public static final byte DELIVER = 20;
        public static final byte RETRIEVE = 21;
        public static final byte GET_QUEUE = 30;
        public static final byte SEND_QUEUE = 31;
    }
}
