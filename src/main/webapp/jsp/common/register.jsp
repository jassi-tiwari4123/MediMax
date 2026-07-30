<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register — OHMS</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-bg">

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-7">
            <div class="card shadow-lg">

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

                    <!-- Role Tabs -->
                    <ul class="nav nav-pills nav-fill mb-4" id="roleTab" role="tablist">
                        <li class="nav-item">
                            <button class="nav-link active" id="patient-tab"
                                    data-bs-toggle="pill" data-bs-target="#patientPane"
                                    type="button">
                                <i class="bi bi-person-heart me-1"></i>Register as Patient
                            </button>
                        </li>
                        <li class="nav-item">
                            <button class="nav-link" id="doctor-tab"
                                    data-bs-toggle="pill" data-bs-target="#doctorPane"
                                    type="button">
                                <i class="bi bi-clipboard2-pulse me-1"></i>Register as Doctor
                            </button>
                        </li>
                    </ul>

                    <div class="tab-content">

                        <!-- ── Patient Form ── -->
                        <div class="tab-pane fade show active" id="patientPane">
                            <form action="${pageContext.request.contextPath}/register"
                                  method="post" novalidate id="patientForm">
                                <input type="hidden" name="role" value="PATIENT">

                                <div class="row g-3">
                                    <div class="col-12">
                                        <label class="form-label fw-semibold">Full Name</label>
                                        <input type="text" name="fullName" class="form-control"
                                               placeholder="Your full name" required minlength="2" maxlength="100">
                                        <div class="invalid-feedback">Full name is required.</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Email Address</label>
                                        <input type="email" name="email" id="patientEmail"
                                               class="form-control" placeholder="name@gmail.com" required>
                                        <div class="invalid-feedback" id="patientEmailError">
                                            Enter a valid Gmail address (e.g. name@gmail.com)
                                        </div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Phone Number</label>
                                        <input type="tel" name="phone" class="form-control"
                                               placeholder="10-digit mobile number"
                                               required pattern="[6-9][0-9]{9}" maxlength="10">
                                        <div class="invalid-feedback">Enter a valid 10-digit Indian mobile number.</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Gender</label>
                                        <select name="gender" class="form-select" required>
                                            <option value="">Select Gender</option>
                                            <option value="MALE">Male</option>
                                            <option value="FEMALE">Female</option>
                                            <option value="OTHER">Other</option>
                                        </select>
                                        <div class="invalid-feedback">Please select gender.</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Date of Birth</label>
                                        <input type="date" name="dateOfBirth" class="form-control" id="patientDob">
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Password</label>
                                        <div class="input-group">
                                            <input type="password" name="password" id="patientPwd"
                                                   class="form-control"
                                                   placeholder="Min 8 chars" required minlength="8">
                                            <button type="button" class="btn btn-outline-secondary toggle-pwd"
                                                    data-target="patientPwd">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                        </div>
                                        <div class="progress mt-1" style="height:5px;">
                                            <div class="progress-bar" id="patientPwdBar" style="width:0%"></div>
                                        </div>
                                        <small id="patientPwdHint" class="form-text"></small>
                                        <div class="invalid-feedback">
                                            Must be 8+ chars with uppercase, lowercase, number and special character (@$!%*?&).
                                        </div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Confirm Password</label>
                                        <div class="input-group">
                                            <input type="password" name="confirmPassword" id="patientConfirm"
                                                   class="form-control" placeholder="Repeat password" required>
                                            <button type="button" class="btn btn-outline-secondary toggle-pwd"
                                                    data-target="patientConfirm">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                        </div>
                                        <div class="invalid-feedback" id="patientConfirmError">
                                            Passwords do not match.
                                        </div>
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-primary btn-lg w-100 mt-4"
                                        id="patientSubmit">
                                    <i class="bi bi-person-check me-2"></i>Register as Patient
                                </button>
                            </form>
                        </div>

                        <!-- ── Doctor Form ── -->
                        <div class="tab-pane fade" id="doctorPane">
                            <form action="${pageContext.request.contextPath}/register"
                                  method="post" novalidate id="doctorForm">
                                <input type="hidden" name="role" value="DOCTOR">

                                <div class="row g-3">
                                    <div class="col-12">
                                        <label class="form-label fw-semibold">Full Name</label>
                                        <input type="text" name="fullName" class="form-control"
                                               placeholder="Dr. Full Name" required minlength="2" maxlength="100">
                                        <div class="invalid-feedback">Full name is required.</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Email Address</label>
                                        <input type="email" name="email" id="doctorEmail"
                                               class="form-control" placeholder="name@gmail.com" required>
                                        <div class="invalid-feedback" id="doctorEmailError">
                                            Enter a valid Gmail address (e.g. name@gmail.com)
                                        </div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Phone Number</label>
                                        <input type="tel" name="phone" class="form-control"
                                               placeholder="10-digit mobile number"
                                               required pattern="[6-9][0-9]{9}" maxlength="10">
                                        <div class="invalid-feedback">Enter a valid 10-digit Indian mobile number.</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Gender</label>
                                        <select name="gender" class="form-select" required>
                                            <option value="">Select Gender</option>
                                            <option value="MALE">Male</option>
                                            <option value="FEMALE">Female</option>
                                            <option value="OTHER">Other</option>
                                        </select>
                                        <div class="invalid-feedback">Please select gender.</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Date of Birth</label>
                                        <input type="date" name="dateOfBirth" class="form-control">
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Password</label>
                                        <div class="input-group">
                                            <input type="password" name="password" id="doctorPwd"
                                                   class="form-control"
                                                   placeholder="Min 8 chars" required minlength="8">
                                            <button type="button" class="btn btn-outline-secondary toggle-pwd"
                                                    data-target="doctorPwd">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                        </div>
                                        <div class="progress mt-1" style="height:5px;">
                                            <div class="progress-bar" id="doctorPwdBar" style="width:0%"></div>
                                        </div>
                                        <small id="doctorPwdHint" class="form-text"></small>
                                        <div class="invalid-feedback">
                                            Must be 8+ chars with uppercase, lowercase, number and special character.
                                        </div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Confirm Password</label>
                                        <div class="input-group">
                                            <input type="password" name="confirmPassword" id="doctorConfirm"
                                                   class="form-control" placeholder="Repeat password" required>
                                            <button type="button" class="btn btn-outline-secondary toggle-pwd"
                                                    data-target="doctorConfirm">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                        </div>
                                        <div class="invalid-feedback" id="doctorConfirmError">
                                            Passwords do not match.
                                        </div>
                                    </div>

                                    <!-- Professional Details -->
                                    <div class="col-12 mt-2">
                                        <hr>
                                        <h6 class="fw-bold text-primary">
                                            <i class="bi bi-stethoscope me-1"></i>Professional Details
                                        </h6>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Department</label>
                                        <select name="departmentId" id="deptSelect"
                                                class="form-select" required>
                                            <option value="">Select Department</option>
                                            <c:forEach var="dept" items="${departments}">
                                                <option value="${dept.id}">${dept.name}</option>
                                            </c:forEach>
                                        </select>
                                        <div class="invalid-feedback">Please select a department.</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Specialization</label>
                                        <div id="specDropdownDiv">
                                            <select id="specSelect" class="form-select">
                                                <option value="">— Select Department First —</option>
                                            </select>
                                        </div>
                                        <div id="specTextDiv" style="display:none;">
                                            <input type="text" id="specText"
                                                   class="form-control"
                                                   placeholder="Type your specialization...">
                                        </div>
                                        <%-- Single hidden field that always holds the final value --%>
                                        <input type="hidden" name="specialization" id="specFinal">
                                        <div class="text-danger small mt-1" id="specError" style="display:none;">
                                            Please select or enter a specialization.
                                        </div>
                                    </div>

                                    <div class="col-12">
                                        <label class="form-label fw-semibold">Qualification</label>
                                        <input type="text" name="qualification" class="form-control"
                                               placeholder="e.g. MBBS, MD (Cardiology)" required>
                                        <div class="invalid-feedback">Required.</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Experience (years)</label>
                                        <input type="number" name="experienceYears" class="form-control"
                                               min="0" max="60" placeholder="5" required>
                                        <div class="invalid-feedback">Required.</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Consultation Fee (₹)</label>
                                        <input type="number" name="consultationFee" class="form-control"
                                               min="0" step="0.01" placeholder="500" required>
                                        <div class="invalid-feedback">Required.</div>
                                    </div>
                                </div>

                                <div class="alert alert-info mt-3 small">
                                    <i class="bi bi-info-circle me-1"></i>
                                    Doctor registrations require admin approval before you can receive appointments.
                                </div>

                                <button type="submit" class="btn btn-success btn-lg w-100"
                                        id="doctorSubmit">
                                    <i class="bi bi-clipboard2-check me-2"></i>Register as Doctor
                                </button>
                            </form>
                        </div>

                    </div><!-- /tab-content -->

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
<script>

