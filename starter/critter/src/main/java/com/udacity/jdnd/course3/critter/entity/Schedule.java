package com.udacity.jdnd.course3.critter.entity;

import javax.persistence.*;
import java.time.LocalDate;

@NamedQueries({
        @NamedQuery(name = "Schedule.findAll", query = "SELECT s from Schedule s")
})
@Entity
public class Schedule {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}
