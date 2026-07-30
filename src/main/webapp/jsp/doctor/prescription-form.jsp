<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Prescription — OHMS</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="wrapper d-flex">

    <%-- Sidebar --%>
    <nav id="sidebar" class="sidebar d-flex flex-column">
        <div class="sidebar-brand d-flex align-items-center p-3">
            <i class="bi bi-hospital fs-4 me-2 text-white"></i>
            <div>
                <div class="fw-bold text-white lh-1">OHMS</div>
                <small class="text-white-50" style="font-size:0.7rem;">Doctor Portal</small>
            </div>
        </div>
        <ul class="nav flex-column px-2 flex-grow-1">
            <li class="nav-item">
                <a class="nav-link sidebar-link"
                   href="${pageContext.request.contextPath}/doctor/dashboard">
                    <i class="bi bi-speedometer2"></i> Dashboard
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link sidebar-link active"
                   href="${pageContext.request.contextPath}/doctor/appointments">
                    <i class="bi bi-calendar3"></i> My Appointments
                </a>
            </li>
        </ul>
        <div class="px-2 pb-3">
            <hr class="border-secondary">
            <a class="nav-link sidebar-link text-danger"
               href="${pageContext.request.contextPath}/logout">
                <i class="bi bi-box-arrow-right"></i> Logout
            </a>
        </div>
    </nav>

    <div class="main-content flex-grow-1">
        <nav class="navbar navbar-light bg-white border-bottom px-4 shadow-sm">
            <button class="btn btn-sm btn-outline-secondary me-2" id="sidebarToggle">
                <i class="bi bi-list"></i>
            </button>
            <span class="navbar-brand mb-0 h5">
                <i class="bi bi-file-earmark-plus me-2 text-primary"></i>Create Prescription
            </span>
            <div class="ms-auto">
                <a href="${pageContext.request.contextPath}/doctor/appointments"
                   class="btn btn-sm btn-outline-secondary">
                    <i class="bi bi-arrow-left me-1"></i>Back
                </a>
            </div>
        </nav>

        <div class="p-4">
            <div class="row justify-content-center">
                <div class="col-lg-10">

                    <%-- Appointment summary card --%>
                    <div class="card border-0 shadow-sm mb-4">
                        <div class="card-body d-flex align-items-center gap-4">
                            <div class="rounded-3 bg-primary-subtle text-primary p-3">
                                <i class="bi bi-calendar2-check fs-3"></i>
                            </div>
                            <div>
                                <h6 class="mb-0 fw-bold">Appointment #${appointment.id}</h6>
                                <div class="text-muted small">
                                    Patient ID: ${appointment.patientId} &bull;
                                    ${appointment.appointmentDate} at ${appointment.appointmentTime}
                                </div>
                                <div class="text-muted small">
                                    Reason: ${not empty appointment.reason ? appointment.reason : '—'}
                                </div>
                            </div>
                            <div class="ms-auto">
                                <span class="badge bg-success px-3 py-2">
                                    Dr. ${doctor.fullName}
                                </span>
                            </div>
                        </div>
                    </div>

                    <%-- Prescription Form --%>
                    <form method="post"
                          action="${pageContext.request.contextPath}/doctor/prescription"
                          id="prescriptionForm">

                        <input type="hidden" name="appointmentId" value="${appointment.id}">
                        <input type="hidden" name="patientId"     value="${appointment.patientId}">

                        <%-- Diagnosis & Instructions --%>
                        <div class="card border-0 shadow-sm mb-4">
                            <div class="card-header bg-white fw-semibold">
                                <i class="bi bi-stethoscope me-2 text-primary"></i>Diagnosis & Instructions
                            </div>
                            <div class="card-body">
                                <div class="mb-3">
                                    <label class="form-label fw-semibold">
                                        Diagnosis <span class="text-danger">*</span>
                                    </label>
                                    <textarea name="diagnosis" class="form-control" rows="3"
                                              placeholder="Primary diagnosis..." required></textarea>
                                </div>
                                <div class="row g-3">
                                    <div class="col-md-8">
                                        <label class="form-label fw-semibold">General Instructions</label>
                                        <textarea name="instructions" class="form-control" rows="2"
                                                  placeholder="Rest, diet, lifestyle instructions..."></textarea>
                                    </div>
                                    <div class="col-md-4">
                                        <label class="form-label fw-semibold">Follow-up Date</label>
                                        <input type="date" name="followUpDate" class="form-control">
                                    </div>
                                </div>
                            </div>
                        </div>

                        <%-- Medicines --%>
                        <div class="card border-0 shadow-sm mb-4">
                            <div class="card-header bg-white fw-semibold d-flex justify-content-between">
                                <span><i class="bi bi-capsule me-2 text-success"></i>Medicines</span>
                                <button type="button" class="btn btn-sm btn-success"
                                        id="addMedicineBtn">
                                    <i class="bi bi-plus-circle me-1"></i>Add Medicine
                                </button>
                            </div>
                            <div class="card-body" id="medicinesContainer">

                                <%-- Template row (first row, always visible) --%>
                                <div class="medicine-row" id="medRow0">
                                    <div class="row g-2 align-items-end">
                                        <div class="col-md-3">
                                            <label class="form-label small fw-semibold">Medicine Name</label>
                                            <input type="text" name="medicineName"
                                                   class="form-control form-control-sm"
                                                   placeholder="e.g. Paracetamol" required>
                                        </div>
                                        <div class="col-md-2">
                                            <label class="form-label small fw-semibold">Dosage</label>
                                            <input type="text" name="dosage"
                                                   class="form-control form-control-sm"
                                                   placeholder="e.g. 500mg">
                                        </div>
                                        <div class="col-md-1 text-center">
                                            <label class="form-label small fw-semibold d-block">Morning</label>
                                            <div class="form-check d-flex justify-content-center">
                                                <input class="form-check-input" type="checkbox"
                                                       name="morning" value="on">
                                            </div>
                                        </div>
                                        <div class="col-md-1 text-center">
                                            <label class="form-label small fw-semibold d-block">Afternoon</label>
                                            <div class="form-check d-flex justify-content-center">
                                                <input class="form-check-input" type="checkbox"
                                                       name="afternoon" value="on">
                                            </div>
                                        </div>
                                        <div class="col-md-1 text-center">
                                            <label class="form-label small fw-semibold d-block">Night</label>
                                            <div class="form-check d-flex justify-content-center">
                                                <input class="form-check-input" type="checkbox"
                                                       name="night" value="on">
                                            </div>
                                        </div>
                                        <div class="col-md-2">
                                            <label class="form-label small fw-semibold">Duration (days)</label>
                                            <input type="number" name="durationDays"
                                                   class="form-control form-control-sm"
                                                   placeholder="7" min="1">
                                        </div>
                                        <div class="col-md-2">
                                            <label class="form-label small fw-semibold">Item Instructions</label>
                                            <input type="text" name="itemInstructions"
                                                   class="form-control form-control-sm"
                                                   placeholder="After food...">
                                        </div>
                                    </div>
                                </div>

                            </div><%-- /medicinesContainer --%>
                        </div>

                        <div class="d-flex gap-3 justify-content-end">
                            <a href="${pageContext.request.contextPath}/doctor/appointments"
                               class="btn btn-outline-secondary btn-lg">
                                <i class="bi bi-x me-1"></i>Cancel
                            </a>
                            <button type="submit" class="btn btn-primary btn-lg px-5">
                                <i class="bi bi-file-earmark-check me-2"></i>
                                Save Prescription &amp; Generate PDF
                            </button>
                        </div>

                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