// ── Specialization — load from DB via AJAX ────────────────────────────────────
document.getElementById('deptSelect').addEventListener('change', function () {
    const specDropDiv = document.getElementById('specDropdownDiv');
    const specTextDiv = document.getElementById('specTextDiv');
    const specSelect  = document.getElementById('specSelect');
    const specText    = document.getElementById('specText');
    const specFinal   = document.getElementById('specFinal');
    const deptId      = this.value;

    // Reset
    specFinal.value  = '';
    specText.value   = '';
    specSelect.innerHTML = '<option value="">Loading...</option>';

    if (!deptId) {
        specSelect.innerHTML = '<option value="">— Select Department First —</option>';
        specDropDiv.style.display = '';
        specTextDiv.style.display = 'none';
        return;
    }

    // Fetch specializations from server via AJAX
    fetch('${pageContext.request.contextPath}/get-specializations?departmentId=' + deptId)
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (data && data.length > 0) {
                // Has specializations — show dropdown
                specDropDiv.style.display = '';
                specTextDiv.style.display = 'none';
                specSelect.innerHTML = '<option value="">— Select Specialization —</option>';
                data.forEach(function(spec) {
                    const opt = document.createElement('option');
                    opt.value       = spec.name;
                    opt.textContent = spec.name;
                    specSelect.appendChild(opt);
                });
            } else {
                // No specializations — show text input
                specDropDiv.style.display = 'none';
                specTextDiv.style.display = '';
                specText.placeholder = 'Type your specialization...';
                specText.focus();
            }
        })
        .catch(function() {
            specDropDiv.style.display = 'none';
            specTextDiv.style.display = '';
        });
});

