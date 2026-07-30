package com.ohms.model;

/**
 * Specialization — a specialization belonging to a department.
 */
public class Specialization {

    private int    id;
    private int    departmentId;
    private String name;

    public Specialization() {}

    public Specialization(int departmentId, String name) {
        this.departmentId = departmentId;
        this.name         = name;
    }

    public int    getId()                        { return id; }
    public void   setId(int id)                  { this.id = id; }

    public int    getDepartmentId()              { return departmentId; }
    public void   setDepartmentId(int deptId)    { this.departmentId = deptId; }

    public String getName()                      { return name; }
    public void   setName(String name)           { this.name = name; }
}
