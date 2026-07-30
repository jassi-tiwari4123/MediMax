<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard — OHMS</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="wrapper d-flex">
    <%@ include file="../common/admin-sidebar.jspf" %>

    <div class="main-content flex-grow-1">

        <!-- Top Navbar -->
        <nav class="navbar navbar-light bg-white border-bottom px-4 shadow-sm">
            <button class="btn btn-sm btn-outline-secondary me-2" id="sidebarToggle">
                <i class="bi bi-list"></i>
            </button>
            <span class="navbar-brand mb-0 h5">
                <i class="bi bi-speedometer2 me-2 text-primary"></i>Dashboard
            </span>
            <div class="ms-auto d-flex align-items-center gap-3">
                <span class="badge bg-primary">Admin</span>
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

            <h4 class="mb-4">Welcome back, <strong>Admin</strong>
                <small class="text-muted fs-6 fw-normal">— Overview</small>
            </h4>

            <!-- ── Stat Cards ── -->
            <div class="row g-4 mb-4">

                <div class="col-sm-6 col-xl-3">
                    <div class="card stat-card border-0 shadow-sm">
                        <div class="card-body d-flex align-items-center gap-3">
                            <div class="stat-icon bg-primary-subtle text-primary rounded-3 p-3">
                                <i class="bi bi-people fs-3"></i>
                            </div>
                            <div>
                                <div class="stat-value fw-bold fs-3">${totalPatients}</div>
                                <div class="text-muted small">Total Patients</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-sm-6 col-xl-3">
                    <div class="card stat-card border-0 shadow-sm">
                        <div class="card-body d-flex align-items-center gap-3">
                            <div class="stat-icon bg-success-subtle text-success rounded-3 p-3">
                                <i class="bi bi-person-badge fs-3"></i>
                            </div>
                            <div>
                                <div class="stat-value fw-bold fs-3">${totalDoctors}</div>
                                <div class="text-muted small">Active Doctors</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-sm-6 col-xl-3">
                    <div class="card stat-card border-0 shadow-sm">
                        <div class="card-body d-flex align-items-center gap-3">
                            <div class="stat-icon bg-warning-subtle text-warning rounded-3 p-3">
                                <i class="bi bi-hourglass-split fs-3"></i>
                            </div>
                            <div>
                                <div class="stat-value fw-bold fs-3">${pendingDoctors}</div>
                                <div class="text-muted small">Pending Approvals</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-sm-6 col-xl-3">
                    <div class="card stat-card border-0 shadow-sm">
                        <div class="card-body d-flex align-items-center gap-3">
                            <div class="stat-icon bg-info-subtle text-info rounded-3 p-3">
                                <i class="bi bi-calendar-check fs-3"></i>
                            </div>
                            <div>
                                <div class="stat-value fw-bold fs-3">${completedAppointments}</div>
                                <div class="text-muted small">Completed Appts</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ── Appointment Status Summary ── -->
            <div class="row g-4 mb-4">
                <div class="col-md-6">
                    <div class="card border-0 shadow-sm h-100">
                        <div class="card-header bg-white fw-semibold border-bottom">
                            <i class="bi bi-bar-chart me-2 text-primary"></i>Appointment Overview
                        </div>
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span><span class="badge bg-warning text-dark me-2">●</span>Pending</span>
                                <strong>${pendingAppointments}</strong>
                            </div>
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span><span class="badge bg-primary me-2">●</span>Confirmed</span>
                                <strong>${confirmedAppointments}</strong>
                            </div>
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span><span class="badge bg-success me-2">●</span>Completed</span>
                                <strong>${completedAppointments}</strong>
                            </div>
                            <div class="d-flex justify-content-between align-items-center">
                                <span><span class="badge bg-danger me-2">●</span>Cancelled</span>
                                <strong>${cancelledAppointments}</strong>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- ── Quick Links ── -->
                <div class="col-md-6">
                    <div class="card border-0 shadow-sm h-100">
                        <div class="card-header bg-white fw-semibold border-bottom">
                            <i class="bi bi-lightning me-2 text-warning"></i>Quick Actions
                        </div>
                        <div class="card-body d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/admin/doctors?action=pending"
                               class="btn btn-outline-warning">
                                <i class="bi bi-person-check me-2"></i>
                                Review Pending Doctors (${pendingDoctors})
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/appointments?status=PENDING"
                               class="btn btn-outline-primary">
                                <i class="bi bi-calendar-event me-2"></i>
                                View Pending Appointments
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/departments"
                               class="btn btn-outline-secondary">
                                <i class="bi bi-building me-2"></i>Manage Departments
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ── Recent Appointments Table ── -->
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white fw-semibold border-bottom d-flex justify-content-between">
                    <span><i class="bi bi-clock-history me-2 text-primary"></i>Recent Appointments</span>
                    <a href="${pageContext.request.contextPath}/admin/appointments"
                       class="btn btn-sm btn-outline-primary">View All</a>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Patient</th>
                                    <th>Doctor</th>
                                    <th>Date</th>
                                    <th>Time</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="appt" items="${recentAppointments}" varStatus="loop">
                                    <c:if test="${loop.index < 10}">
                                        <tr>
                                            <td>${appt.id}</td>
                                            <td>${appt.patient != null ? appt.patient.fullName : appt.patientId}</td>
                                            <td>${appt.doctor != null ? 'Dr. '.concat(appt.doctor.fullName) : appt.doctorId}</td>
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
                                                ">
                                                    ${appt.status.displayName}
                                                </span>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                                <c:if test="${empty recentAppointments}">
                                    <tr><td colspan="6" class="text-center text-muted py-4">No appointments found.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

        </div><!-- /p-4 -->
    </div><!-- /main-content -->
</div><!-- /wrapper -->

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
</body>
</html>
