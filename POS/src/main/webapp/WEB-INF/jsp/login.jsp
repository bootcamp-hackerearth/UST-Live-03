<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            height: 100vh;
            margin: 0;
            background-color: #f1f3f6;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", sans-serif;
        }

        .login-card {
            width: 380px;
            border-radius: 12px;
            border: none;
        }

        .login-header {
            background-color: #1e272e;
            color: #fff;
            border-top-left-radius: 12px;
            border-top-right-radius: 12px;
            text-align: center;
            padding: 16px;
        }

        .form-label {
            font-size: 0.9rem;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="card login-card shadow-sm">

    <div class="login-header">
        <h5 class="mb-0">POS System Login</h5>
    </div>

    <div class="card-body p-4">

        <!-- Login Error -->
        <c:if test="${param.error == 'true'}">
            <div class="alert alert-danger text-center small">
                Invalid username or password
            </div>
        </c:if>

        <!-- Optional message -->
        <c:if test="${not empty message}">
            <div class="alert alert-info text-center small">
                ${message}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">

            <div class="mb-3">
                <label class="form-label">Email</label>
                <input type="text"
                       name="username"
                       class="form-control"
                       placeholder="Enter your email"
                       required>
            </div>

            <div class="mb-3">
                <label class="form-label">Password</label>
                <input type="password"
                       name="password"
                       class="form-control"
                       placeholder="Enter your password"
                       required>
            </div>

            <div class="d-grid mt-4">
                <button type="submit" class="btn btn-primary">
                    Sign In
                </button>
            </div>
        </form>

    </div>

    <div class="card-footer bg-light text-center small">
        Don’t have an account?
        <a href="${pageContext.request.contextPath}/register"
           class="text-decoration-none">
            Register here
        </a>
    </div>

</div>

</body>
</html>