// Sync dropdown selection → hidden field
document.getElementById('specSelect').addEventListener('change', function () {
    document.getElementById('specFinal').value = this.value;
});

// Sync text input → hidden field
document.getElementById('specText').addEventListener('input', function () {
    document.getElementById('specFinal').value = this.value;
});

// Validate specialization before form submit
document.getElementById('doctorForm').addEventListener('submit', function (e) {
    const specFinal   = document.getElementById('specFinal');
    const specSelect  = document.getElementById('specSelect');
    const specText    = document.getElementById('specText');
    const specError   = document.getElementById('specError');
    const specTextDiv = document.getElementById('specTextDiv');

    // Sync value from whichever field is visible
    if (specTextDiv.style.display === 'none') {
        // Dropdown is visible
        specFinal.value = specSelect.value;
    } else {
        // Text input is visible
        specFinal.value = specText.value.trim();
    }

    if (!specFinal.value) {
        e.preventDefault();
        e.stopPropagation();
        specError.style.display = '';
        specError.textContent   = 'Please select or enter a specialization.';
        this.classList.add('was-validated');
        return;
    }
    specError.style.display = 'none';
});

// ── Password strength checker ─────────────────────────────────────────────────
function checkPasswordStrength(password) {
    let score = 0;
    if (password.length >= 8)                    score++;
    if (/[A-Z]/.test(password))                  score++;
    if (/[a-z]/.test(password))                  score++;
    if (/[0-9]/.test(password))                  score++;
    if (/[@$!%*?&]/.test(password))              score++;
    return score;
}

