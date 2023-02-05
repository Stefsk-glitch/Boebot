package com.roboworks.gui;

public class Message {
    private Type type;
    private Position position;

    public Message(Type type, Position position) {
        this.type = type;
        this.position = position;
    }

    public Type getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public enum Type {
        DELIVER,
        RETRIEVE
    }
}
