<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Home</title>

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Inter', sans-serif;
            background: #d1d5db;
        }

        /* TOPBAR */
        .topbar {
            height: 60px;
            background: #cbd5e1;
            display: flex;
            align-items: center;
            padding: 0 20px;
            position: fixed;
            width: 100%;
            top: 0;
            z-index: 1000;
        }

        .topbar-left {
            display: flex;
            align-items: center;
        }

        /* Adjusted to native button styling to preserve your layout */
        .menu {
            width: 28px;
            cursor: pointer;
            margin-right: 16px;
            background: none;
            border: none;
            padding: 0;
        }

        .menu div {
            height: 3px;
            background: #0891b2;
            margin: 5px 0;
        }

        .topbar strong {
            font-size: 18px;
            color: #0891b2;
        }

        /* LOGOUT BUTTON */
        .logout-form {
            position: absolute;
            right: 50px;
            margin: 0;
        }

        .logout-btn {
            padding: 8px 18px;
            background: linear-gradient(135deg, #0891b2, #0e7490);
            color: white;
            border: none;
            border-radius: 20px;
            font-weight: 600;
            cursor: pointer;
            transition: 0.25s;
        }

        .logout-btn:hover {
            background: linear-gradient(135deg, #0e7490, #075985);
            transform: translateY(-2px);
            box-shadow: 0 6px 15px rgba(8,145,178,0.4);
        }

        /* SIDEBAR */
        .sidebar {
            position: fixed;
            top: 60px;
            left: -240px;
            width: 240px;
            height: calc(100vh - 60px);
            background: #94a3b8;
            transition: 0.3s;
        }

        .sidebar.active {
            left: 0;
        }

        .sidebar a {
            display: block;
            padding: 14px 20px;
            color: #0f172a;
            text-decoration: none;
            margin: 6px;
            border-radius: 8px;
            transition: 0.25s;
        }

        .sidebar a:hover {
            background: linear-gradient(135deg, #0e7490, #0891b2);
            color: white;
            transform: translateX(6px);
        }

        /* CONTENT */
        .content {
            margin-top: 60px;
            height: calc(100vh - 60px);
            display: flex;
            justify-content: center;
            align-items: flex-start;
            padding-top: 80px;
        }

        /* CARD */
        .welcome-box {
            background: #f1f5f9;
            padding: 40px 50px;
            border-radius: 16px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
            text-align: center;
        }

        .welcome {
            font-size: 26px;
            font-weight: 600;
            color: #0891b2;
        }

        .welcome-sub {
            margin-top: 10px;
            color: #475569;
            font-size: 15px;
        }
    </style>

    <script>
        function toggleMenu() {
            document.getElementById("sidebar").classList.toggle("active");
        }
    </script>
</head>

<body>

<div class="topbar">
    <div class="topbar-left">
        <button class="menu" onclick="toggleMenu()" aria-label="Toggle Menu">
            <div></div>
            <div></div>
            <div></div>
        </button>
        <strong>Role Management</strong>
    </div>

    <form action="${pageContext.request.contextPath}/logout" method="post" class="logout-form">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
</div>

<div class="sidebar" id="sidebar">
    <c:forEach var="node" items="${nodes}">
        <a href="${pageContext.request.contextPath}${node.path}">
            ${node.identifier}
        </a>
    </c:forEach>
</div>

<div class="content">
    <div class="welcome-box">
        <div class="welcome">Welcome to the Role Management System</div>
        <div class="welcome-sub">
            Please select an option from the menu.
        </div>
    </div>
</div>

</body>
</html>