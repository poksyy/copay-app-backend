package com.copay.app.exception.notification;

import java.io.Serial;

public class NotificationAccessDeniedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public NotificationAccessDeniedException(String message) {
        super(message);
    }
}
