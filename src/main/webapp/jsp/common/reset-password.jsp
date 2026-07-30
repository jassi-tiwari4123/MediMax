<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password — OHMS</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-bg">

<div class="container d-flex align-items-center justify-content-center min-vh-100">
    <div class="card shadow-lg auth-card">

        <div class="card-header ohms-header text-center py-4">
            <i class="bi bi-shield-lock fs-1 text-white"></i>
            <h4 class="text-white mb-0 mt-2">Reset Password</h4>
        </div>

        <div class="card-body p-4">

            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show">
                    <i class="bi bi-exclamation-triangle me-2"></i>${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="alert alert-success"><i class="bi bi-check-circle me-2"></i>${success}</div>
            </c:if>

            <p class="text-muted text-center mb-4">
                Enter the OTP sent to your email and choose a new password.
            </p>

            <form action="${pageContext.request.contextPath}/reset-password"
                  method="post" novalidate id="resetForm">

                <input type="hidden" name="email" value="${email}">

                <div class="mb-3">
                    <label class="form-label fw-semibold">Email Address</label>
                    <input type="email" name="emailDisplay" class="form-control"
                           value="${email}" readonly>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">
                        <i class="bi bi-123 me-1"></i>OTP Code
                    </label>
                    <input type="text" name="otp" class="form-control form-control-lg text-center"
                           placeholder="6-digit OTP" maxlength="6" pattern="[0-9]{6}"
                           required style="letter-spacing:8px;font-size:1.4rem;">
                    <div class="invalid-feedback">Enter the 6-digit OTP from your email.</div>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">New Password</label>
                    <input type="password" name="newPassword" id="newPwd" class="form-control"
                           placeholder="Min 8 chars, uppercase + number + special" required minlength="8">
                    <div class="invalid-feedback">Password must be at least 8 characters.</div>
                </div>

                <div class="mb-4">
                    <label class="form-label fw-semibold">Confirm New Password</label>
                    <input type="password" name="confirmPassword" id="confirmPwd" class="form-control"
                           placeholder="Repeat password" required>
                    <div class="invalid-feedback">Passwords must match.</div>
                </div>

                <button type="submit" class="btn btn-success btn-lg w-100">
                    <i class="bi bi-check2-circle me-2"></i>Reset Password
                </button>
            </form>

            <p class="text-center mt-3 mb-0">
                <a href="${pageContext.request.contextPath}/login" class="text-decoration-none">
                    <i class="bi bi-arrow-left me-1"></i>Back to Login
                </a>
            </p>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.getElementById('resetForm').addEventListener('submit', function(e) {
        const np = document.getElementById('newPwd').value;
        const cp = document.getElementById('confirmPwd').value;
        if (np !== cp) {
            document.getElementById('confirmPwd').setCustomValidity('Passwords do not match.');
        } else {
            document.getElementById('confirmPwd').setCustomValidity('');
        }
        if (!this.checkValidity()) { e.preventDefault(); e.stopPropagation(); }
        this.classList.add('was-validated');
    });
</script>
</body>
</html>
