<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Patient Dashboard — OHMS</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="wrapper d-flex">

    <!-- Patient Sidebar -->
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
                <a class="nav-link sidebar-link active"
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
                <a class="nav-link sidebar-link"
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
                <i class="bi bi-person-heart me-2 text-info"></i>Patient Dashboard
            </span>
            <div class="ms-auto d-flex align-items-center gap-3">
                <span class="fw-semibold text-info">
                    <i class="bi bi-person-circle me-1"></i>${patient.fullName}
                </span>
                <a href="${pageContext.request.contextPath}/logout"
                   class="btn btn-sm btn-outline-danger">
                    <i class="bi bi-box-arrow-right me-1"></i>Logout
                </a>
            </div>
        </nav>

        <div class="p-4">

            <!-- Flash messages -->
            <c:if test="${not empty sessionScope.flashSuccess}">
                <div class="alert alert-success alert-dismissible fade show">
                    <i class="bi bi-check-circle me-2"></i>${sessionScope.flashSuccess}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <c:remove var="flashSuccess" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.flashError}">
                <div class="alert alert-danger alert-dismissible fade show">
                    <i class="bi bi-exclamation-triangle me-2"></i>${sessionScope.flashError}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <c:remove var="flashError" scope="session"/>
            </c:if>

            <!-- Patient Profile Card -->
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body d-flex align-items-center gap-4">
                    <div class="rounded-circle bg-info-subtle text-info d-flex align-items-center justify-content-center"
                         style="width:72px;height:72px;font-size:2rem;">
                        <i class="bi bi-person-circle"></i>
                    </div>
                    <div>
                        <h5 class="mb-0">${patient.fullName}</h5>
                        <div class="text-muted">${patient.user.email} &bull; ${patient.user.phone}</div>
                        <c:if test="${not empty patient.bloodGroup}">
                            <span class="badge bg-danger">${patient.bloodGroup}</span>
                        </c:if>
                    </div>
                    <div class="ms-auto">
                        <a href="${pageContext.request.contextPath}/patient/book-appointment"
                           class="btn btn-info text-white">
                            <i class="bi bi-calendar-plus me-2"></i>Book Appointment
                        </a>
                    </div>
                </div>
            </div>

            <!-- Appointments -->
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-header bg-white fw-semibold d-flex justify-content-between">
                    <span><i class="bi bi-calendar3 me-2 text-info"></i>My Appointments</span>
                    <a href="${pageContext.request.contextPath}/patient/book-appointment"
                       class="btn btn-sm btn-outline-info">+ Book New</a>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Doctor</th>
                                    <th>Date</th>
                                    <th>Time</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="appt" items="${appointments}" varStatus="loop">
                                    <tr>
                                        <td>${loop.count}</td>
                                        <td>Dr. ${appt.doctorId}</td>
                                        <td>${appt.appointmentDate}</td>
                                        <td>${appt.appointmentTime}</td>
                                        <td>
                                            <span class="badge
                                                <c:choose>
                                                    <c:when test='${appt.status.name() eq "PENDING"}'>bg-warning text-dark</c:when>
                                                    <c:when test='${appt.status.name() eq "CONFIRMED"}'>bg-primary</c:when>
                                                    <c:when test='${appt.status.name() eq "COMPLETED"}'>bg-success</c:when>
                                                    <c:when test='${appt.status.name() eq "CANCELLED"}'>bg-danger</c:when>
                                                    <c:otherwise>bg-secondary</c:otherwise>
                                                </c:choose>
                                            ">${appt.status.displayName}</span>
                                        </td>
                                        <td>
                                            <c:if test="${appt.status.name() eq 'PENDING' or appt.status.name() eq 'CONFIRMED'}">
                                                <button type="button"
                                                        class="btn btn-sm btn-outline-danger"
                                                        data-bs-toggle="modal"
                                                        data-bs-target="#cancelModal"
                                                        data-apptid="${appt.id}">
                                                    <i class="bi bi-x-circle me-1"></i>Cancel
                                                </button>
                                            </c:if>
                                            <c:if test="${appt.status.name() eq 'COMPLETED'}">
                                                <a href="${pageContext.request.contextPath}/patient/download-prescription?prescriptionId=${appt.id}"
                                                   class="btn btn-sm btn-outline-success">
                                                    <i class="bi bi-file-earmark-pdf me-1"></i>Rx
                                                </a>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty appointments}">
                                    <tr>
                                        <td colspan="6" class="text-center text-muted py-5">
                                            <i class="bi bi-calendar-x fs-2 d-block mb-2"></i>
                                            No appointments yet.
                                            <a href="${pageContext.request.contextPath}/patient/book-appointment">Book one now</a>
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Prescriptions -->
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white fw-semibold">
                    <i class="bi bi-file-medical me-2 text-success"></i>My Prescriptions
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Diagnosis</th>
                                    <th>Date</th>
                                    <th>Follow-up</th>
                                    <th>Download</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="presc" items="${prescriptions}" varStatus="loop">
                                    <tr>
                                        <td>${loop.count}</td>
                                        <td>${presc.diagnosis}</td>
                                        <td>${presc.createdAt}</td>
                                        <td>${presc.followUpDate != null ? presc.followUpDate : '—'}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/patient/download-prescription?prescriptionId=${presc.id}"
                                               class="btn btn-sm btn-success">
                                                <i class="bi bi-download me-1"></i>PDF
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty prescriptions}">
                                    <tr>
                                        <td colspan="5" class="text-center text-muted py-4">
                                            No prescriptions available yet.
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>

<!-- Cancel Appointment Modal -->
<div class="modal fade" id="cancelModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form method="post"
                  action="${pageContext.request.contextPath}/patient/book-appointment">
                <input type="hidden" name="action" value="cancel">
                <input type="hidden" name="appointmentId" id="cancelApptId">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="bi bi-x-circle text-danger me-2"></i>Cancel Appointment
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Reason for cancellation</label>
                        <textarea name="cancelReason" class="form-control" rows="3"
                                  placeholder="Optional reason..."></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        Keep Appointment
                    </button>
                    <button type="submit" class="btn btn-danger">
                        <i class="bi bi-x-circle me-1"></i>Confirm Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
<script>
    // Pass appointment ID to cancel modal
    document.getElementById('cancelModal').addEventListener('show.bs.modal', function(e) {
        const apptId = e.relatedTarget.getAttribute('data-apptid');
        document.getElementById('cancelApptId').value = apptId;
    });
</script>
</body>
</html>
