package com.example.simple_webflux;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventNotify {

    private List<String> events = new ArrayList<>();
    private boolean change = false;

    public void add(String data) {
        events.add(data);
        change = true;
    }

    public boolean isChange() {
        return change ? true : false;
    }

    public void changeChange() {
        this.change = !this.change;
    }

    public List<String> getEvents() {
        return events;
    }
}
