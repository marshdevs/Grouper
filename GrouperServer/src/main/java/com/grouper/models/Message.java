package com.grouper.models;

import java.sql.Date;
import java.time.Instant;

public class Message {

    // Status codes
    public static final int DEFAULT_SUCCESS_STATUS = 200;
    public static final int DEFAULT_FAILURE_STATUS = 400;
    public static final int NOT_FOUND_STATUS = 404;

    // Default context strings
    public static final String DEFAULT_SUCCESS_DESC = "Operation succeeded.";
    public static final String DEFAULT_FAILURE_DESC = "Operation failed.";
    public static final String FALLBACK_PATH_DESC = "Endpoint not found.";

    // Amazon Debugging Strings
    public static final String AWS_GET_SUCCESS = "AWS: GET request succeeded.";
    public static final String AWS_PUT_SUCCESS = "AWS: PUT request succeeded.";
    public static final String AWS_UPDATE_SUCCESS = "AWS: UPDATE request succeeded.";
    public static final String AWS_DELETE_SUCCESS = "AWS: DELETE request succeeded.";

    // Amazon Service Exception Error Strings
    public static final String AWS_GET_FAILURE = "ASE: GET request failed. Desired entry may not exist, or provided " +
        "key may be invalid.";
    public static final String AWS_PUT_FAILURE = "ASE: PUT request failed. An entry may already exist under the " +
        "provided key, or the provided AttributeValue may be improperly formatted.";
    public static final String AWS_UPDATE_FAILURE = "ASE: UPDATE request failed. Desired entry may not exist, or the " +
        "provided AttributeValue may be improperly formatted.";
    public static final String AWS_DELETE_FAILURE = "ASE: DELETE request failed. Desired entry may not exist.";

    private static final String DEFAULT_EMPTY_FIELD = "EMPTY";
    private static final String DEFAULT_EMPTY_VALUE = null;

    private final Integer status;
    private final String description;
    private final String field;
    private final Object value;

    private Message(MessageBuilder builder) {
        this.status = builder.status;
        this.description = builder.description;
        this.field = builder.field;
        this.value = builder.value;
    }

    public static class MessageBuilder {

        private final Integer status;
        private String description;
        private String field = DEFAULT_EMPTY_FIELD;
        private Object value = DEFAULT_EMPTY_VALUE;


        public MessageBuilder(Integer status) {
            this.status = status;

            if (status == DEFAULT_SUCCESS_STATUS) {
                this.description = DEFAULT_SUCCESS_DESC;
            } else if (status == NOT_FOUND_STATUS) {
                this.description = FALLBACK_PATH_DESC;
            }
            else {
                this.description = DEFAULT_FAILURE_DESC;
            }
        }

        public MessageBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public MessageBuilder withField(String field) {
            this.field = field;
            return this;
        }

        public MessageBuilder withValue(Object value) {
            this.value = value;
            return this;
        }

        public Message build() {
            return new Message(this);
        }

    }

    public Integer getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        String message = new String();
        message += ":----------:\n";
        if (this.status == DEFAULT_FAILURE_STATUS) {
            message += "ERROR:\n";
        } else if (this.status == DEFAULT_SUCCESS_STATUS) {
            message += "DEBUG:\n";
        }
        message += " TIME: " + Date.from(Instant.now()).toString() + "\n";
        message += " STATUS: " + this.status.toString() + "\n";
        message += " Description: " + this.description + "\n";
        message += " Field: " + this.field + "\n";
        message += " Value: " + this.value.toString();

        return message;
    }

}
