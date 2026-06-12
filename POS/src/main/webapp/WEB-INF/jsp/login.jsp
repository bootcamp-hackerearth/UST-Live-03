<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <!-- Bootstrap + Font -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #fdf6f0, #f3e7e9);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            width: 380px;
            padding: 35px;
            border-radius: 20px;
            background: rgba(255, 255, 255, 0.9);
            backdrop-filter: blur(10px);
            box-shadow: 0 20px 40px rgba(75, 46, 43, 0.2);
            border: none;
        }

        h3 {
            text-align: center;
            margin-bottom: 5px;
            font-weight: 600;
            color: #4B2E2B;
        }

        .subtitle {
            text-align: center;
            font-size: 14px;
            color: #777;
            margin-bottom: 25px;
        }

        .form-label {
            font-size: 13px;
            font-weight: 500;
            color: #4B2E2B;
            margin-bottom: 5px;
        }

        .form-control {
            border-radius: 10px;
            border: 1px solid #ddd;
            padding: 10px;
            transition: all 0.3s ease;
        }

        .form-control:focus {
            border-color: #4B2E2B;
            box-shadow: 0 0 0 2px rgba(75, 46, 43, 0.1);
        }

        .btn-primary {
            background: linear-gradient(135deg, #4B2E2B, #6d4c41);
            border: none;
            border-radius: 12px;
            font-weight: 600;
            padding: 12px;
            width: 100%;
            transition: 0.3s;
        }

        .btn-primary:hover {
            transform: scale(1.03);
            background: linear-gradient(135deg, #3a2421, #5c3d2e);
        }

        .register-text {
            text-align: center;
            margin-top: 18px;
            font-size: 14px;
            color: #555;
        }

        .register-btn {
            margin-top: 10px;
            background: #f5e6dc;
            color: #4B2E2B;
            border-radius: 12px;
            text-align: center;
            display: block;
            padding: 10px;
            text-decoration: none;
            font-weight: 500;
            transition: 0.3s;
        }

        .register-btn:hover {
            background: #ecd6c8;
        }

        .error {
            color: #d32f2f;
            text-align: center;
            margin-bottom: 15px;
            font-size: 14px;
        }

        .success {
            color: #2e7d32;
            text-align: center;
            margin-bottom: 15px;
            font-size: 14px;
        }
    </style>
</head>
<body>
<div class="card">
    <h3>Login</h3>
    <p class="subtitle">Login to your POS account</p>
    <form:form action="/login" method="post">
        <c:if test="${param.error != null}">
            <div class="error">
                Invalid username or password
            </div>
        </c:if>
        <c:if test="${param.logout != null}">
            <div class="success">
                You have been logged out successfully
            </div>
        </c:if>
        <div class="mb-3">
            <label class="form-label">Username</label>
            <input type="text" name="username" class="form-control" placeholder="Enter username" required />
        </div>
        <div class="mb-4">
            <label class="form-label">Password</label>
            <input type="password" name="password" class="form-control" placeholder="Enter password" required />
        </div>
        <button type="submit" class="btn btn-primary">
            Login
        </button>
        <p class="register-text">Don't have an account?</p>
        <a href="/register" class="register-btn">
            Create New Account
        </a>
    </form:form>
</div>
</body>
</html>