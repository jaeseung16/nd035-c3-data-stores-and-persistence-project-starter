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
        logger.info("Saving employeeDTO.getDaysAvailable() = {}", employeeDTO.getDaysAvailable());
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

        DayAvailable dayAvailable = new DayAvailable();

        if (employeeDTO.getDaysAvailable() != null && !employeeDTO.getDaysAvailable().isEmpty()) {
            dayAvailable = getDayAvailable(employeeDTO.getDaysAvailable());
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

    public void setAvailability(Set<DayOfWeek> daysAvailable, long employeeId) {
        DayAvailable dayAvailable = getDayAvailable(daysAvailable);
        dayAvailable.setEmployeeId(employeeId);

        dayAvailableRepository.save(dayAvailable);
    }

    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeDTO) {
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();

        List<Skill> skillList = skillRepository.findBySkillIn(employeeDTO.getSkills());

        Set<Employee> employeeList = skillList.stream()
                .map(Skill::getEmployee)
                .collect(Collectors.toSet());

        DayOfWeek dayOfWeek = employeeDTO.getDate().getDayOfWeek();

        for (Employee employee : employeeList) {
            DayAvailable dayAvailable = dayAvailableRepository.findById(employee.getId()).orElse(null);

            boolean matchedDayAvailable = false;
            if (dayAvailable != null) {
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
            }

            boolean matchedSkills = true;
            for (EmployeeSkill skill : employeeDTO.getSkills()) {
                List<EmployeeSkill> skillListFromEmployee = employee.getSkills().stream()
                        .map(Skill::getSkill)
                        .collect(Collectors.toList());

                if (!skillListFromEmployee.contains(skill)) {
                    matchedSkills = false;
                    break;
                }
            }

            if (matchedDayAvailable && matchedSkills) {
                employeeDTOList.add(convertEntityToDTO(employee, dayAvailable));
            }
        }

        return employeeDTOList;
    }

    private Employee convertDTOToEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        employee.setId(employeeDTO.getId() > 0 ? employeeDTO.getId() : null);
        employee.setName(employeeDTO.getName());
        employee.setSkills(employee.getSkills());
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

        return dayOfWeekSet;
    }
}
