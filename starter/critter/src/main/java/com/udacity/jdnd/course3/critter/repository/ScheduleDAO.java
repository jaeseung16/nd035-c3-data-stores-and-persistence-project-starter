package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.user.EmployeeSkill;

import java.util.List;

public interface ScheduleDAO {
    void addEmployeeBySchedule(Long employeeId, Long scheduleId);
    List<Long> findEmployeeBySchedule(Long scheduleId);
    List<Long> findScheduleByEmployee(Long employeeId);
    void deleteEmployeeBySchedule(Long scheduleId);

    void addPetBySchedule(Long petId, Long scheduleId);
    List<Long> findPetBySchedule(Long scheduleId);
    List<Long> findScheduleByPet(Long petId);
    void deletePetsBySchedule(Long scheduleId);

    void addActivityBySchedule(EmployeeSkill skill, Long scheduleId);
    List<EmployeeSkill> findActivityBySchedule(Long scheduleId);
    void deleteActivitiesBySchedule(Long scheduleId);
}
