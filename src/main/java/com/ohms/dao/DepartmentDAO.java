package com.ohms.dao;

import com.ohms.exception.DatabaseException;
import com.ohms.model.Department;
import java.util.List;
import java.util.Optional;

/**
 * DepartmentDAO — contract for departments table.
 */
public interface DepartmentDAO {
    int              save(Department dept)       throws DatabaseException;
    void             update(Department dept)     throws DatabaseException;
    void             toggleActive(int id)        throws DatabaseException;
    Optional<Department> findById(int id)        throws DatabaseException;
    List<Department> findAll()                   throws DatabaseException;
    List<Department> findActive()                throws DatabaseException;
}
