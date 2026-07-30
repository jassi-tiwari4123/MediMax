<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 Not Found — OHMS</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-bg">
<div class="container error-page">
    <div class="text-white text-center">
        <div class="error-code text-white">404</div>
        <i class="bi bi-compass fs-1 mb-3 d-block opacity-75"></i>
        <h2 class="fw-bold">Page Not Found</h2>
        <p class="opacity-75 mb-4">The page you're looking for doesn't exist or has been moved.</p>
        <a href="${pageContext.request.contextPath}/login"
           class="btn btn-light btn-lg px-5">
            <i class="bi bi-house me-2"></i>Go Home
        </a>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
