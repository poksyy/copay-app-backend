package com.copay.app.exception.notification;

import java.io.Serial;

public class NotificationNotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  public NotificationNotFoundException(String message) {
    super(message);
  }
}