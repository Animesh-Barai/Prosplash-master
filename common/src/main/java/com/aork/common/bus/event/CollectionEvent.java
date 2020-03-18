package com.aork.common.bus.event;

import com.aork.base.unsplash.Collection;

public class CollectionEvent {

    public Collection collection;
    public Event event;

    public enum Event {
        UPDATE, CREATE, DELETE
    }

    public CollectionEvent(Collection collection, Event event) {
        this.collection = collection;
        this.event = event;
    }
}
