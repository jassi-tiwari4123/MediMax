package com.ohms.dao;

import com.ohms.exception.DatabaseException;
import com.ohms.model.Patient;
import java.util.List;
import java.util.Optional;

/**
 * PatientDAO — contract for patients table operations.
 */
public interface PatientDAO {

    int              save(Patient patient)                 throws DatabaseException;
    void             update(Patient patient)               throws DatabaseException;
    Optional<Patient> findById(int id)                     throws DatabaseException;
    Optional<Patient> findByUserId(int userId)             throws DatabaseException;
    List<Patient>    findAll()                             throws DatabaseException;
    List<Patient>    searchByName(String name)             throws DatabaseException;
    int              count()                               throws DatabaseException;
}
