package com.udacity.jdnd.course3.critter.user;

/**
 * A example list of employee skills that could be included on an employee or a schedule request.
 */
public enum EmployeeSkill {
    PETTING(0),
    WALKING(1),
    FEEDING(2),
    MEDICATING(3),
    SHAVING(4);

    private int dbValue;

    EmployeeSkill(int dbValue) {
        this.dbValue = dbValue;
    }

    public int getDbValue() {
        return dbValue;
    }

    public static EmployeeSkill fromDbValue(int dbValue) {
        for (EmployeeSkill skill : values()) {
            if (skill.getDbValue() == dbValue) {
                return skill;
            }
        }
        return null;
    }
}
