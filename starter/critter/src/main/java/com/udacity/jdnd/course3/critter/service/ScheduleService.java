package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private EmployeeService employeeService;

    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
        List<Long> scheduleIdList = new ArrayList<>();

        for (Long petId : scheduleDTO.getPetIds()) {
            for (Long employeeId : scheduleDTO.getEmployeeIds()) {
                EmployeeDTO employeeDTO = employeeService.getEmployee(employeeId);

                if (employeeDTO.getDaysAvailable().contains(scheduleDTO.getDate().getDayOfWeek())) {
                    for (EmployeeSkill skill : scheduleDTO.getActivities()) {
                        if (employeeDTO.getSkills().contains(skill)) {
                            Schedule schedule = new Schedule();
                            schedule.setDate(scheduleDTO.getDate());
                            schedule.setPetId(petId);
                            schedule.setEmployeeId(employeeId);
                            schedule.setActivity(skill);

                            scheduleRepository.persist(schedule);
                            scheduleIdList.add(schedule.getId());
                        }
                    }
                }
            }
        }

        ScheduleDTO scheduleDTOToReturn = new ScheduleDTO();
        Set<Long> employeeIds = new HashSet<>();
        Set<Long> petIds = new HashSet<>();
        Set<EmployeeSkill> activities = new HashSet<>();

        for (Long scheduleId : scheduleIdList) {
            Schedule schedule = scheduleRepository.find(scheduleId);
            if (scheduleDTO.getDate().equals(schedule.getDate())) {
                employeeIds.add(schedule.getEmployeeId());
                petIds.add(schedule.getPetId());
                activities.add(schedule.getActivity());
            }
        }

        scheduleDTOToReturn.setDate(scheduleDTO.getDate());
        scheduleDTOToReturn.setPetIds(new ArrayList<>(petIds));
        scheduleDTOToReturn.setEmployeeIds(new ArrayList<>(employeeIds));
        scheduleDTOToReturn.setActivities(activities);

        logger.info("Returning scheduleDTO = {}", scheduleDTOToReturn);
        return scheduleDTOToReturn;
    }

    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> scheduleList = scheduleRepository.getAllSchedules();
        List<ScheduleDTO> scheduleDTOList = scheduleList.stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());

        logger.info("Returning scheduleDTOList = {}", scheduleDTOList);
        return scheduleDTOList;

    }

    private ScheduleDTO convertEntityToDTO(Schedule schedule) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setDate(schedule.getDate());

        scheduleDTO.setPetIds(Collections.singletonList(schedule.getPetId()));
        scheduleDTO.setEmployeeIds(Collections.singletonList(schedule.getEmployeeId()));
        scheduleDTO.setActivities(Collections.singleton(schedule.getActivity()));

        return scheduleDTO;
    }
}
