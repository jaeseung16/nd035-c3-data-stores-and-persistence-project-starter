package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.repository.ScheduleDAO;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import com.udacity.jdnd.course3.critter.schedule.ScheduleRequestDTO;
import com.udacity.jdnd.course3.critter.user.CustomerDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScheduleService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleDAO scheduleDAO;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CustomerService customerService;

    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
        logger.info("Creating schedule with request={}", scheduleDTO);
        Schedule schedule = new Schedule();
        schedule.setDate(scheduleDTO.getDate());

        scheduleRepository.persist(schedule);
        Long scheduleId = schedule.getId();
        scheduleDTO.getEmployeeIds()
                .forEach(employeeId -> scheduleDAO.addEmployeeBySchedule(employeeId, scheduleId));
        scheduleDTO.getPetIds()
                .forEach(petId -> scheduleDAO.addPetBySchedule(petId, scheduleId));
        scheduleDTO.getActivities()
                .forEach(activity -> scheduleDAO.addActivityBySchedule(activity, scheduleId));
        logger.info("Persisted schedule: Id={}", schedule.getId());

        ScheduleDTO scheduleDTOToReturn = getDTOByScheduleId(scheduleId);

        logger.info("Returning {}", scheduleDTOToReturn);
        return scheduleDTOToReturn;
    }

    public List<ScheduleDTO> getAllSchedules() {
        logger.info("Retrieving all schedules");
        List<ScheduleDTO> results = new ArrayList<>();

        List<Schedule> scheduleList = scheduleRepository.getAllSchedules();

        if (scheduleList.isEmpty()) {
            logger.error("No schedules found");
            throw new ScheduleNotFoundException("No schedules found");
        }

        for (Schedule schedule : scheduleList) {
            results.add(getDTOByScheduleId(schedule.getId()));
        }

        logger.info("Returning {} schedules", results.size());
        return results;
    }

    public ScheduleDTO updateSchedule(ScheduleRequestDTO scheduleRequestDTO, Long scheduleId) {
        logger.info("Updating a schedule: id={}", scheduleId);

        Schedule schedule = scheduleRepository.find(scheduleId);

        if (schedule == null) {
            logger.warn("Cannot find a schedule for a given id = {}", scheduleId);
            throw new ScheduleNotFoundException("Cannot find a schedule for a given id = " + scheduleId + ".");
        }

        schedule.setDate(scheduleRequestDTO.getDate());
        scheduleRepository.merge(schedule);

        ScheduleDTO scheduleDTO = getDTOByScheduleId(scheduleId);

        logger.info("Returning {}", scheduleDTO);
        return scheduleDTO;
    }

    public void deleteSchedule(Long scheduleId) {
        logger.info("Deleting a schedule: id={}", scheduleId);

        Schedule schedule = scheduleRepository.find(scheduleId);
        if (schedule == null) {
            logger.warn("Cannot find a schedule for a given id = {}", scheduleId);
            throw new ScheduleNotFoundException("Cannot find a schedule for a given id = " + scheduleId + ".");
        }

        scheduleDAO.deleteEmployeeBySchedule(scheduleId);
        scheduleDAO.deletePetsBySchedule(scheduleId);
        scheduleDAO.deleteActivitiesBySchedule(scheduleId);

        scheduleRepository.delete(schedule);
    }

    public List<ScheduleDTO> getScheduleForEmployee(long employeeId) {
        logger.info("Retrieving schedules for an employee: id={}", employeeId);
        List<ScheduleDTO> results = new ArrayList<>();

        List<Long> scheduleIds = scheduleDAO.findScheduleByEmployee(employeeId);

        if (scheduleIds.isEmpty()) {
            logger.warn("Cannot find any schedules for a given employeeId = {}", employeeId);
            throw new ScheduleNotFoundException("Cannot find any schedules for a given employeeId = " + employeeId + ".");
        }

        for (Long scheduleId : scheduleIds) {
            results.add(getDTOByScheduleId(scheduleId));
        }

        logger.info("Returning schedules={}", results);
        return results;
    }

    public List<ScheduleDTO> getScheduleForPet(long petId) {
        logger.info("Retrieving schedules for a pet: id={}", petId);
        List<ScheduleDTO> results = new ArrayList<>();

        List<Long> scheduleIds = scheduleDAO.findScheduleByPet(petId);

        if (scheduleIds.isEmpty()) {
            logger.warn("Cannot find any schedules for a given petId = {}", petId);
            throw new ScheduleNotFoundException("Cannot find any schedules for a given petId = " + petId + ".");
        }

        for (Long scheduleId : scheduleIds) {
            results.add(getDTOByScheduleId(scheduleId));
        }
        logger.info("Returning schedules={}", results);
        return results;
    }

    public List<ScheduleDTO> getScheduleForCustomer(long customerId) {
        logger.info("Retrieving schedules for a customer: customer={}", customerId);
        List<ScheduleDTO> results = new ArrayList<>();

        CustomerDTO customerDTO = customerService.findCustomer(customerId);

        Set<Long> scheduleIds = new HashSet<>();
        for (Long petId : customerDTO.getPetIds()) {
            scheduleIds.addAll(scheduleDAO.findScheduleByPet(petId));
        }

        if (scheduleIds.isEmpty()) {
            logger.warn("Cannot find any schedules for a given customerId = {}", customerId);
            throw new ScheduleNotFoundException("Cannot find any schedules for a given customerId = " + customerId + ".");
        }

        for (Long scheduleId : scheduleIds) {
            results.add(getDTOByScheduleId(scheduleId));
        }
        logger.info("Returning schedules={}", results);
        return results;
    }

    private ScheduleDTO getDTOByScheduleId(Long scheduleId) {
        List<Long> employeeIds = scheduleDAO.findEmployeeBySchedule(scheduleId);
        List<Long> petIds = scheduleDAO.findPetBySchedule(scheduleId);
        List<EmployeeSkill> activities = scheduleDAO.findActivityBySchedule(scheduleId);
        return convertEntityToDTO(scheduleRepository.find(scheduleId), petIds, employeeIds, activities);
    }

    private ScheduleDTO convertEntityToDTO(Schedule schedule, List<Long> petIds, List<Long> employeeIds, List<EmployeeSkill> activities) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(schedule.getId());
        scheduleDTO.setDate(schedule.getDate());
        scheduleDTO.setPetIds(petIds);
        scheduleDTO.setEmployeeIds(employeeIds);
        scheduleDTO.setActivities(new HashSet<>(activities));
        return scheduleDTO;
    }
}
