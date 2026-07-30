<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book Appointment — OHMS</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="wrapper d-flex">

    <%-- Patient Sidebar --%>
    <nav id="sidebar" class="sidebar d-flex flex-column">
        <div class="sidebar-brand d-flex align-items-center p-3">
            <i class="bi bi-hospital fs-4 me-2 text-white"></i>
            <div>
                <div class="fw-bold text-white lh-1">OHMS</div>
                <small class="text-white-50" style="font-size:0.7rem;">Patient Portal</small>
            </div>
        </div>
        <ul class="nav flex-column px-2 flex-grow-1">
            <li class="nav-item">
                <a class="nav-link sidebar-link"
                   href="${pageContext.request.contextPath}/patient/dashboard">
                    <i class="bi bi-speedometer2"></i> Dashboard
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link sidebar-link"
                   href="${pageContext.request.contextPath}/patient/search-doctors">
                    <i class="bi bi-search-heart"></i> Find Doctors
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link sidebar-link active"
                   href="${pageContext.request.contextPath}/patient/book-appointment">
                    <i class="bi bi-calendar-plus"></i> Book Appointment
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
                <i class="bi bi-calendar-plus me-2 text-info"></i>Book an Appointment
            </span>
        </nav>

        <div class="p-4">
            <div class="row justify-content-center">
                <div class="col-lg-8">

                    <%-- Flash messages from session --%>
                    <c:if test="${not empty sessionScope.flashError}">
                        <div class="alert alert-danger alert-dismissible fade show">
                            <i class="bi bi-exclamation-triangle me-2"></i>${sessionScope.flashError}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <c:remove var="flashError" scope="session"/>
                    </c:if>

                    <%-- Pre-selected doctor info card --%>
                    <c:if test="${not empty selectedDoctor}">
                        <div class="card border-0 shadow-sm mb-4 border-start border-4 border-success">
                            <div class="card-body d-flex align-items-center gap-3">
                                <div class="rounded-circle bg-success-subtle text-success d-flex align-items-center justify-content-center"
                                     style="width:52px;height:52px;font-size:1.5rem;">
                                    <i class="bi bi-person-circle"></i>
                                </div>
                                <div>
                                    <div class="fw-bold">Dr. ${selectedDoctor.fullName}</div>
                                    <div class="text-muted small">
                                        ${selectedDoctor.specialization} &bull; ${selectedDoctor.department.name}
                                    </div>
                                    <div class="text-muted small">
                                        ₹${selectedDoctor.consultationFee} consultation fee
                                    </div>
                                </div>
                                <span class="ms-auto badge bg-success">Selected</span>
                            </div>
                        </div>
                    </c:if>

                    <%-- Booking Form --%>
                    <div class="card border-0 shadow-sm">
                        <div class="card-header bg-white fw-semibold">
                            <i class="bi bi-calendar-plus me-2 text-info"></i>Appointment Details
                        </div>
                        <div class="card-body p-4">
                            <form method="post"
                                  action="${pageContext.request.contextPath}/patient/book-appointment"
                                  novalidate id="bookingForm">

                                <%-- Doctor selection --%>
                                <div class="mb-4">
                                    <label class="form-label fw-semibold">
                                        <i class="bi bi-person-badge me-1"></i>
                                        Select Doctor <span class="text-danger">*</span>
                                    </label>
                                    <select name="doctorId" class="form-select form-select-lg" required
                                            id="doctorSelect">
                                        <option value="">— Choose a doctor —</option>
                                        <c:forEach var="doc" items="${doctors}">
                                            <option value="${doc.id}"
                                                    ${selectedDoctor != null and selectedDoctor.id eq doc.id ? 'selected' : ''}
                                                    data-fee="${doc.consultationFee}"
                                                    data-dept="${doc.department.name}"
                                                    data-spec="${doc.specialization}">
                                                Dr. ${doc.fullName} — ${doc.specialization} (${doc.department.name}) — ₹${doc.consultationFee}
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <div class="invalid-feedback">Please select a doctor.</div>
                                </div>

                                <%-- Doctor info preview --%>
                                <div id="doctorPreview" class="alert alert-info py-2 small mb-3" style="display:none;">
                                    <i class="bi bi-info-circle me-1"></i>
                                    <span id="previewText"></span>
                                </div>

                                <div class="row g-3 mb-3">
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">
                                            <i class="bi bi-calendar3 me-1"></i>
                                            Appointment Date <span class="text-danger">*</span>
                                        </label>
                                        <input type="date" name="appointmentDate"
                                               id="appointmentDate"
                                               class="form-control form-control-lg" required>
                                        <div class="invalid-feedback">Please select a date.</div>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">
                                            <i class="bi bi-clock me-1"></i>
                                            Appointment Time <span class="text-danger">*</span>
                                        </label>
                                        <input type="time" name="appointmentTime"
                                               class="form-control form-control-lg"
                                               required min="08:00" max="20:00">
                                        <div class="form-text">Clinic hours: 8:00 AM – 8:00 PM</div>
                                        <div class="invalid-feedback">Please select a time.</div>
                                    </div>
                                </div>

                                <div class="mb-4">
                                    <label class="form-label fw-semibold">
                                        <i class="bi bi-chat-text me-1"></i>Reason for Visit
                                    </label>
                                    <textarea name="reason" class="form-control" rows="3"
                                              placeholder="Briefly describe your symptoms or reason..."></textarea>
                                </div>

                                <%-- Important note --%>
                                <div class="alert alert-warning py-2 small mb-4">
                                    <i class="bi bi-exclamation-triangle me-2"></i>
                                    <strong>Note:</strong> Double-bookings are not allowed. If the selected
                                    time slot is already taken, you will be notified immediately.
                                </div>

                                <div class="d-flex gap-3">
                                    <a href="${pageContext.request.contextPath}/patient/search-doctors"
                                       class="btn btn-outline-secondary btn-lg flex-grow-1">
                                        <i class="bi bi-arrow-left me-1"></i>Back to Search
                                    </a>
                                    <button type="submit" class="btn btn-info text-white btn-lg flex-grow-1"
                                            id="bookBtn">
                                        <i class="bi bi-calendar-check me-2"></i>Confirm Booking
                                    </button>
                                </div>

                            </form>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
<script>
    // Minimum date = today
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('appointmentDate').setAttribute('min', today);

    // Show doctor preview on selection
    const doctorSelect = document.getElementById('doctorSelect');
    const preview      = document.getElementById('doctorPreview');
    const previewText  = document.getElementById('previewText');

    doctorSelect.addEventListener('change', function () {
        const opt = this.options[this.selectedIndex];
        if (this.value) {
            previewText.textContent = opt.getAttribute('data-spec')
                + ' | ' + opt.getAttribute('data-dept')
                + ' | Consultation Fee: ₹' + opt.getAttribute('data-fee');
            preview.style.display = '';
        } else {
            preview.style.display = 'none';
        }
    });

    // Trigger on load if pre-selected
    if (doctorSelect.value) {
        doctorSelect.dispatchEvent(new Event('change'));
    }

    // Form validation + submit spinner
    document.getElementById('bookingForm').addEventListener('submit', function (e) {
        if (!this.checkValidity()) {
            e.preventDefault();
            e.stopPropagation();
        } else {
            document.getElementById('bookBtn').innerHTML =
                '<span class="spinner-border spinner-border-sm me-2"></span>Booking...';
        }
        this.classList.add('was-validated');
    });
</script>
</body>
</html>
