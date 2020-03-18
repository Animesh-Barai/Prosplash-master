package com.aork.common.bus.event;

import androidx.annotation.NonNull;

import com.aork.base.unsplash.User;

public class FollowEvent {

    @NonNull public User target;

    public FollowEvent(@NonNull User target) {
        this.target = target;
    }
}
