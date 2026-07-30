<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login — OHMS</title>
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-bg">

<div class="container d-flex align-items-center justify-content-center min-vh-100">
    <div class="card shadow-lg auth-card">

        <!-- Header -->
        <div class="card-header text-center ohms-header py-4">
            <i class="bi bi-hospital fs-1 text-white"></i>
            <h4 class="text-white mb-0 mt-2">OHMS Healthcare</h4>
            <small class="text-white-50">Online Healthcare Management System</small>
        </div>

        <div class="card-body p-4">
            <h5 class="text-center mb-4 fw-semibold">Sign In to Your Account</h5>

            <!-- Flash messages from session -->
            <c:if test="${not empty sessionScope.flashSuccess}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle me-2"></i>${sessionScope.flashSuccess}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <c:remove var="flashSuccess" scope="session"/>
            </c:if>

            <!-- Error from request attribute -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Success from request attribute (e.g., after registration) -->
            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle me-2"></i>${success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Session expired notice -->
            <c:if test="${param.sessionExpired eq 'true'}">
                <div class="alert alert-warning alert-dismissible fade show" role="alert">
                    <i class="bi bi-clock me-2"></i>Session expired. Please log in again.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login"
                  method="post" novalidate id="loginForm">

                <div class="mb-3">
                    <label for="email" class="form-label fw-semibold">
                        <i class="bi bi-envelope me-1"></i>Email Address
                    </label>
                    <input type="email" id="email" name="email"
                           class="form-control form-control-lg"
                           placeholder="doctor@hospital.com"
                           required autocomplete="email">
                    <div class="invalid-feedback">Please enter a valid email.</div>
                </div>

                <div class="mb-3">
                    <label for="password" class="form-label fw-semibold">
                        <i class="bi bi-lock me-1"></i>Password
                    </label>
                    <div class="input-group">
                        <input type="password" id="password" name="password"
                               class="form-control form-control-lg"
                               placeholder="Your password"
                               required autocomplete="current-password">
                        <button type="button"
                                class="btn btn-outline-secondary"
                                id="togglePassword"
                                title="Show/hide password">
                            <i class="bi bi-eye" id="eyeIcon"></i>
                        </button>
                    </div>
                    <div class="invalid-feedback">Password is required.</div>
                </div>

                <div class="d-flex justify-content-between align-items-center mb-3">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="remember">
                        <label class="form-check-label" for="remember">Remember me</label>
                    </div>
                    <a href="${pageContext.request.contextPath}/forgot-password"
                       class="text-decoration-none small">Forgot password?</a>
                </div>

                <button type="submit" class="btn btn-primary btn-lg w-100" id="loginBtn">
                    <i class="bi bi-box-arrow-in-right me-2"></i>Sign In
                </button>
            </form>

            <hr class="my-4">

            <p class="text-center mb-0">
                New patient?
                <a href="${pageContext.request.contextPath}/register"
                   class="text-decoration-none fw-semibold">Create Account</a>
            </p>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Toggle password visibility
    document.getElementById('togglePassword').addEventListener('click', function () {
        const pwd  = document.getElementById('password');
        const icon = document.getElementById('eyeIcon');
        if (pwd.type === 'password') {
            pwd.type = 'text';
            icon.className = 'bi bi-eye-slash';
        } else {
            pwd.type = 'password';
            icon.className = 'bi bi-eye';
        }
    });

    // Bootstrap form validation
    document.getElementById('loginForm').addEventListener('submit', function (e) {
        if (!this.checkValidity()) {
            e.preventDefault();
            e.stopPropagation();
        }
        this.classList.add('was-validated');
        // Show spinner on submit
        document.getElementById('loginBtn').innerHTML =
            '<span class="spinner-border spinner-border-sm me-2"></span>Signing in...';
    });
</script>
</body>
</html>
