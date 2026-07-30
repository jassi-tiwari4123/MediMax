<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile — Doctor</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="wrapper d-flex">

    <nav id="sidebar" class="sidebar d-flex flex-column">
        <div class="sidebar-brand d-flex align-items-center p-3">
            <i class="bi bi-hospital fs-4 me-2 text-white"></i>
            <div><div class="fw-bold text-white lh-1">OHMS</div>
            <small class="text-white-50" style="font-size:0.7rem;">Doctor Portal</small></div>
        </div>
        <ul class="nav flex-column px-2 flex-grow-1">
            <li class="nav-item">
                <a class="nav-link sidebar-link" href="${pageContext.request.contextPath}/doctor/dashboard">
                    <i class="bi bi-speedometer2"></i> Dashboard</a>
            </li>
            <li class="nav-item">
                <a class="nav-link sidebar-link" href="${pageContext.request.contextPath}/doctor/appointments">
                    <i class="bi bi-calendar3"></i> Appointments</a>
            </li>
            <li class="nav-item">
                <a class="nav-link sidebar-link active" href="${pageContext.request.contextPath}/doctor/profile">
                    <i class="bi bi-person-circle"></i> My Profile</a>
            </li>
        </ul>
        <div class="px-2 pb-3">
            <hr class="border-secondary">
            <a class="nav-link sidebar-link text-danger" href="${pageContext.request.contextPath}/logout">
                <i class="bi bi-box-arrow-right"></i> Logout</a>
        </div>
    </nav>

    <div class="main-content flex-grow-1">
        <nav class="navbar navbar-light bg-white border-bottom px-4 shadow-sm">
            <button class="btn btn-sm btn-outline-secondary me-2" id="sidebarToggle">
                <i class="bi bi-list"></i></button>
            <span class="navbar-brand mb-0 h5">
                <i class="bi bi-person-circle me-2 text-success"></i>My Profile</span>
        </nav>

        <div class="p-4">
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

            <div class="row g-4">

                <!-- Profile Picture Card -->
                <div class="col-md-4">
                    <div class="card border-0 shadow-sm text-center p-4">
                        <div class="position-relative d-inline-block mx-auto mb-3">
                            <c:choose>
                                <c:when test="${not empty doctor.user.profileImage}">
                                    <img src="${pageContext.request.contextPath}/${doctor.user.profileImage}"
                                         class="rounded-circle border border-3 border-success"
                                         style="width:120px;height:120px;object-fit:cover;"
                                         alt="Profile Photo">
                                </c:when>
                                <c:otherwise>
                                    <div class="rounded-circle bg-success-subtle text-success d-flex align-items-center justify-content-center mx-auto"
                                         style="width:120px;height:120px;font-size:3rem;">
                                        <i class="bi bi-person-circle"></i>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <h5 class="fw-bold">Dr. ${doctor.fullName}</h5>
                        <div class="text-success fw-semibold">${doctor.specialization}</div>
                        <div class="text-muted small">${doctor.department.name}</div>
                        <div class="mt-2">
                            <span class="badge bg-success px-3 py-2">${doctor.status.displayName}</span>
                        </div>
                        <hr>
                        <div class="text-start small">
                            <div class="mb-1"><i class="bi bi-envelope me-2 text-muted"></i>${doctor.user.email}</div>
                            <div class="mb-1"><i class="bi bi-telephone me-2 text-muted"></i>${doctor.user.phone}</div>
                            <div class="mb-1"><i class="bi bi-mortarboard me-2 text-muted"></i>${doctor.qualification}</div>
                            <div><i class="bi bi-clock-history me-2 text-muted"></i>${doctor.experienceYears} years experience</div>
                        </div>
                    </div>
                </div>

                <!-- Edit Profile Form -->
                <div class="col-md-8">
                    <div class="card border-0 shadow-sm">
                        <div class="card-header bg-white fw-semibold">
                            <i class="bi bi-pencil me-2 text-success"></i>Edit Profile
                        </div>
                        <div class="card-body p-4">
                            <form action="${pageContext.request.contextPath}/doctor/profile"
                                  method="post" enctype="multipart/form-data" novalidate>

                                <h6 class="fw-bold text-success mb-3">Personal Information</h6>
                                <div class="row g-3 mb-4">
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Full Name</label>
                                        <input type="text" name="fullName" class="form-control"
                                               value="${doctor.fullName}" required>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Phone</label>
                                        <input type="tel" name="phone" class="form-control"
                                               value="${doctor.user.phone}" pattern="[6-9][0-9]{9}" maxlength="10">
                                    </div>
                                    <div class="col-md-12">
                                        <label class="form-label fw-semibold">Profile Photo</label>
                                        <input type="file" name="profileImage" class="form-control"
                                               accept="image/jpeg,image/png,image/jpg">
                                        <div class="form-text">Max 2MB. JPG or PNG only.</div>
                                    </div>
                                </div>

                                <h6 class="fw-bold text-success mb-3">Professional Information</h6>
                                <div class="row g-3 mb-4">
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Qualification</label>
                                        <input type="text" name="qualification" class="form-control"
                                               value="${doctor.qualification}">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Consultation Fee (₹)</label>
                                        <input type="number" name="consultationFee" class="form-control"
                                               value="${doctor.consultationFee}" min="0" step="0.01">
                                    </div>
                                    <div class="col-md-12">
                                        <label class="form-label fw-semibold">Available Days</label>
                                        <input type="text" name="availableDays" class="form-control"
                                               value="${doctor.availableDays}"
                                               placeholder="e.g. MON, WED, FRI">
                                    </div>
                                    <div class="col-md-12">
                                        <label class="form-label fw-semibold">Bio / About</label>
                                        <textarea name="bio" class="form-control" rows="4"
                                                  placeholder="Write something about yourself...">${doctor.bio}</textarea>
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-success btn-lg px-5">
                                    <i class="bi bi-save me-2"></i>Save Changes
                                </button>
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
</body>
</html>
