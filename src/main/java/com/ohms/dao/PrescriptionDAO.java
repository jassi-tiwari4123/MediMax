package com.ohms.dao;

import com.ohms.exception.DatabaseException;
import com.ohms.model.Prescription;
import com.ohms.model.PrescriptionItem;
import java.util.List;
import java.util.Optional;

/**
 * PrescriptionDAO — contract for prescription + prescription_items operations.
 *
 * INTERVIEW POINT:
 *   saveFull() uses a transaction to insert both the prescription header
 *   and its items atomically — either both succeed or both fail.
 */
public interface PrescriptionDAO {

    /**
     * Saves prescription header + all items in a single transaction.
     * @return generated prescription id
     */
    int saveFull(Prescription prescription)                   throws DatabaseException;

    void update(Prescription prescription)                    throws DatabaseException;
    void updatePdfPath(int prescriptionId, String pdfPath)    throws DatabaseException;

    Optional<Prescription> findById(int id)                   throws DatabaseException;
    Optional<Prescription> findByAppointmentId(int apptId)    throws DatabaseException;

    List<Prescription> findByPatient(int patientId)           throws DatabaseException;
    List<Prescription> findByDoctor(int doctorId)             throws DatabaseException;

    List<PrescriptionItem> findItemsByPrescription(int presId) throws DatabaseException;
}
