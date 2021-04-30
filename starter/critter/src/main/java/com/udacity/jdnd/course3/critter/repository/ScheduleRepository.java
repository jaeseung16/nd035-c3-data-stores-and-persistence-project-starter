package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.entity.Schedule;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ScheduleRepository {
    @PersistenceContext
    EntityManager entityManager;

    public void persist(Schedule schedule) {
        entityManager.persist(schedule);
    }

    public Schedule find(Long id) {
        return entityManager.find(Schedule.class, id);
    }

    public void merge(Schedule schedule) {
        entityManager.merge(schedule);
    }

    public void delete(Schedule schedule) {
        entityManager.remove(schedule);
    }

    public List<Schedule> getAllSchedules() {
        return entityManager
                .createNamedQuery("Schedule.findAll", Schedule.class)
                .getResultList();
    }
}
