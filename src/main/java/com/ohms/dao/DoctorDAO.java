package com.ohms.dao;

import com.ohms.exception.DatabaseException;
import com.ohms.model.Doctor;
import java.util.List;
import java.util.Optional;

/**
 * DoctorDAO — contract for doctor table operations.
 */
public interface DoctorDAO {

    int              save(Doctor doctor)                                    throws DatabaseException;
    void             update(Doctor doctor)                                  throws DatabaseException;
    void             updateStatus(int doctorId, String status)              throws DatabaseException;
    Optional<Doctor> findById(int id)                                       throws DatabaseException;
    Optional<Doctor> findByUserId(int userId)                               throws DatabaseException;
    List<Doctor>     findAll()                                              throws DatabaseException;
    List<Doctor>     findApproved()                                         throws DatabaseException;
    List<Doctor>     findByDepartment(int departmentId)                     throws DatabaseException;
    List<Doctor>     searchByName(String name)                              throws DatabaseException;
    List<Doctor>     findPending()                                          throws DatabaseException;
    int              countApproved()                                        throws DatabaseException;
}
