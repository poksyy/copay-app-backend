package com.copay.app.exception.group;

import java.io.Serial;

public class GroupAccessDeniedException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

    public GroupAccessDeniedException(String message) {
        super(message);
    }
}
