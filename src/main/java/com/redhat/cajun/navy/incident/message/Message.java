package com.redhat.cajun.navy.incident.message;

import java.util.UUID;

public class Message<T> {

    private String id;

    private String messageType;

    private String invokingService;

    private long timestamp;

    private T body;

    public String getMessageType() {
        return messageType;
    }

    public String getId() {
        return id;
    }

    public String getInvokingService() {
        return invokingService;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public T getBody() {
        return body;
    }

    public static class Builder<T> {

        private final Message<T> message;

        public Builder(String messageType, String invokingService, T body) {

            message = new Message<>();
            message.messageType = messageType;
            message.invokingService = invokingService;
            message.body = body;
            message.id = UUID.randomUUID().toString();
            message.timestamp = System.currentTimeMillis();
        }

        public Builder<T> id(String id) {
            message.id = id;
            return this;
        }

        public Builder<T> timestamp(long timestamp) {
            message.timestamp = timestamp;
            return this;
        }

        public Message<T> build() {
            return message;
        }
    }
}
