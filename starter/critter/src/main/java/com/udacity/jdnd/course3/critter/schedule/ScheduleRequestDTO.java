package com.udacity.jdnd.course3.critter.schedule;

import java.time.LocalDate;

public class ScheduleRequestDTO {
    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
