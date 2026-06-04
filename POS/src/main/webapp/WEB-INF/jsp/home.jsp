<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Home</title>
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">
    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            background-color: #E9EEF5        }
        .sidebar {
            position: fixed;
            top: 0;
            left: 0;
            width: 240px;
            height: 100vh;
            background: linear-gradient(180deg, #1e3c72, #2a5298);
            padding-top: 30px;
            box-shadow: 2px 0 10px rgba(0,0,0,0.15);
        }
        .sidebar h4 {
            color: #fff;
            text-align: center;
            margin-bottom: 30px;
            font-weight: 600;
            letter-spacing: 1px;
        }
        .sidebar a {
            display: block;
            padding: 14px 25px;
            color: #d1d1d1;
            text-decoration: none;
            font-size: 15px;
            transition: all 0.3s ease;
        }
        .sidebar a:hover {
            background-color: rgba(255, 255, 255, 0.1);
            color: #ffffff;
            padding-left: 35px;
        }
        .logout-btn {
            position: absolute;
            bottom: 30px;
            width: 100%;
            padding: 0 25px;
        }
        .logout-btn button {
            width: 100%;
        }
        .content {
            margin-left: 240px;
            padding: 40px;
        }
        .welcome-card {
            background: #ffffff;
            border-radius: 12px;
            padding: 40px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        }
    </style>
</head>
<body>

<div class="sidebar">
    <h4>Dashboard</h4>

    <c:forEach items="${nodes}" var="nav">
        <a href="${nav.path}">${nav.identifier}</a>
    </c:forEach>

    <div class="logout-btn">
        <form action="/logout" method="post">
            <button type="submit" class="btn btn-danger btn-sm">Logout</button>
        </form>
    </div>
</div>

<div class="content">
    <div class="welcome-card">
        <h1>Welcome</h1>
        <p>Select a module from the sidebar to continue managing your application.</p>
    </div>
</div>

</body>
</html>