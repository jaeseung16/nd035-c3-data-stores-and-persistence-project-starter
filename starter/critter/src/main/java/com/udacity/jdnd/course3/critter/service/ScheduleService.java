package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.repository.ScheduleDAO;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
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
        Schedule schedule = new Schedule();
        schedule.setDate(scheduleDTO.getDate());
        scheduleRepository.persist(schedule);

        logger.info("Persisted schedule.getId() = {}", schedule.getId());

        Long scheduleId = schedule.getId();
        for (Long employeeId : scheduleDTO.getEmployeeIds()) {
            scheduleDAO.addEmployeeBySchedule(employeeId, scheduleId);
        }
        for (Long petId : scheduleDTO.getPetIds()) {
            scheduleDAO.addPetBySchedule(petId, scheduleId);
        }
        for (EmployeeSkill skill : scheduleDTO.getActivities()) {
            scheduleDAO.addActivityBySchedule(skill, scheduleId);
        }

        List<Long> employeeIds = scheduleDAO.findEmployeeBySchedule(scheduleId);
        List<Long> petIds = scheduleDAO.findPetBySchedule(scheduleId);
        List<EmployeeSkill> activities = scheduleDAO.findActivityBySchedule(scheduleId);

        ScheduleDTO scheduleDTOToReturn = convertEntityToDTO(
                scheduleRepository.find(scheduleId),
                petIds,
                employeeIds,
                activities);

        logger.info("Returning scheduleDTO = {}", scheduleDTOToReturn);
        return scheduleDTOToReturn;
    }

    public List<ScheduleDTO> getAllSchedules() {
        List<ScheduleDTO> results = new ArrayList<>();

        List<Schedule> scheduleList = scheduleRepository.getAllSchedules();
        logger.info("scheduleList.size() = {}", scheduleList.size());
        if (!scheduleList.isEmpty()) {
            for (Schedule schedule : scheduleList) {
                Long scheduleId = schedule.getId();
                List<Long> employeeIds = scheduleDAO.findEmployeeBySchedule(scheduleId);
                List<Long> petIds = scheduleDAO.findPetBySchedule(scheduleId);
                List<EmployeeSkill> activities = scheduleDAO.findActivityBySchedule(scheduleId);

                ScheduleDTO scheduleDTO = convertEntityToDTO(
                        scheduleRepository.find(scheduleId),
                        petIds,
                        employeeIds,
                        activities);

                logger.info("** scheduleDTO = {}, petIDs = {}, employeeIds ={}, activities = {}", scheduleDTO, petIds, employeeIds, activities);
                results.add(scheduleDTO);
            }
        }

        logger.info("Returning results = {}", results);
        return results;

    }

    public List<ScheduleDTO> getScheduleForEmployee(long employeeId) {
        List<ScheduleDTO> results = new ArrayList<>();

        List<Long> scheduleIds = scheduleDAO.findScheduleByEmployee(employeeId);

        for (Long scheduleId : scheduleIds) {
            List<Long> employeeIds = scheduleDAO.findEmployeeBySchedule(scheduleId);
            List<Long> petIds = scheduleDAO.findPetBySchedule(scheduleId);
            List<EmployeeSkill> activities = scheduleDAO.findActivityBySchedule(scheduleId);

            ScheduleDTO scheduleDTO = convertEntityToDTO(
                    scheduleRepository.find(scheduleId),
                    petIds,
                    employeeIds,
                    activities);

            logger.info("** scheduleDTO = {}, petIDs = {}, employeeIds ={}, activities = {}", scheduleDTO, petIds, employeeIds, activities);
            results.add(scheduleDTO);
        }

        return results;
    }

    public List<ScheduleDTO> getScheduleForPet(long petId) {
        List<ScheduleDTO> results = new ArrayList<>();

        List<Long> scheduleIds = scheduleDAO.findScheduleByPet(petId);

        for (Long scheduleId : scheduleIds) {
            List<Long> employeeIds = scheduleDAO.findEmployeeBySchedule(scheduleId);
            List<Long> petIds = scheduleDAO.findPetBySchedule(scheduleId);
            List<EmployeeSkill> activities = scheduleDAO.findActivityBySchedule(scheduleId);

            ScheduleDTO scheduleDTO = convertEntityToDTO(
                    scheduleRepository.find(scheduleId),
                    petIds,
                    employeeIds,
                    activities);

            logger.info("** scheduleDTO = {}, petIDs = {}, employeeIds ={}, activities = {}", scheduleDTO, petIds, employeeIds, activities);
            results.add(scheduleDTO);
        }

        return results;
    }

    public List<ScheduleDTO> getScheduleForCustomer(long customerId) {
        List<ScheduleDTO> results = new ArrayList<>();

        CustomerDTO customerDTO = customerService.findCustomer(customerId);

        Set<Long> scheduleIds = new HashSet<>();
        for (Long petId : customerDTO.getPetIds()) {
            scheduleIds.addAll(scheduleDAO.findScheduleByPet(petId));
        }

        for (Long scheduleId : scheduleIds) {
            List<Long> employeeIds = scheduleDAO.findEmployeeBySchedule(scheduleId);
            List<Long> petIds = scheduleDAO.findPetBySchedule(scheduleId);
            List<EmployeeSkill> activities = scheduleDAO.findActivityBySchedule(scheduleId);

            ScheduleDTO scheduleDTO = convertEntityToDTO(
                    scheduleRepository.find(scheduleId),
                    petIds,
                    employeeIds,
                    activities);

            logger.info("** scheduleDTO = {}, petIDs = {}, employeeIds ={}, activities = {}", scheduleDTO, petIds, employeeIds, activities);
            results.add(scheduleDTO);
        }

        return results;
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