<script>
    let rowCount = 1;

    document.getElementById('addMedicineBtn').addEventListener('click', function () {
        const container = document.getElementById('medicinesContainer');
        const template  = document.getElementById('medRow0').cloneNode(true);

        // Clear values in cloned row
        template.id = 'medRow' + rowCount++;
        template.querySelectorAll('input[type="text"], input[type="number"]')
                .forEach(i => i.value = '');
        template.querySelectorAll('input[type="checkbox"]')
                .forEach(i => i.checked = false);
        template.querySelector('[name="medicineName"]').removeAttribute('required');

        // Add remove button
        const removeBtn = document.createElement('div');
        removeBtn.className = 'col-12 text-end mt-1';
        removeBtn.innerHTML = '<button type="button" class="btn btn-sm btn-outline-danger remove-row">'
                            + '<i class="bi bi-trash me-1"></i>Remove</button>';
        template.querySelector('.row').appendChild(removeBtn);

        container.appendChild(template);
    });

    // Event delegation for remove buttons
    document.getElementById('medicinesContainer').addEventListener('click', function (e) {
        if (e.target.closest('.remove-row')) {
            e.target.closest('.medicine-row').remove();
        }
    });

    // Set follow-up date minimum to today
    const fuDate = document.querySelector('input[name="followUpDate"]');
    if (fuDate) {
        fuDate.setAttribute('min', new Date().toISOString().split('T')[0]);
    }
</script>
</body>
</html>
