package com.ohms.dao;

import com.ohms.exception.DatabaseException;
import com.ohms.model.Specialization;
import java.util.List;

/**
 * SpecializationDAO — contract for specializations table.
 */
public interface SpecializationDAO {
    List<Specialization> findByDepartment(int departmentId) throws DatabaseException;
    int save(Specialization specialization)                  throws DatabaseException;
    void delete(int id)                                      throws DatabaseException;
}
