package com.copay.app.service.user;

import com.copay.app.entity.User;

public interface UserAvailabilityService {
    void checkUserExistence(User user);
}
