<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password — OHMS</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-bg">

<div class="container d-flex align-items-center justify-content-center min-vh-100">
    <div class="card shadow-lg auth-card">

        <div class="card-header ohms-header text-center py-4">
            <i class="bi bi-key fs-1 text-white"></i>
            <h4 class="text-white mb-0 mt-2">Forgot Password</h4>
        </div>

        <div class="card-body p-4">
            <p class="text-muted text-center mb-4">
                Enter your registered email address and we'll send you an OTP.
            </p>

            <c:if test="${not empty error}">
                <div class="alert alert-danger"><i class="bi bi-exclamation-triangle me-2"></i>${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/forgot-password"
                  method="post" novalidate id="fpForm">
                <div class="mb-3">
                    <label class="form-label fw-semibold">
                        <i class="bi bi-envelope me-1"></i>Email Address
                    </label>
                    <input type="email" name="email" class="form-control form-control-lg"
                           placeholder="yourname@email.com" required autocomplete="email">
                    <div class="invalid-feedback">Enter a valid email.</div>
                </div>
                <button type="submit" class="btn btn-primary btn-lg w-100">
                    <i class="bi bi-send me-2"></i>Send OTP
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
    document.getElementById('fpForm').addEventListener('submit', function(e) {
        if (!this.checkValidity()) { e.preventDefault(); e.stopPropagation(); }
        this.classList.add('was-validated');
    });
</script>
</body>
</html>
