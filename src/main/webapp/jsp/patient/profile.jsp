<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile — Patient</title>
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
            <small class="text-white-50" style="font-size:0.7rem;">Patient Portal</small></div>
        </div>
        <ul class="nav flex-column px-2 flex-grow-1">
            <li class="nav-item">
                <a class="nav-link sidebar-link" href="${pageContext.request.contextPath}/patient/dashboard">
                    <i class="bi bi-speedometer2"></i> Dashboard</a>
            </li>
            <li class="nav-item">
                <a class="nav-link sidebar-link" href="${pageContext.request.contextPath}/patient/search-doctors">
                    <i class="bi bi-search-heart"></i> Find Doctors</a>
            </li>
            <li class="nav-item">
                <a class="nav-link sidebar-link" href="${pageContext.request.contextPath}/patient/book-appointment">
                    <i class="bi bi-calendar-plus"></i> Book Appointment</a>
            </li>
            <li class="nav-item">
                <a class="nav-link sidebar-link active" href="${pageContext.request.contextPath}/patient/profile">
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
                <i class="bi bi-person-circle me-2 text-info"></i>My Profile</span>
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
                                <c:when test="${not empty patient.user.profileImage}">
                                    <img src="${pageContext.request.contextPath}/${patient.user.profileImage}"
                                         class="rounded-circle border border-3 border-info"
                                         style="width:120px;height:120px;object-fit:cover;"
                                         alt="Profile Photo">
                                </c:when>
                                <c:otherwise>
                                    <div class="rounded-circle bg-info-subtle text-info d-flex align-items-center justify-content-center mx-auto"
                                         style="width:120px;height:120px;font-size:3rem;">
                                        <i class="bi bi-person-circle"></i>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <h5 class="fw-bold">${patient.fullName}</h5>
                        <div class="text-muted small">${patient.user.email}</div>
                        <c:if test="${not empty patient.bloodGroup}">
                            <span class="badge bg-danger mt-2 px-3">${patient.bloodGroup}</span>
                        </c:if>
                        <hr>
                        <div class="text-start small">
                            <div class="mb-1"><i class="bi bi-telephone me-2 text-muted"></i>${patient.user.phone}</div>
                            <c:if test="${not empty patient.user.dateOfBirth}">
                                <div class="mb-1"><i class="bi bi-calendar me-2 text-muted"></i>${patient.user.dateOfBirth}</div>
                            </c:if>
                            <c:if test="${not empty patient.address}">
                                <div><i class="bi bi-geo-alt me-2 text-muted"></i>${patient.address}</div>
                            </c:if>
                        </div>
                    </div>
                </div>

                <!-- Edit Profile Form -->
                <div class="col-md-8">
                    <div class="card border-0 shadow-sm">
                        <div class="card-header bg-white fw-semibold">
                            <i class="bi bi-pencil me-2 text-info"></i>Edit Profile
                        </div>
                        <div class="card-body p-4">
                            <form action="${pageContext.request.contextPath}/patient/profile"
                                  method="post" enctype="multipart/form-data" novalidate>

                                <h6 class="fw-bold text-info mb-3">Personal Information</h6>
                                <div class="row g-3 mb-4">
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Full Name</label>
                                        <input type="text" name="fullName" class="form-control"
                                               value="${patient.fullName}" required>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Phone</label>
                                        <input type="tel" name="phone" class="form-control"
                                               value="${patient.user.phone}" pattern="[6-9][0-9]{9}" maxlength="10">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Date of Birth</label>
                                        <input type="date" name="dateOfBirth" class="form-control"
                                               value="${patient.user.dateOfBirth}">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Blood Group</label>
                                        <select name="bloodGroup" class="form-select">
                                            <option value="">Select Blood Group</option>
                                            <option value="A+"  ${patient.bloodGroup eq 'A+'  ? 'selected' : ''}>A+</option>
                                            <option value="A-"  ${patient.bloodGroup eq 'A-'  ? 'selected' : ''}>A-</option>
                                            <option value="B+"  ${patient.bloodGroup eq 'B+'  ? 'selected' : ''}>B+</option>
                                            <option value="B-"  ${patient.bloodGroup eq 'B-'  ? 'selected' : ''}>B-</option>
                                            <option value="O+"  ${patient.bloodGroup eq 'O+'  ? 'selected' : ''}>O+</option>
                                            <option value="O-"  ${patient.bloodGroup eq 'O-'  ? 'selected' : ''}>O-</option>
                                            <option value="AB+" ${patient.bloodGroup eq 'AB+' ? 'selected' : ''}>AB+</option>
                                            <option value="AB-" ${patient.bloodGroup eq 'AB-' ? 'selected' : ''}>AB-</option>
                                        </select>
                                    </div>
                                    <div class="col-md-12">
                                        <label class="form-label fw-semibold">Profile Photo</label>
                                        <input type="file" name="profileImage" class="form-control"
                                               accept="image/jpeg,image/png,image/jpg">
                                        <div class="form-text">Max 2MB. JPG or PNG only.</div>
                                    </div>
                                </div>

                                <h6 class="fw-bold text-info mb-3">Additional Information</h6>
                                <div class="row g-3 mb-4">
                                    <div class="col-md-12">
                                        <label class="form-label fw-semibold">Address</label>
                                        <textarea name="address" class="form-control" rows="2"
                                                  placeholder="Your address...">${patient.address}</textarea>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Emergency Contact Name</label>
                                        <input type="text" name="emergencyContactName" class="form-control"
                                               value="${patient.emergencyContactName}"
                                               placeholder="e.g. Parent / Spouse">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Emergency Contact Phone</label>
                                        <input type="tel" name="emergencyContactPhone" class="form-control"
                                               value="${patient.emergencyContactPhone}"
                                               pattern="[6-9][0-9]{9}" maxlength="10">
                                    </div>
                                    <div class="col-md-12">
                                        <label class="form-label fw-semibold">Medical History</label>
                                        <textarea name="medicalHistory" class="form-control" rows="3"
                                                  placeholder="Any known allergies, chronic conditions...">${patient.medicalHistory}</textarea>
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-info text-white btn-lg px-5">
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
