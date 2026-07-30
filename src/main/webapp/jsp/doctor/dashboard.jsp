<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Doctor Dashboard — OHMS</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="wrapper d-flex">

    <!-- Doctor Sidebar -->
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
                <a class="nav-link sidebar-link active"
                   href="${pageContext.request.contextPath}/doctor/dashboard">
                    <i class="bi bi-speedometer2"></i> Dashboard
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link sidebar-link"
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
                <i class="bi bi-speedometer2 me-2 text-success"></i>Doctor Dashboard
            </span>
            <div class="ms-auto d-flex align-items-center gap-3">
                <span class="fw-semibold text-success">
                    <i class="bi bi-person-circle me-1"></i>Dr. ${doctor.fullName}
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

            <!-- Doctor Profile Card -->
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body d-flex align-items-center gap-4">
                    <div class="rounded-circle bg-success-subtle text-success d-flex align-items-center justify-content-center"
                         style="width:72px;height:72px;font-size:2rem;">
                        <i class="bi bi-person-circle"></i>
                    </div>
                    <div>
                        <h5 class="mb-0">Dr. ${doctor.fullName}</h5>
                        <div class="text-muted">${doctor.specialization} &bull; ${doctor.department.name}</div>
                        <div class="text-muted small">${doctor.qualification} &bull; ${doctor.experienceYears} years exp.</div>
                    </div>
                    <div class="ms-auto">
                        <span class="badge bg-success fs-6 px-3 py-2">
                            <i class="bi bi-patch-check me-1"></i>${doctor.status.displayName}
                        </span>
                    </div>
                </div>
            </div>

            <!-- Stats Row -->
            <div class="row g-4 mb-4">
                <div class="col-sm-4">
                    <div class="card border-0 shadow-sm text-center py-3">
                        <div class="fs-2 fw-bold text-warning">${pendingCount}</div>
                        <div class="text-muted small">Pending</div>
                    </div>
                </div>
                <div class="col-sm-4">
                    <div class="card border-0 shadow-sm text-center py-3">
                        <div class="fs-2 fw-bold text-primary">${confirmedCount}</div>
                        <div class="text-muted small">Confirmed</div>
                    </div>
                </div>
                <div class="col-sm-4">
                    <div class="card border-0 shadow-sm text-center py-3">
                        <div class="fs-2 fw-bold text-success">${completedCount}</div>
                        <div class="text-muted small">Completed</div>
                    </div>
                </div>
            </div>

            <!-- Today's Appointments -->
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white fw-semibold d-flex justify-content-between">
                    <span><i class="bi bi-calendar-day me-2 text-success"></i>My Appointments</span>
                    <a href="${pageContext.request.contextPath}/doctor/appointments"
                       class="btn btn-sm btn-outline-success">View All</a>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Patient</th>
                                    <th>Date</th>
                                    <th>Time</th>
                                    <th>Reason</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="appt" items="${appointments}" varStatus="loop">
                                    <c:if test="${loop.index < 8}">
                                        <tr>
                                            <td>${loop.count}</td>
                                            <td>${appt.patientId}</td>
                                            <td>${appt.appointmentDate}</td>
                                            <td>${appt.appointmentTime}</td>
                                            <td><small>${appt.reason}</small></td>
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
                                                <a href="${pageContext.request.contextPath}/doctor/appointments"
                                                   class="btn btn-sm btn-outline-success">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                                <c:if test="${empty appointments}">
                                    <tr>
                                        <td colspan="7" class="text-center text-muted py-5">
                                            <i class="bi bi-calendar-x fs-2 d-block mb-2"></i>
                                            No appointments yet.
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
