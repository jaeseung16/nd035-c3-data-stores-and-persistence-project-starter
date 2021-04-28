package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.DayAvailable;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Skill;
import com.udacity.jdnd.course3.critter.repository.DayAvailableRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repository.SkillRepository;
import com.udacity.jdnd.course3.critter.user.EmployeeDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private SkillRepository skillRepository;

    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) {
        logger.info("Saving employeeDTO={}", employeeDTO);

        Employee employee = getEntityFromDTO(employeeDTO);
        employeeRepository.persist(employee);
        logger.info("Persisted an employee: id={}", employee.getId());

        DayAvailable dayAvailable = new DayAvailable();
        if (employeeDTO.getDaysAvailable() != null && !employeeDTO.getDaysAvailable().isEmpty()) {
            dayAvailable = getDayAvailable(employeeDTO.getDaysAvailable());
            dayAvailable.setEmployeeId(employee.getId());
            dayAvailableRepository.save(dayAvailable);
        }

        EmployeeDTO employeeDTOToReturn = convertEntityToDTO(employee, dayAvailable);
        logger.info("Returning {}", employeeDTOToReturn);
        return employeeDTOToReturn;
    }

    public EmployeeDTO getEmployee(long employeeId) {
        logger.info("Retrieving an employee: id={}", employeeId);
        Employee employee = employeeRepository.find(employeeId);

        if (employee == null) {
            logger.warn("Cannot find an employee for a given id = {}", employeeId);
            throw new EmployeeNotFoundException("Cannot find an employee for a given id = " + employeeId + ".");
        }

        DayAvailable dayAvailable = dayAvailableRepository.findById(employeeId).orElse(null);

        EmployeeDTO employeeDTO = convertEntityToDTO(employee, dayAvailable);
        logger.info("Returning {}", employeeDTO);
        return employeeDTO;
    }

    public void setAvailability(Set<DayOfWeek> daysAvailable, long employeeId) {
        logger.info("Setting availability of an employee: id={}", employeeId);

        Employee employee = employeeRepository.find(employeeId);

        if (employee == null) {
            logger.warn("Cannot find a employees for a given id = {}", employeeId);
            throw new EmployeeNotFoundException("Cannot find a employees for a given id = " + employeeId + ".");
        }

        DayAvailable dayAvailable = getDayAvailable(daysAvailable);
        dayAvailable.setEmployeeId(employeeId);
        dayAvailableRepository.save(dayAvailable);
    }

    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeDTO) {
        logger.info("Retrieving employees for services={}", employeeDTO.getSkills());

        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        Set<Employee> employeeSet = skillRepository.findBySkillIn(employeeDTO.getSkills())
                .stream()
                .map(Skill::getEmployee)
                .collect(Collectors.toSet());

        if (employeeSet.isEmpty()) {
            logger.warn("Cannot find a employees for given services = {}", employeeDTO.getSkills());
            throw new EmployeeNotFoundException("Cannot find any employees for given services = " + employeeDTO.getSkills() + ".");
        }

        DayOfWeek dayOfWeek = employeeDTO.getDate().getDayOfWeek();
        Set<EmployeeSkill> skillSet = employeeDTO.getSkills();
        for (Employee employee : employeeSet) {
            DayAvailable dayAvailable = dayAvailableRepository.findById(employee.getId()).orElse(null);
            boolean availableOnAGivenDay = dayAvailable != null && checkIfAvailableOnAGivenDay(dayAvailable, dayOfWeek);
            boolean hasSkill = checkIfEmployeeHasSkill(employee, skillSet);
            if (availableOnAGivenDay && hasSkill) {
                employeeDTOList.add(convertEntityToDTO(employee, dayAvailable));
            }
        }

        if (employeeDTOList.isEmpty()) {
            logger.warn("Cannot find a employees for a given date = {}", employeeDTO.getDate());
            throw new EmployeeNotFoundException("Cannot find any employees for a given date = " + employeeDTO.getDate() + ".");
        }

        logger.info("Returning {}", employeeDTOList);
        return employeeDTOList;
    }

    private boolean checkIfAvailableOnAGivenDay(DayAvailable dayAvailable, DayOfWeek dayOfWeek) {
        boolean matchedDayAvailable = false;
        switch (dayOfWeek) {
            case SUNDAY:
                matchedDayAvailable = dayAvailable.getSunday();
                break;
            case MONDAY:
                matchedDayAvailable = dayAvailable.getMonday();
                break;
            case TUESDAY:
                matchedDayAvailable = dayAvailable.getTuesday();
                break;
            case WEDNESDAY:
                matchedDayAvailable = dayAvailable.getWednesday();
                break;
            case THURSDAY:
                matchedDayAvailable = dayAvailable.getThursday();
                break;
            case FRIDAY:
                matchedDayAvailable = dayAvailable.getFriday();
                break;
            case SATURDAY:
                matchedDayAvailable = dayAvailable.getSaturday();
                break;
        }
        return matchedDayAvailable;
    }

    private boolean checkIfEmployeeHasSkill(Employee employee, Set<EmployeeSkill> employeeSkillSet) {
        boolean matchedSkills = true;

        List<EmployeeSkill> skillListFromEmployee = employee.getSkills().stream()
                .map(Skill::getSkill)
                .collect(Collectors.toList());

        for (EmployeeSkill skill : employeeSkillSet) {
            if (!skillListFromEmployee.contains(skill)) {
                matchedSkills = false;
                break;
            }
        }
        return matchedSkills;
    }

    private Employee getEntityFromDTO(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        employee.setName(employeeDTO.getName());

        List<Skill> skillList = new ArrayList<>();
        for (EmployeeSkill employeeSkill : employeeDTO.getSkills()) {
            Skill skill = new Skill();
            skill.setSkill(employeeSkill);
            skill.setEmployee(employee);
            skillList.add(skill);
        }
        employee.setSkills(skillList);

        return employee;
    }

    private DayAvailable getDayAvailable(Set<DayOfWeek> dayOfWeeks) {
        DayAvailable dayAvailable = new DayAvailable();

        for (DayOfWeek dayOfWeek : dayOfWeeks) {
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

        return dayAvailable;
    }

    private EmployeeDTO convertEntityToDTO(Employee employee, DayAvailable dayAvailable) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employee.getId());
        employeeDTO.setName(employee.getName());

        Set<EmployeeSkill> skillSet = employee.getSkills() == null ? null : employee.getSkills().stream()
                .map(Skill::getSkill)
                .collect(Collectors.toSet());
        employeeDTO.setSkills(skillSet);

        Set<DayOfWeek> dayOfWeeks = populateDayOfWeekSet(dayAvailable);
        if (!dayOfWeeks.isEmpty()) {
            employeeDTO.setDaysAvailable(dayOfWeeks);
        }

        return employeeDTO;
    }

    private Set<DayOfWeek> populateDayOfWeekSet(DayAvailable dayAvailable) {
        Set<DayOfWeek> dayOfWeekSet = new HashSet<>();

        if (dayAvailable != null) {
            if (dayAvailable.getSunday()) {
                dayOfWeekSet.add(DayOfWeek.SUNDAY);
            }
            if (dayAvailable.getMonday()) {
                dayOfWeekSet.add(DayOfWeek.MONDAY);
            }
            if (dayAvailable.getTuesday()) {
                dayOfWeekSet.add(DayOfWeek.TUESDAY);
            }
            if (dayAvailable.getWednesday()) {
                dayOfWeekSet.add(DayOfWeek.WEDNESDAY);
            }
            if (dayAvailable.getThursday()) {
                dayOfWeekSet.add(DayOfWeek.THURSDAY);
            }
            if (dayAvailable.getFriday()) {
                dayOfWeekSet.add(DayOfWeek.FRIDAY);
            }
            if (dayAvailable.getSaturday()) {
                dayOfWeekSet.add(DayOfWeek.SATURDAY);
            }
        }

        return dayOfWeekSet;
    }
}
