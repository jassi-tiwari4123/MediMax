<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>All Appointments — OHMS</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="wrapper d-flex">
    <%@ include file="../common/admin-sidebar.jspf" %>

    <div class="main-content flex-grow-1">
        <nav class="navbar navbar-light bg-white border-bottom px-4 shadow-sm">
            <button class="btn btn-sm btn-outline-secondary me-2" id="sidebarToggle">
                <i class="bi bi-list"></i>
            </button>
            <span class="navbar-brand mb-0 h5">
                <i class="bi bi-calendar3 me-2 text-primary"></i>All Appointments
            </span>
            <div class="ms-auto"><span class="badge bg-primary">Admin</span></div>
        </nav>

        <div class="p-4">

            <%-- Status filter tabs --%>
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body py-2">
                    <div class="d-flex gap-2 flex-wrap">
                        <a href="${pageContext.request.contextPath}/admin/appointments?status=ALL"
                           class="btn btn-sm ${selectedStatus eq 'ALL' ? 'btn-primary' : 'btn-outline-primary'}">
                            All
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/appointments?status=PENDING"
                           class="btn btn-sm ${selectedStatus eq 'PENDING' ? 'btn-warning' : 'btn-outline-warning'}">
                            Pending
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/appointments?status=CONFIRMED"
                           class="btn btn-sm ${selectedStatus eq 'CONFIRMED' ? 'btn-primary' : 'btn-outline-primary'}">
                            Confirmed
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/appointments?status=COMPLETED"
                           class="btn btn-sm ${selectedStatus eq 'COMPLETED' ? 'btn-success' : 'btn-outline-success'}">
                            Completed
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/appointments?status=CANCELLED"
                           class="btn btn-sm ${selectedStatus eq 'CANCELLED' ? 'btn-danger' : 'btn-outline-danger'}">
                            Cancelled
                        </a>
                    </div>
                </div>
            </div>

            <%-- Appointments Table --%>
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white fw-semibold">
                    <i class="bi bi-calendar3 me-2 text-primary"></i>
                    Appointments — <span class="text-muted">${selectedStatus}</span>
                    <span class="badge bg-secondary ms-2">${appointments.size()}</span>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>Patient</th>
                                    <th>Doctor</th>
                                    <th>Date</th>
                                    <th>Time</th>
                                    <th>Reason</th>
                                    <th>Status</th>
                                    <th>Booked On</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="appt" items="${appointments}">
                                    <tr>
                                        <td><span class="text-muted small">#${appt.id}</span></td>
                                        <td>
                                            <div class="fw-semibold">${appt.patient != null ? appt.patient.fullName : 'Patient #'.concat(appt.patientId)}</div>
                                        </td>
                                        <td>
                                            <div class="fw-semibold">${appt.doctor != null ? 'Dr. '.concat(appt.doctor.fullName) : 'Doctor #'.concat(appt.doctorId)}</div>
                                            <small class="text-muted">
                                                ${appt.doctor != null ? appt.doctor.specialization : ''}
                                            </small>
                                        </td>
                                        <td>${appt.appointmentDate}</td>
                                        <td>${appt.appointmentTime}</td>
                                        <td>
                                            <small class="text-muted">
                                                ${not empty appt.reason ? appt.reason : '—'}
                                            </small>
                                        </td>
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
                                            <small class="text-muted">${appt.createdAt}</small>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty appointments}">
                                    <tr>
                                        <td colspan="8" class="text-center text-muted py-5">
                                            <i class="bi bi-calendar-x fs-2 d-block mb-2"></i>
                                            No appointments found for this filter.
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
</body>
</html>
