package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.DayAvailable;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Skill;
import com.udacity.jdnd.course3.critter.repository.DayAvailableRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.user.EmployeeDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DayAvailableRepository dayAvailableRepository;

    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) {
        logger.info("Saving employeeDTO.getDaysAvailable() = {}", employeeDTO.getDaysAvailable());
        Employee employee = new Employee();

        employee.setName(employeeDTO.getName());

        List<Skill> skillList = new ArrayList<>();
        for (EmployeeSkill employeeSkill : employeeDTO.getSkills()) {
            Skill skill = new Skill();
            skill.setSkill(employeeSkill);
            skillList.add(skill);
        }
        employee.setSkills(skillList);

        DayAvailable dayAvailable = new DayAvailable();

        if (employeeDTO.getDaysAvailable() != null && !employeeDTO.getDaysAvailable().isEmpty()) {
            for (DayOfWeek dayOfWeek : employeeDTO.getDaysAvailable()) {
                switch (dayOfWeek) {
                    case SUNDAY:
                        dayAvailable.setSunday(true);
                        break;
                    case MONDAY:
                        dayAvailable.setMonday(true);
                        break;
                    case TUESDAY:
                        dayAvailable.setTuesday(true);
                        break;
                    case WEDNESDAY:
                        dayAvailable.setWednesday(true);
                        break;
                    case THURSDAY:
                        dayAvailable.setThursday(true);
                        break;
                    case FRIDAY:
                        dayAvailable.setFriday(true);
                        break;
                    case SATURDAY:
                        dayAvailable.setSaturday(true);
                        break;
                }
            }
        }

        logger.info("Persisting employee.getId() = {}", employee.getId());
        logger.info("Persisting employee.getSkills() = {}", employee.getSkills());
        employeeRepository.persist(employee);
        logger.info("Persisting employee.getId() = {}", employee.getId());
        logger.info("Persisting employee.getSkills() = {}", employee.getSkills());

        logger.info("Persisting dayAvailable.getEmployeeId() = {}", dayAvailable.getEmployeeId());
        dayAvailable.setEmployeeId(employee.getId());
        dayAvailableRepository.save(dayAvailable);
        logger.info("Persisting dayAvailable.getEmployeeId() = {}", dayAvailable.getEmployeeId());

        logger.info("Returning EmployeeDTO = {}", employeeDTO);
        return convertEntityToDTO(employee, dayAvailable);
    }

    public EmployeeDTO getEmployee(long employeeId) {
        Employee employee = employeeRepository.find(employeeId);
        DayAvailable dayAvailable = dayAvailableRepository.findById(employeeId).orElse(null);

        logger.info("Persisting employee.getSkills() = {}", employee.getSkills());
        logger.info("Returning dayAvailable = {}", dayAvailable);
        return convertEntityToDTO(employee, dayAvailable);
    }

    private EmployeeDTO convertEntityToDTO(Employee employee, DayAvailable dayAvailable) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employee.getId());
        employeeDTO.setName(employee.getName());

        Set<EmployeeSkill> skillSet = employee.getSkills() == null ? null : employee.getSkills().stream()
                .map(Skill::getSkill)
                .collect(Collectors.toSet());
        employeeDTO.setSkills(skillSet);

        Set<DayOfWeek> dayOfWeeks = new HashSet<>();
        if (dayAvailable.getSunday()) {
            dayOfWeeks.add(DayOfWeek.SUNDAY);
        }
        if (dayAvailable.getMonday()) {
            dayOfWeeks.add(DayOfWeek.MONDAY);
        }
        if (dayAvailable.getTuesday()) {
            dayOfWeeks.add(DayOfWeek.TUESDAY);
        }
        if (dayAvailable.getWednesday()) {
            dayOfWeeks.add(DayOfWeek.WEDNESDAY);
        }
        if (dayAvailable.getThursday()) {
            dayOfWeeks.add(DayOfWeek.THURSDAY);
        }
        if (dayAvailable.getFriday()) {
            dayOfWeeks.add(DayOfWeek.FRIDAY);
        }
        if (dayAvailable.getSaturday()) {
            dayOfWeeks.add(DayOfWeek.SATURDAY);
        }

        if (!dayOfWeeks.isEmpty()) {
            employeeDTO.setDaysAvailable(dayOfWeeks);
        }

        return employeeDTO;
    }
}
