<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Patients — OHMS</title>
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
                <i class="bi bi-people me-2 text-primary"></i>Manage Patients
            </span>
            <div class="ms-auto"><span class="badge bg-primary">Admin</span></div>
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

            <%-- Search bar --%>
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body">
                    <form method="get"
                          action="${pageContext.request.contextPath}/admin/patients"
                          class="row g-2 align-items-end">
                        <div class="col-md-7">
                            <label class="form-label small fw-semibold">Search by Patient Name</label>
                            <input type="text" name="search" class="form-control"
                                   placeholder="Patient name..." value="${search}">
                        </div>
                        <div class="col-md-3">
                            <button type="submit" class="btn btn-primary w-100">
                                <i class="bi bi-search me-1"></i>Search
                            </button>
                        </div>
                        <div class="col-md-2">
                            <a href="${pageContext.request.contextPath}/admin/patients"
                               class="btn btn-outline-secondary w-100">
                                <i class="bi bi-x-circle me-1"></i>Clear
                            </a>
                        </div>
                    </form>
                </div>
            </div>

            <%-- Patients Table --%>
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white fw-semibold">
                    <i class="bi bi-people me-2 text-primary"></i>
                    Patients (${patients.size()} found)
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Phone</th>
                                    <th>Blood Group</th>
                                    <th>Gender</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="pat" items="${patients}" varStatus="loop">
                                    <tr>
                                        <td>${loop.count}</td>
                                        <td>
                                            <div class="d-flex align-items-center gap-2">
                                                <div class="rounded-circle bg-info-subtle text-info d-flex align-items-center justify-content-center"
                                                     style="width:36px;height:36px;">
                                                    <i class="bi bi-person"></i>
                                                </div>
                                                <div class="fw-semibold">${pat.fullName}</div>
                                            </div>
                                        </td>
                                        <td><small>${pat.user.email}</small></td>
                                        <td>${pat.user.phone}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty pat.bloodGroup}">
                                                    <span class="badge bg-danger">${pat.bloodGroup}</span>
                                                </c:when>
                                                <c:otherwise><span class="text-muted">—</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${pat.user.gender.displayName}</td>
                                        <td>
                                            <span class="badge ${pat.user.active ? 'bg-success' : 'bg-secondary'}">
                                                ${pat.user.active ? 'Active' : 'Inactive'}
                                            </span>
                                        </td>
                                        <td>
                                            <c:if test="${pat.user.active}">
                                                <form method="post"
                                                      action="${pageContext.request.contextPath}/admin/patients"
                                                      style="display:inline;">
                                                    <input type="hidden" name="action" value="deactivate">
                                                    <input type="hidden" name="patientId" value="${pat.id}">
                                                    <button type="submit"
                                                            class="btn btn-sm btn-outline-danger"
                                                            onclick="return confirm('Deactivate ${pat.fullName}?')">
                                                        <i class="bi bi-person-dash"></i> Deactivate
                                                    </button>
                                                </form>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty patients}">
                                    <tr>
                                        <td colspan="8" class="text-center text-muted py-5">
                                            <i class="bi bi-people fs-2 d-block mb-2"></i>
                                            No patients found.
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div><%-- /p-4 --%>
    </div><%-- /main-content --%>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
</body>
</html>
