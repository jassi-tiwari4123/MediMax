package com.ohms.dao;

import com.ohms.exception.DatabaseException;
import com.ohms.model.Appointment;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * AppointmentDAO — contract for appointments table operations.
 */
public interface AppointmentDAO {

    int              save(Appointment appointment)                          throws DatabaseException;
    void             update(Appointment appointment)                        throws DatabaseException;
    void             updateStatus(int appointmentId, String status,
                                  String cancelledBy, String reason)        throws DatabaseException;
    Optional<Appointment> findById(int id)                                  throws DatabaseException;
    List<Appointment>     findByPatient(int patientId)                      throws DatabaseException;
    List<Appointment>     findByDoctor(int doctorId)                        throws DatabaseException;
    List<Appointment>     findByDoctorAndDate(int doctorId, LocalDate date) throws DatabaseException;
    List<Appointment>     findAll()                                         throws DatabaseException;
    List<Appointment>     findByStatus(String status)                       throws DatabaseException;

    /**
     * Checks if the given doctor already has a PENDING or CONFIRMED
     * appointment at the exact date+time — used to prevent double booking.
     */
    boolean isSlotTaken(int doctorId, LocalDate date, LocalTime time)       throws DatabaseException;

    int countByStatus(String status)                                        throws DatabaseException;
}
