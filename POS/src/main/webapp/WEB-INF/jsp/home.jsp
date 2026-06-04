<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Home</title>

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Inter', sans-serif;
            background: #020617;
        }


        .topbar {
            height: 60px;
            background: #020617;
            display: flex;
            align-items: center;
            padding: 0 20px;
            position: fixed;
            width: 100%;
            top: 0;
            z-index: 1000;
            box-shadow: 0 2px 8px rgba(0,0,0,0.6);
            border-bottom: 1px solid #1e293b;
        }

        .topbar-left {
            display: flex;
            align-items: center;
        }

        .menu {
            width: 28px;
            cursor: pointer;
            margin-right: 16px;
        }

        .menu div {
            height: 3px;
            background: #38bdf8;
            margin: 5px 0;
            border-radius: 3px;
        }

        .topbar strong {
            font-size: 18px;
            color: #38bdf8;
        }

        .logout-form {
            position: absolute;
            right: 40px;
            margin: 0;
        }

        .logout-btn {
            padding: 8px 18px;
            background: linear-gradient(135deg, #38bdf8, #0284c7);
            color: #020617;
            border: none;
            border-radius: 20px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.25s;
        }

        .logout-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(56,189,248,0.5);
        }

        .sidebar {
            position: fixed;
            top: 60px;
            left: -240px;
            width: 240px;
            height: calc(100vh - 60px);
            background: #020617;
            transition: left 0.3s ease;
            overflow-y: auto;
            border-right: 1px solid #1e293b;
        }

        .sidebar.active {
            left: 0;
        }

        .sidebar a {
            display: block;
            padding: 14px 20px;
            color: #e5e7eb;
            text-decoration: none;
            margin: 6px;
            border-radius: 8px;
            font-weight: 500;
            transition: all 0.25s;
        }

        .sidebar a:hover {
            background: linear-gradient(135deg, #38bdf8, #0284c7);
            color: #020617;
            transform: translateX(6px);
        }

        .content {
            margin-top: 60px;
            height: calc(100vh - 60px);
            display: flex;
            justify-content: center;
            align-items: flex-start;
            padding-top: 100px;
        }

        .welcome-box {
            background: #020617;
            padding: 35px;
            border-radius: 18px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.7);
            width: 420px;
            text-align: center;
            border: 1px solid #1e293b;
        }

        .welcome {
            font-size: 22px;
            font-weight: 600;
            color: #38bdf8;
        }

        .welcome-sub {
            margin-top: 10px;
            color: #94a3b8;
            font-size: 14px;
        }
    </style>

    <script>
        function toggleMenu() {
            const sidebar = document.getElementById("sidebar");
            sidebar.classList.toggle("active");

            localStorage.setItem(
                "sidebarState",
                sidebar.classList.contains("active") ? "open" : "closed"
            );
        }

        window.onload = function () {
            const sidebar = document.getElementById("sidebar");
            if (localStorage.getItem("sidebarState") === "open") {
                sidebar.classList.add("active");
            }
        };
    </script>
</head>

<body>

<div class="topbar">
    <div class="topbar-left">
        <div class="menu" onclick="toggleMenu()">
            <div></div>
            <div></div>
            <div></div>
        </div>
        <strong>Role Management</strong>
    </div>

    <form action="${pageContext.request.contextPath}/logout" method="post" class="logout-form">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
</div>

<div class="sidebar" id="sidebar">
    <c:if test="${not empty nodes}">
        <c:forEach var="node" items="${nodes}">
            <a href="${pageContext.request.contextPath}${node.path}">
                ${node.identifier}
            </a>
        </c:forEach>
    </c:if>
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