package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class ScheduleDAOImpl implements ScheduleDAO {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    private static final String SCHEDULE_ID = "scheduleId";
    private static final String EMPLOYEE_ID = "employeeId";
    private static final String PET_ID = "petId";
    private static final String ACTIVITY_ID = "activityId";

    private static final String INSERT_EMPLOYEE = "INSERT INTO employee_schedule (employee_id, schedule_id) VALUES (:" + EMPLOYEE_ID + ", :" + SCHEDULE_ID + ")";
    private static final String FIND_EMPLOYEE = "SELECT s.employee_id FROM employee_schedule s WHERE schedule_id = :" + SCHEDULE_ID;
    private static final String FIND_SCHEDULE_BY_EMPLOYEE = "SELECT s.schedule_id FROM employee_schedule s WHERE employee_id = :" + EMPLOYEE_ID;

    private static final String INSERT_PET = "INSERT INTO pet_schedule (pet_id, schedule_id) VALUES (:" + PET_ID + ", :" + SCHEDULE_ID + ")";
    private static final String FIND_PET = "SELECT s.pet_id FROM pet_schedule s WHERE schedule_id = :" + SCHEDULE_ID;
    private static final String FIND_SCHEDULE_BY_PET = "SELECT s.schedule_id FROM pet_schedule s WHERE pet_id = :" + PET_ID;

    private static final String INSERT_ACTIVITY = "INSERT INTO activity_schedule (activity_id, schedule_id) VALUES (:" + ACTIVITY_ID + ", :" + SCHEDULE_ID + ")";
    private static final String FIND_ACTIVITY = "SELECT s.activity_id FROM activity_schedule s WHERE schedule_id = :" + SCHEDULE_ID;

    @Override
    public void addEmployeeBySchedule(Long employeeId, Long scheduleId) {
        jdbcTemplate.update(
                INSERT_EMPLOYEE,
                new MapSqlParameterSource()
                        .addValue(EMPLOYEE_ID, employeeId)
                        .addValue(SCHEDULE_ID, scheduleId));
    }

    @Override
    public List<Long> findEmployeeBySchedule(Long scheduleId) {
        return jdbcTemplate.queryForList(FIND_EMPLOYEE,
                new MapSqlParameterSource().addValue(SCHEDULE_ID, scheduleId),
                Long.class);
    }

    @Override
    public List<Long> findScheduleByEmployee(Long employeeId) {
        return jdbcTemplate.queryForList(FIND_SCHEDULE_BY_EMPLOYEE,
                new MapSqlParameterSource().addValue(EMPLOYEE_ID, employeeId),
                Long.class);
    }

    @Override
    public void addPetBySchedule(Long petId, Long scheduleId) {
        jdbcTemplate.update(
                INSERT_PET,
                new MapSqlParameterSource()
                        .addValue(PET_ID, petId)
                        .addValue(SCHEDULE_ID, scheduleId));
    }

    @Override
    public List<Long> findPetBySchedule(Long scheduleId) {
        return jdbcTemplate.queryForList(FIND_PET,
                new MapSqlParameterSource().addValue(SCHEDULE_ID, scheduleId),
                Long.class);
    }

    @Override
    public List<Long> findScheduleByPet(Long petId) {
        return jdbcTemplate.queryForList(FIND_SCHEDULE_BY_PET,
                new MapSqlParameterSource().addValue(PET_ID, petId),
                Long.class);
    }

    @Override
    public void addActivityBySchedule(EmployeeSkill skill, Long scheduleId) {
        jdbcTemplate.update(
                INSERT_ACTIVITY,
                new MapSqlParameterSource()
                        .addValue(ACTIVITY_ID, skill.getDbValue())
                        .addValue(SCHEDULE_ID, scheduleId));
    }

    @Override
    public List<EmployeeSkill> findActivityBySchedule(Long scheduleId) {
        List<Integer> results = jdbcTemplate.queryForList(FIND_ACTIVITY,
                new MapSqlParameterSource().addValue(SCHEDULE_ID, scheduleId),
                Integer.class);

        return results.stream().map(EmployeeSkill::fromDbValue).collect(Collectors.toList());
    }
}
