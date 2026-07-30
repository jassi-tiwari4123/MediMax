<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register — OHMS</title>
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-bg">

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-7">
            <div class="card shadow-lg">

                <!-- Header -->
                <div class="card-header ohms-header text-center py-3">
                    <i class="bi bi-person-plus fs-2 text-white"></i>
                    <h4 class="text-white mb-0 mt-1">Create Account</h4>
                </div>

                <div class="card-body p-4">

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show">
                            <i class="bi bi-exclamation-triangle me-2"></i>${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <!-- Role selector tabs -->
                    <ul class="nav nav-pills nav-fill mb-4" id="roleTab" role="tablist">
                        <li class="nav-item" role="presentation">
                            <button class="nav-link active" id="patient-tab"
                                    data-bs-toggle="pill" data-bs-target="#patientPane"
                                    type="button" role="tab">
                                <i class="bi bi-person-heart me-1"></i>Register as Patient
                            </button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="doctor-tab"
                                    data-bs-toggle="pill" data-bs-target="#doctorPane"
                                    type="button" role="tab">
                                <i class="bi bi-clipboard2-pulse me-1"></i>Register as Doctor
                            </button>
                        </li>
                    </ul>

                    <div class="tab-content">

                        <!-- ── Patient Registration ── -->
                        <div class="tab-pane fade show active" id="patientPane" role="tabpanel">
                            <form action="${pageContext.request.contextPath}/register"
                                  method="post" novalidate id="patientForm">
                                <input type="hidden" name="role" value="PATIENT">
                                <%@ include file="register-common-fields.jspf" %>
                                <button type="submit" class="btn btn-primary btn-lg w-100 mt-3">
                                    <i class="bi bi-person-check me-2"></i>Register as Patient
                                </button>
                            </form>
                        </div>

                        <!-- ── Doctor Registration ── -->
                        <div class="tab-pane fade" id="doctorPane" role="tabpanel">
                            <form action="${pageContext.request.contextPath}/register"
                                  method="post" novalidate id="doctorForm">
                                <input type="hidden" name="role" value="DOCTOR">
                                <%@ include file="register-common-fields.jspf" %>

                                <hr class="my-3">
                                <h6 class="fw-bold mb-3 text-primary">
                                    <i class="bi bi-stethoscope me-1"></i>Professional Details
                                </h6>

                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Department</label>
                                        <select name="departmentId" class="form-select" required>
                                            <option value="">Select Department</option>
                                            <option value="1">Cardiology</option>
                                            <option value="2">Neurology</option>
                                            <option value="3">Orthopedics</option>
                                            <option value="4">Pediatrics</option>
                                            <option value="5">Dermatology</option>
                                            <option value="6">General Medicine</option>
                                            <option value="7">ENT</option>
                                            <option value="8">Ophthalmology</option>
                                            <option value="9">Gynecology</option>
                                            <option value="10">Psychiatry</option>
                                        </select>
                                        <div class="invalid-feedback">Select a department.</div>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Specialization</label>
                                        <input type="text" name="specialization"
                                               class="form-control" placeholder="e.g. Interventional Cardiologist"
                                               required>
                                        <div class="invalid-feedback">Required.</div>
                                    </div>
                                    <div class="col-md-12">
                                        <label class="form-label fw-semibold">Qualification</label>
                                        <input type="text" name="qualification"
                                               class="form-control" placeholder="e.g. MBBS, MD (Cardiology)"
                                               required>
                                        <div class="invalid-feedback">Required.</div>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Experience (years)</label>
                                        <input type="number" name="experienceYears"
                                               class="form-control" min="0" max="60"
                                               placeholder="5" required>
                                        <div class="invalid-feedback">Required.</div>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Consultation Fee (₹)</label>
                                        <input type="number" name="consultationFee"
                                               class="form-control" min="0" step="0.01"
                                               placeholder="500.00" required>
                                        <div class="invalid-feedback">Required.</div>
                                    </div>
                                </div>

                                <div class="alert alert-info mt-3 mb-0 small">
                                    <i class="bi bi-info-circle me-1"></i>
                                    Doctor registrations require admin approval before you can receive appointments.
                                </div>

                                <button type="submit" class="btn btn-success btn-lg w-100 mt-3">
                                    <i class="bi bi-clipboard2-check me-2"></i>Register as Doctor
                                </button>
                            </form>
                        </div>

                    </div><!-- tab-content -->

                    <hr class="mt-4">
                    <p class="text-center mb-0">
                        Already have an account?
                        <a href="${pageContext.request.contextPath}/login"
                           class="fw-semibold text-decoration-none">Sign In</a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/validation.js"></script>
<script>
    // Bootstrap validation for all forms
    document.querySelectorAll('form').forEach(function(form) {
        form.addEventListener('submit', function(e) {
            if (!form.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });
</script>
</body>
</html>