function updateStrengthBar(inputId, barId, hintId) {
    const input = document.getElementById(inputId);
    const bar   = document.getElementById(barId);
    const hint  = document.getElementById(hintId);

    input.addEventListener('input', function () {
        const score = checkPasswordStrength(this.value);
        const pct   = (score / 5) * 100;
        const colors = ['', 'bg-danger', 'bg-warning', 'bg-info', 'bg-primary', 'bg-success'];
        const labels = ['', 'Very Weak', 'Weak', 'Fair', 'Strong', 'Very Strong'];

        bar.style.width  = pct + '%';
        bar.className    = 'progress-bar ' + (colors[score] || '');
        hint.textContent = score > 0 ? 'Strength: ' + labels[score] : '';
        hint.className   = 'form-text ' + (score >= 4 ? 'text-success' : 'text-warning');

        // Mark invalid if not strong enough
        if (this.value.length > 0 && score < 4) {
            this.setCustomValidity('Password is too weak. Add uppercase, number and special character.');
        } else {
            this.setCustomValidity('');
        }
    });
}

updateStrengthBar('patientPwd', 'patientPwdBar', 'patientPwdHint');
updateStrengthBar('doctorPwd',  'doctorPwdBar',  'doctorPwdHint');

// ── Confirm password match ────────────────────────────────────────────────────
function setupConfirmCheck(pwdId, confirmId, errorId) {
    const confirm = document.getElementById(confirmId);
    const pwd     = document.getElementById(pwdId);

    confirm.addEventListener('input', function () {
        if (this.value !== pwd.value) {
            this.setCustomValidity('Passwords do not match.');
            document.getElementById(errorId).textContent = 'Passwords do not match.';
        } else {
            this.setCustomValidity('');
            document.getElementById(errorId).textContent = '';
        }
    });
    // Also re-check when main password changes
    pwd.addEventListener('input', function () {
        if (confirm.value && confirm.value !== this.value) {
            confirm.setCustomValidity('Passwords do not match.');
        } else if (confirm.value) {
            confirm.setCustomValidity('');
        }
    });
}

setupConfirmCheck('patientPwd', 'patientConfirm', 'patientConfirmError');
setupConfirmCheck('doctorPwd',  'doctorConfirm',  'doctorConfirmError');

// ── Gmail-only email validation ───────────────────────────────────────────────
function setupGmailCheck(inputId, errorId) {
    document.getElementById(inputId).addEventListener('input', function () {
        const val = this.value.trim().toLowerCase();
        if (val.length > 0 && !val.endsWith('@gmail.com')) {
            this.setCustomValidity('Email must be a Gmail address (ending with @gmail.com)');
            document.getElementById(errorId).textContent =
                'Email must end with @gmail.com';
        } else {
            this.setCustomValidity('');
            document.getElementById(errorId).textContent =
                'Enter a valid Gmail address (e.g. name@gmail.com)';
        }
    });
}

setupGmailCheck('patientEmail', 'patientEmailError');
setupGmailCheck('doctorEmail',  'doctorEmailError');

// ── Phone — digits only ───────────────────────────────────────────────────────
document.querySelectorAll('input[name="phone"]').forEach(function (input) {
    input.addEventListener('input', function () {
        this.value = this.value.replace(/\D/g, '').slice(0, 10);
    });
});

// ── Set max date of birth to today ────────────────────────────────────────────
const today = new Date().toISOString().split('T')[0];
document.querySelectorAll('input[name="dateOfBirth"]').forEach(function (el) {
    el.setAttribute('max', today);
});

// ── Form submit validation ────────────────────────────────────────────────────
document.querySelectorAll('form').forEach(function (form) {
    form.addEventListener('submit', function (e) {
        if (!form.checkValidity()) {
            e.preventDefault();
            e.stopPropagation();
        }
        form.classList.add('was-validated');
    });
});
// ── Eye toggle for password fields ───────────────────────────────────────────
document.querySelectorAll('.toggle-pwd').forEach(function (btn) {
    btn.addEventListener('click', function () {
        const targetId = this.getAttribute('data-target');
        const input    = document.getElementById(targetId);
        const icon     = this.querySelector('i');
        if (input.type === 'password') {
            input.type     = 'text';
            icon.className = 'bi bi-eye-slash';
        } else {
            input.type     = 'password';
            icon.className = 'bi bi-eye';
        }
    });
});

</script>
</body>
</html>
