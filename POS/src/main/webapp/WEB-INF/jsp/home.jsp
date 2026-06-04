<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS Application</title>

    <style>

        body {
            margin: 0;
            font-family: "Inter", sans-serif;
            background-color: #3f3f3f;
            color: #2f2f2f;
        }

        .topbar {
            height: 56px;
            background-color: #e2d8cb;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 20px;
            border-bottom: 1px solid #c8b9a7;
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            z-index: 1000;
        }

        .topbar-left {
            display: flex;
            align-items: center;
        }

        .menu {
            width: 26px;
            cursor: pointer;
            margin-right: 16px;
            transition: transform 0.2s ease;
        }

        .menu:hover {
            transform: scale(1.08);
        }

        .menu div {
            height: 3px;
            background: #3f3f3f;
            margin: 5px 0;
            border-radius: 2px;
        }

        .top-title {
            font-size: 14px;
            font-weight: 700;
            letter-spacing: 1px;
            color: #2f2f2f;
        }

        .logout-btn {
            background: #3f3f3f;
            color: #fff;
            border: 2px solid #3f3f3f;
            padding: 6px 14px;
            font-size: 12px;
            font-weight: 700;
            cursor: pointer;
            transition: all 0.25s ease;
        }

        .logout-btn:hover {
            background: transparent;
            color: #3f3f3f;
            transform: scale(1.04);
        }

        .sidebar {
            position: fixed;
            top: 56px;
            left: -240px;
            width: 240px;
            height: calc(100vh - 56px);
            background: #f3efe9;
            border-right: 1px solid #d6d0c7;
            transition: left 0.3s ease;
            overflow-y: auto;
            z-index: 999;
        }

        .sidebar.active {
            left: 0;
        }

        .sidebar-title {
            padding: 18px 20px 8px;
            font-size: 11px;
            font-weight: 700;
            letter-spacing: 1.5px;
            color: #8b8b8b;
        }

        .sidebar a {
            display: block;
            padding: 14px 20px;
            text-decoration: none;
            color: #3f3f3f;
            font-size: 13px;
            font-weight: 600;
            border-left: 3px solid transparent;
            transition: all 0.25s ease;
            position: relative;
        }

        .sidebar a:hover {
            background: #e7e2db;
            border-left: 3px solid #3f3f3f;
            transform: translateX(6px);
            letter-spacing: 0.5px;
            box-shadow: inset 0 0 8px rgba(0,0,0,0.05);
        }

        .dashboard-link {
            background: #e7e2db;
            border-left: 3px solid #3f3f3f !important;
        }

        .content {
            margin-top: 56px;
            padding: 40px;
            transition: margin-left 0.3s ease;
        }

        .content.shifted {
            margin-left: 240px;
        }

        .card {
            background: #f3efe9;
            padding: 32px;
            border-radius: 8px;
            max-width: 700px;
            border: 2px solid transparent;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            transition: background 0.25s ease;
        }

        .card:hover {
            background: #e2d8cb;
        }

        .welcome {
            font-size: 24px;
            font-weight: 700;
            margin-bottom: 16px;
            color: #2f2f2f;
        }

        .welcome-sub {
            font-size: 15px;
            color: #6f6f6f;
            line-height: 1.7;
        }

        .sidebar::-webkit-scrollbar {
            width: 6px;
        }

        .sidebar::-webkit-scrollbar-thumb {
            background: #c5beb3;
            border-radius: 10px;
        }

    </style>

    <script>

        function toggleMenu() {
            document.getElementById("sidebar").classList.toggle("active");
            document.getElementById("content").classList.toggle("shifted");
        }

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

        <div class="top-title">
            POS APPLICATION
        </div>

    </div>

    <form action="${pageContext.request.contextPath}/logout" method="post">

        <button type="submit" class="logout-btn">
            LOGOUT
        </button>

    </form>

</div>

<div class="sidebar" id="sidebar">

    <div class="sidebar-title">
        CORE NAVIGATION
    </div>

    <a href="${pageContext.request.contextPath}/"
       class="dashboard-link">
        Home Dashboard
    </a>

    <div class="sidebar-title">
        MANAGEMENT MODULES
    </div>

    <c:forEach var="node" items="${nodes}">

        <a href="${pageContext.request.contextPath}${node.path}">
            ${node.identifier}
        </a>

    </c:forEach>

</div>

<div class="content" id="content">

    <div class="card">

        <div class="welcome">
            Welcome to the System
        </div>

        <div class="welcome-sub">

            Efficiently manage your retail operations.
            Select a module from the navigation menu on the left
            and monitor system activities through
            the centralized POS management dashboard.

        </div>

    </div>

</div>

</body>
</html>