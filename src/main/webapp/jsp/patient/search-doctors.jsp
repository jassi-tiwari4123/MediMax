<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Find Doctors — OHMS</title>
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
                <a class="nav-link sidebar-link active"
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
                <i class="bi bi-search-heart me-2 text-info"></i>Find a Doctor
            </span>
        </nav>

        <div class="p-4">

            <%-- Search & Filter --%>
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body">
                    <form method="get"
                          action="${pageContext.request.contextPath}/patient/search-doctors"
                          class="row g-3 align-items-end">
                        <div class="col-md-5">
                            <label class="form-label fw-semibold small">
                                <i class="bi bi-search me-1"></i>Search by Name
                            </label>
                            <input type="text" name="name" class="form-control"
                                   placeholder="Doctor name..." value="${searchName}">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold small">
                                <i class="bi bi-building me-1"></i>Filter by Department
                            </label>
                            <select name="departmentId" class="form-select">
                                <option value="0">All Departments</option>
                                <c:forEach var="dept" items="${departments}">
                                    <option value="${dept.id}"
                                            ${searchDept eq dept.id.toString() ? 'selected' : ''}>
                                        ${dept.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-3 d-flex gap-2">
                            <button type="submit" class="btn btn-primary flex-grow-1">
                                <i class="bi bi-search me-1"></i>Search
                            </button>
                            <a href="${pageContext.request.contextPath}/patient/search-doctors"
                               class="btn btn-outline-secondary">
                                <i class="bi bi-x-circle"></i>
                            </a>
                        </div>
                    </form>
                </div>
            </div>

            <%-- Results --%>
            <div class="row g-4">
                <c:forEach var="doc" items="${doctors}">
                    <div class="col-md-6 col-xl-4">
                        <div class="card border-0 shadow-sm h-100 doctor-card"
                             style="transition:transform 0.2s;">
                            <div class="card-body">
                                <div class="d-flex align-items-start gap-3 mb-3">
                                    <div class="rounded-circle bg-success-subtle text-success d-flex align-items-center justify-content-center flex-shrink-0"
                                         style="width:56px;height:56px;font-size:1.5rem;">
                                        <i class="bi bi-person-circle"></i>
                                    </div>
                                    <div>
                                        <h6 class="fw-bold mb-0">Dr. ${doc.fullName}</h6>
                                        <div class="text-success small fw-semibold">${doc.specialization}</div>
                                        <div class="text-muted small">${doc.department.name}</div>
                                    </div>
                                </div>

                                <div class="row g-2 mb-3 text-center">
                                    <div class="col-4">
                                        <div class="bg-light rounded p-2">
                                            <div class="fw-bold text-primary">${doc.experienceYears}</div>
                                            <div class="text-muted" style="font-size:0.7rem;">Yrs Exp</div>
                                        </div>
                                    </div>
                                    <div class="col-4">
                                        <div class="bg-light rounded p-2">
                                            <div class="fw-bold text-success">₹${doc.consultationFee}</div>
                                            <div class="text-muted" style="font-size:0.7rem;">Consult Fee</div>
                                        </div>
                                    </div>
                                    <div class="col-4">
                                        <div class="bg-light rounded p-2">
                                            <span class="badge bg-success">Available</span>
                                        </div>
                                    </div>
                                </div>

                                <div class="text-muted small mb-3">
                                    <i class="bi bi-mortarboard me-1"></i>${doc.qualification}
                                </div>

                                <c:if test="${not empty doc.availableDays}">
                                    <div class="text-muted small mb-3">
                                        <i class="bi bi-calendar-week me-1"></i>${doc.availableDays}
                                        <c:if test="${not empty doc.availableFrom}">
                                            &bull; ${doc.availableFrom} – ${doc.availableTo}
                                        </c:if>
                                    </div>
                                </c:if>

                                <a href="${pageContext.request.contextPath}/patient/book-appointment?doctorId=${doc.id}"
                                   class="btn btn-info text-white w-100">
                                    <i class="bi bi-calendar-plus me-2"></i>Book Appointment
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>

                <c:if test="${empty doctors}">
                    <div class="col-12">
                        <div class="card border-0 shadow-sm">
                            <div class="card-body text-center py-5 text-muted">
                                <i class="bi bi-person-badge fs-1 d-block mb-3 opacity-50"></i>
                                <h5>No Doctors Found</h5>
                                <p class="mb-0">Try adjusting your search or department filter.</p>
                            </div>
                        </div>
                    </div>
                </c:if>
            </div>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
<script>
    // Card hover effect
    document.querySelectorAll('.doctor-card').forEach(card => {
        card.addEventListener('mouseenter', () => card.style.transform = 'translateY(-4px)');
        card.addEventListener('mouseleave', () => card.style.transform = 'translateY(0)');
    });
</script>
</body>
</html>
