<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} — OHMS</title>
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
                <i class="bi bi-person-badge me-2 text-primary"></i>${pageTitle}
            </span>
            <div class="ms-auto">
                <span class="badge bg-primary">Admin</span>
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

            <!-- Search + Filter bar -->
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body">
                    <form method="get" action="${pageContext.request.contextPath}/admin/doctors"
                          class="row g-2 align-items-end">
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold">Search by Name</label>
                            <input type="text" name="search" class="form-control"
                                   placeholder="Doctor name..." value="${search}">
                        </div>
                        <div class="col-md-3">
                            <button type="submit" class="btn btn-primary w-100">
                                <i class="bi bi-search me-1"></i>Search
                            </button>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/admin/doctors?action=pending"
                               class="btn btn-warning w-100">
                                <i class="bi bi-hourglass-split me-1"></i>Pending Only
                            </a>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Doctors Table -->
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white fw-semibold">
                    <i class="bi bi-person-badge me-2 text-primary"></i>
                    Doctors (${doctors.size()} found)
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Department</th>
                                    <th>Specialization</th>
                                    <th>Exp</th>
                                    <th>Fee</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="doc" items="${doctors}" varStatus="loop">
                                    <tr>
                                        <td>${loop.count}</td>
                                        <td>
                                            <div class="d-flex align-items-center gap-2">
                                                <div class="avatar-sm bg-primary-subtle text-primary rounded-circle d-flex align-items-center justify-content-center" style="width:36px;height:36px;">
                                                    <i class="bi bi-person"></i>
                                                </div>
                                                <div>
                                                    <div class="fw-semibold">Dr. ${doc.fullName}</div>
                                                    <small class="text-muted">${doc.qualification}</small>
                                                </div>
                                            </div>
                                        </td>
                                        <td><small>${doc.user.email}</small></td>
                                        <td>${doc.department.name}</td>
                                        <td>${doc.specialization}</td>
                                        <td>${doc.experienceYears} yrs</td>
                                        <td>₹${doc.consultationFee}</td>
                                        <td>
                                            <span class="badge
                                                <c:choose>
                                                    <c:when test='${doc.status.name() eq "APPROVED"}'>bg-success</c:when>
                                                    <c:when test='${doc.status.name() eq "PENDING"}'>bg-warning text-dark</c:when>
                                                    <c:when test='${doc.status.name() eq "REJECTED"}'>bg-danger</c:when>
                                                    <c:otherwise>bg-secondary</c:otherwise>
                                                </c:choose>
                                            ">${doc.status.displayName}</span>
                                        </td>
                                        <td>
                                            <div class="d-flex gap-1">
                                                <c:if test="${doc.status.name() eq 'PENDING'}">
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/admin/doctors"
                                                          style="display:inline;">
                                                        <input type="hidden" name="action" value="approve">
                                                        <input type="hidden" name="doctorId" value="${doc.id}">
                                                        <button type="submit"
                                                                class="btn btn-success btn-sm"
                                                                title="Approve"
                                                                onclick="return confirm('Approve Dr. ${doc.fullName}?')">
                                                            <i class="bi bi-check-circle"></i>
                                                        </button>
                                                    </form>
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/admin/doctors"
                                                          style="display:inline;">
                                                        <input type="hidden" name="action" value="reject">
                                                        <input type="hidden" name="doctorId" value="${doc.id}">
                                                        <button type="submit"
                                                                class="btn btn-danger btn-sm"
                                                                title="Reject"
                                                                onclick="return confirm('Reject Dr. ${doc.fullName}?')">
                                                            <i class="bi bi-x-circle"></i>
                                                        </button>
                                                    </form>
                                                </c:if>
                                                <c:if test="${doc.status.name() eq 'APPROVED'}">
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/admin/doctors"
                                                          style="display:inline;">
                                                        <input type="hidden" name="action" value="deactivate">
                                                        <input type="hidden" name="doctorId" value="${doc.id}">
                                                        <button type="submit"
                                                                class="btn btn-outline-danger btn-sm"
                                                                title="Deactivate"
                                                                onclick="return confirm('Deactivate Dr. ${doc.fullName}?')">
                                                            <i class="bi bi-person-x"></i>
                                                        </button>
                                                    </form>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty doctors}">
                                    <tr>
                                        <td colspan="9" class="text-center text-muted py-5">
                                            <i class="bi bi-person-badge fs-2 d-block mb-2"></i>
                                            No doctors found.
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
