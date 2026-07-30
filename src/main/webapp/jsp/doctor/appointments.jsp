<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Appointments — OHMS</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="wrapper d-flex">

    <%-- Doctor Sidebar --%>
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
                <i class="bi bi-calendar3 me-2 text-success"></i>My Appointments
            </span>
            <div class="ms-auto">
                <span class="fw-semibold text-success">Dr. ${doctor.fullName}</span>
            </div>
        </nav>

        <div class="p-4">

            <%-- Flash messages --%>
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

            <%-- Appointments Table --%>
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white fw-semibold">
                    <i class="bi bi-calendar3 me-2 text-success"></i>
                    All Appointments (${appointments.size()})
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Patient ID</th>
                                    <th>Date</th>
                                    <th>Time</th>
                                    <th>Reason</th>
                                    <th>Notes</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="appt" items="${appointments}" varStatus="loop">
                                    <tr>
                                        <td>${loop.count}</td>
                                        <td><span class="badge bg-light text-dark border">#${appt.patientId}</span></td>
                                        <td><strong>${appt.appointmentDate}</strong></td>
                                        <td>${appt.appointmentTime}</td>
                                        <td><small class="text-muted">${not empty appt.reason ? appt.reason : '—'}</small></td>
                                        <td><small class="text-muted">${not empty appt.notes ? appt.notes : '—'}</small></td>
                                        <td>
                                            <span class="badge
                                                <c:choose>
                                                    <c:when test='${appt.status.name() eq "PENDING"}'>bg-warning text-dark</c:when>
                                                    <c:when test='${appt.status.name() eq "CONFIRMED"}'>bg-primary</c:when>
                                                    <c:when test='${appt.status.name() eq "COMPLETED"}'>bg-success</c:when>
                                                    <c:when test='${appt.status.name() eq "CANCELLED"}'>bg-danger</c:when>
                                                    <c:otherwise>bg-secondary</c:otherwise>
                                                </c:choose>
                                            ">
                                                ${appt.status.displayName}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="d-flex gap-1 flex-wrap">
                                                <%-- Confirm (only if PENDING) --%>
                                                <c:if test="${appt.status.name() eq 'PENDING'}">
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/doctor/appointments"
                                                          style="display:inline;">
                                                        <input type="hidden" name="action" value="confirm">
                                                        <input type="hidden" name="appointmentId" value="${appt.id}">
                                                        <button type="submit"
                                                                class="btn btn-sm btn-success"
                                                                onclick="return confirm('Confirm this appointment?')">
                                                            <i class="bi bi-check-circle"></i> Confirm
                                                        </button>
                                                    </form>
                                                </c:if>

                                                <%-- Complete (only if CONFIRMED) — opens modal --%>
                                                <c:if test="${appt.status.name() eq 'CONFIRMED'}">
                                                    <button type="button"
                                                            class="btn btn-sm btn-primary"
                                                            data-bs-toggle="modal"
                                                            data-bs-target="#completeModal"
                                                            data-apptid="${appt.id}">
                                                        <i class="bi bi-clipboard2-check"></i> Complete
                                                    </button>
                                                    <%-- Prescription (only for CONFIRMED) --%>
                                                    <a href="${pageContext.request.contextPath}/doctor/prescription?appointmentId=${appt.id}"
                                                       class="btn btn-sm btn-outline-primary">
                                                        <i class="bi bi-file-earmark-plus"></i> Prescribe
                                                    </a>
                                                </c:if>

                                                <%-- Prescription for COMPLETED --%>
                                                <c:if test="${appt.status.name() eq 'COMPLETED'}">
                                                    <a href="${pageContext.request.contextPath}/doctor/prescription?appointmentId=${appt.id}"
                                                       class="btn btn-sm btn-outline-success">
                                                        <i class="bi bi-file-earmark-plus"></i> Prescription
                                                    </a>
                                                </c:if>

                                                <%-- Cancel (PENDING or CONFIRMED) --%>
                                                <c:if test="${appt.status.name() eq 'PENDING' or appt.status.name() eq 'CONFIRMED'}">
                                                    <button type="button"
                                                            class="btn btn-sm btn-outline-danger"
                                                            data-bs-toggle="modal"
                                                            data-bs-target="#cancelModal"
                                                            data-apptid="${appt.id}">
                                                        <i class="bi bi-x-circle"></i>
                                                    </button>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty appointments}">
                                    <tr>
                                        <td colspan="8" class="text-center text-muted py-5">
                                            <i class="bi bi-calendar-x fs-2 d-block mb-2"></i>
                                            No appointments found.
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

<%-- Complete Appointment Modal --%>
<div class="modal fade" id="completeModal" tabindex="-1">
    <div class="modal-dialog">
        <form method="post" action="${pageContext.request.contextPath}/doctor/appointments">
            <input type="hidden" name="action" value="complete">
            <input type="hidden" name="appointmentId" id="completeApptId">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="bi bi-clipboard2-check me-2 text-success"></i>Complete Appointment</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Diagnosis</label>
                        <textarea name="diagnosis" class="form-control" rows="3"
                                  placeholder="Enter diagnosis..." required></textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Doctor Notes</label>
                        <textarea name="notes" class="form-control" rows="3"
                                  placeholder="Internal notes (not shown to patient)..."></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success">
                        <i class="bi bi-check-circle me-1"></i>Mark as Completed
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<%-- Cancel Appointment Modal --%>
<div class="modal fade" id="cancelModal" tabindex="-1">
    <div class="modal-dialog">
        <form method="post" action="${pageContext.request.contextPath}/doctor/appointments">
            <input type="hidden" name="action" value="cancel">
            <input type="hidden" name="appointmentId" id="cancelApptId">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="bi bi-x-circle me-2 text-danger"></i>Cancel Appointment</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Reason for Cancellation</label>
                        <textarea name="cancelReason" class="form-control" rows="3"
                                  placeholder="Please provide a reason..."></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Keep</button>
                    <button type="submit" class="btn btn-danger">
                        <i class="bi bi-x-circle me-1"></i>Confirm Cancel
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
<script>
    document.getElementById('completeModal').addEventListener('show.bs.modal', function(e) {
        document.getElementById('completeApptId').value = e.relatedTarget.getAttribute('data-apptid');
    });
    document.getElementById('cancelModal').addEventListener('show.bs.modal', function(e) {
        document.getElementById('cancelApptId').value = e.relatedTarget.getAttribute('data-apptid');
    });
</script>
</body>
</html>
