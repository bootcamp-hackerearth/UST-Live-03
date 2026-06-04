<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS Application</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background-color: #0f172a;
            color: #e5e7eb;
        }

        /* ===== TOP BAR ===== */
        .topbar {
            height: 56px;
            background-color: #020617;
            display: flex;
            align-items: center;
            padding: 0 20px;
            justify-content: space-between;
            border-bottom: 1px solid #1e293b;
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
            width: 28px;
            cursor: pointer;
            margin-right: 16px;
        }

        .menu div {
            height: 3px;
            background: #e5e7eb;
            margin: 5px 0;
            border-radius: 2px;
        }

        .top-title {
            font-size: 16px;
            font-weight: 600;
            letter-spacing: 0.4px;
        }

        /* ===== LOGOUT ===== */
        .logout-btn {
            background: #dc2626;
            border: none;
            color: white;
            padding: 7px 16px;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
        }

        .logout-btn:hover {
            background: #b91c1c;
        }

        /* ===== SIDEBAR ===== */
        .sidebar {
            position: fixed;
            top: 56px;
            left: -220px;
            width: 220px;
            height: calc(100vh - 56px);
            background-color: #020617;
            transition: left 0.3s ease;
            border-right: 1px solid #1e293b;
            z-index: 999;
        }

        .sidebar.active {
            left: 0;
        }

        .sidebar a {
            display: block;
            padding: 14px 20px;
            color: #cbd5f5;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
            border-left: 3px solid transparent;
            transition: all 0.2s ease;
        }

        .sidebar a:hover {
            background-color: #1e293b;
            color: #ffffff;
            border-left: 3px solid #2563eb;
        }

        /* ===== CONTENT ===== */
        .content {
            margin-top: 56px;
            padding: 48px;
            transition: margin-left 0.3s ease;
        }

        .content.shifted {
            margin-left: 220px;
        }

        .welcome {
            font-size: 20px;
            font-weight: 600;
            margin-bottom: 6px;
        }

        .welcome-sub {
            font-size: 14px;
            color: #94a3b8;
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

<!--  TOP BAR -->
<div class="topbar">
    <div class="topbar-left">
        <div class="menu" onclick="toggleMenu()">
            <div></div>
            <div></div>
            <div></div>
        </div>
        <div class="top-title">POS Application</div>
    </div>

    <!--  LOGOUT -->
    <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
</div>

<!--  SIDEBAR -->
<div class="sidebar" id="sidebar">
    <c:forEach var="node" items="${nodes}">
        <a href="${pageContext.request.contextPath}${node.path}">
            ${node.identifier}
        </a>
    </c:forEach>
</div>

<!-- CONTENT -->
<div class="content" id="content">
    <div class="welcome">
        Welcome to the POS Application
    </div>
    <div class="welcome-sub">
        Use the navigation menu to access system modules.
    </div>
</div>

</body>
</html>