package com.ohms.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.ohms.model.*;
import com.ohms.utility.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * PdfService — generates prescription PDFs using iText 5.
 *
 * INTERVIEW POINTS:
 *   - iText Document API: Document → PdfWriter → add content → close.
 *   - Demonstrates File Handling (Java I/O): creates directory, writes file.
 *   - Professional hospital prescription layout with header, patient info,
 *     medicines table, and footer.
 *   - Returns absolute file path stored in DB — patient downloads via servlet.
 *
 * Phase 12 — PDF Generation.
 */
public class PdfService {

    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("dd MMM yyyy");

    // iText fonts
    private static final Font FONT_TITLE  = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD,
                                                       BaseColor.WHITE);
    private static final Font FONT_H2     = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD,
                                                       new BaseColor(0, 51, 102));
    private static final Font FONT_LABEL  = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static final Font FONT_NORMAL = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final Font FONT_SMALL  = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC,
                                                       BaseColor.GRAY);

    private static final BaseColor HEADER_COLOR  = new BaseColor(13, 110, 253);  // Bootstrap primary
    private static final BaseColor TABLE_HEADER  = new BaseColor(220, 230, 245);
    private static final BaseColor TABLE_ALT_ROW = new BaseColor(248, 249, 250);

    // ── Generate Prescription PDF ────────────────────────────────

    /**
     * Generates a PDF prescription file on disk and returns its path.
     *
     * @param presc Prescription with doctor, patient, and items populated
     * @return absolute file path of the generated PDF
     */
    public String generatePrescriptionPdf(Prescription presc) throws Exception {

        // Create output directory if not exists (File Handling)
        String outputDir = AppConfig.get("pdf.output.dir", "/tmp/ohms/prescriptions");
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName  = "prescription_" + presc.getId() + "_"
                         + System.currentTimeMillis() + ".pdf";
        String filePath  = outputDir + File.separator + fileName;

        Document doc = new Document(PageSize.A4, 36, 36, 36, 36);

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            PdfWriter.getInstance(doc, fos);
            doc.open();

            // ── Header ──────────────────────────────────────────
            addHeader(doc, presc);

            doc.add(Chunk.NEWLINE);

            // ── Patient & Doctor Info ────────────────────────────
            addPatientDoctorInfo(doc, presc);

            doc.add(Chunk.NEWLINE);

            // ── Diagnosis ────────────────────────────────────────
            if (presc.getDiagnosis() != null && !presc.getDiagnosis().isBlank()) {
                doc.add(new Paragraph("Diagnosis:", FONT_LABEL));
                doc.add(new Paragraph(presc.getDiagnosis(), FONT_NORMAL));
                doc.add(Chunk.NEWLINE);
            }

            // ── Medicines Table ──────────────────────────────────
            addMedicinesTable(doc, presc);

            doc.add(Chunk.NEWLINE);

            // ── Instructions ─────────────────────────────────────
            if (presc.getInstructions() != null && !presc.getInstructions().isBlank()) {
                doc.add(new Paragraph("General Instructions:", FONT_LABEL));
                doc.add(new Paragraph(presc.getInstructions(), FONT_NORMAL));
                doc.add(Chunk.NEWLINE);
            }

            // ── Follow-up ────────────────────────────────────────
            if (presc.getFollowUpDate() != null) {
                doc.add(new Paragraph(
                    "Follow-up Date: " + presc.getFollowUpDate().format(DATE_FMT),
                    FONT_LABEL));
                doc.add(Chunk.NEWLINE);
            }

            // ── Doctor Signature ─────────────────────────────────
            addSignature(doc, presc);

            // ── Footer ───────────────────────────────────────────
            addFooter(doc);

        } finally {
            if (doc.isOpen()) doc.close();
        }

        logger.info("Prescription PDF generated: {}", filePath);
        return filePath;
    }

    // ── Private layout methods ───────────────────────────────────

    private void addHeader(Document doc, Prescription presc) throws DocumentException {
        // Full-width colored header band
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(HEADER_COLOR);
        cell.setPadding(12);
        cell.setBorder(Rectangle.NO_BORDER);

        String hospitalName = AppConfig.getHospitalName();
        Paragraph p = new Paragraph();
        p.add(new Chunk(hospitalName + "\n", FONT_TITLE));
        p.add(new Chunk("PRESCRIPTION", new Font(Font.FontFamily.HELVETICA, 11,
                                                   Font.NORMAL, BaseColor.WHITE)));
        cell.addElement(p);

        String dateStr = presc.getCreatedAt() != null
            ? presc.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
            : java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));

        Paragraph datePara = new Paragraph("Date: " + dateStr,
            new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.WHITE));
        datePara.setAlignment(Element.ALIGN_RIGHT);
        cell.addElement(datePara);

        headerTable.addCell(cell);
        doc.add(headerTable);
    }

    private void addPatientDoctorInfo(Document doc, Prescription presc)
            throws DocumentException {

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1f, 1f});

        // Left: Doctor info
        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.BOX);
        left.setPadding(8);
        left.setBackgroundColor(TABLE_ALT_ROW);

        left.addElement(new Paragraph("Doctor Details", FONT_H2));
        if (presc.getDoctor() != null) {
            left.addElement(new Paragraph("Dr. " + presc.getDoctor().getFullName(), FONT_LABEL));
            left.addElement(new Paragraph(presc.getDoctor().getSpecialization(), FONT_NORMAL));
            left.addElement(new Paragraph(presc.getDoctor().getQualification(), FONT_NORMAL));
        }

        // Right: Patient info
        PdfPCell right = new PdfPCell();
        right.setBorder(Rectangle.BOX);
        right.setPadding(8);

        right.addElement(new Paragraph("Patient Details", FONT_H2));
        if (presc.getPatient() != null && presc.getPatient().getUser() != null) {
            User u = presc.getPatient().getUser();
            right.addElement(new Paragraph(u.getFullName(), FONT_LABEL));
            right.addElement(new Paragraph("Phone: " + u.getPhone(), FONT_NORMAL));
            if (presc.getPatient().getBloodGroup() != null) {
                right.addElement(new Paragraph(
                    "Blood Group: " + presc.getPatient().getBloodGroup(), FONT_NORMAL));
            }
        }

        infoTable.addCell(left);
        infoTable.addCell(right);
        doc.add(infoTable);
    }

    private void addMedicinesTable(Document doc, Prescription presc)
            throws DocumentException {

        doc.add(new Paragraph("Medicines Prescribed:", FONT_LABEL));
        doc.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 1.5f, 0.8f, 1f, 0.8f, 2f});

        // Column headers
        String[] headers = {"Medicine", "Dosage", "Morning", "Afternoon", "Night", "Duration"};
        for (String h : headers) {
            PdfPCell hCell = new PdfPCell(new Phrase(h, FONT_LABEL));
            hCell.setBackgroundColor(TABLE_HEADER);
            hCell.setPadding(6);
            hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hCell);
        }

        // Data rows
        boolean alt = false;
        for (PrescriptionItem item : presc.getItems()) {
            BaseColor rowColor = alt ? TABLE_ALT_ROW : BaseColor.WHITE;
            alt = !alt;

            addTableCell(table, item.getMedicineName(), rowColor, Element.ALIGN_LEFT);
            addTableCell(table, item.getDosage(), rowColor, Element.ALIGN_CENTER);
            addTableCell(table, item.isMorning()   ? "✓" : "-", rowColor, Element.ALIGN_CENTER);
            addTableCell(table, item.isAfternoon() ? "✓" : "-", rowColor, Element.ALIGN_CENTER);
            addTableCell(table, item.isNight()     ? "✓" : "-", rowColor, Element.ALIGN_CENTER);
            addTableCell(table,
                item.getDurationDays() != null ? item.getDurationDays() + " days" : "-",
                rowColor, Element.ALIGN_CENTER);
        }

        doc.add(table);
    }

    private void addTableCell(PdfPTable table, String text,
                               BaseColor bg, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "-", FONT_NORMAL));
        cell.setBackgroundColor(bg);
        cell.setPadding(5);
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }

    private void addSignature(Document doc, Prescription presc)
            throws DocumentException {

        PdfPTable sigTable = new PdfPTable(2);
        sigTable.setWidthPercentage(100);
        sigTable.setWidths(new float[]{1f, 1f});

        PdfPCell empty = new PdfPCell(new Phrase(""));
        empty.setBorder(Rectangle.NO_BORDER);

        PdfPCell sig = new PdfPCell();
        sig.setBorder(Rectangle.NO_BORDER);
        sig.setHorizontalAlignment(Element.ALIGN_RIGHT);

        String docName = (presc.getDoctor() != null)
            ? "Dr. " + presc.getDoctor().getFullName() : "Doctor";

        Paragraph sigPara = new Paragraph();
        sigPara.setAlignment(Element.ALIGN_RIGHT);
        sigPara.add(new Chunk("___________________________\n", FONT_NORMAL));
        sigPara.add(new Chunk(docName + "\n", FONT_LABEL));
        if (presc.getDoctor() != null) {
            sigPara.add(new Chunk(presc.getDoctor().getSpecialization(), FONT_NORMAL));
        }
        sig.addElement(sigPara);

        sigTable.addCell(empty);
        sigTable.addCell(sig);
        doc.add(sigTable);
    }

    private void addFooter(Document doc) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        LineSeparator ls = new LineSeparator();
        doc.add(new Chunk(ls));
        doc.add(new Paragraph(
            "This is a digitally generated prescription from " +
            AppConfig.getHospitalName() + ". Valid only with doctor's signature.",
            FONT_SMALL));
    }
}
