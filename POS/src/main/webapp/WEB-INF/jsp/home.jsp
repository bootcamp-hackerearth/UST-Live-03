<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS Application</title>

    <style>
        html, body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background-color: #ffffff;
            color: #1f2937;

            overflow: hidden;
        }

        /* ===== TOP BAR ===== */
        .topbar {
            height: 56px;
            background-color: teal;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 20px;
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
            margin-right: 14px;
        }

        .menu div {
            height: 3px;
            background: #ffffff;
            margin: 4px 0;
        }

        .top-title {
            font-size: 15px;
            font-weight: 600;
            color: #ffffff;
        }

        .logout-btn {
            background: #ffffff;
            color: teal;
            border: none;
            padding: 7px 16px;
            border-radius: 20px;
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
        }

        /* ===== SIDEBAR ===== */
        .sidebar {
            position: fixed;
            top: 56px;
            left: -240px;
            width: 240px;
            height: calc(100vh - 56px);
            background-color: #f9fafb;
            border-right: 1px solid #e5e7eb;
            transition: left 0.3s ease;
            overflow-y: auto;
            scrollbar-width: none;
            -ms-overflow-style: none;
        }

        .sidebar::-webkit-scrollbar {
            display: none;
        }

        .sidebar.active {
            left: 0;
        }

        .sidebar-title {
            padding: 10px 16px 4px;
            font-size: 11px;
            color: #64748b;
            font-weight: 700;
        }

        .sidebar a {
            display: block;
            padding: 9px 16px;
            font-size: 13px;
            color: #334155;
            text-decoration: none;
            border-left: 3px solid transparent;
            border-radius: 6px;
            margin: 3px 10px;
        }

        .sidebar a:hover {
            background-color: #e6fffa;
            border-left: 3px solid teal;
        }

        .sidebar a.active-link {
            background-color: #e6fffa;
            border-left: 3px solid teal;
            font-weight: 600;
        }

        /* ===== CONTENT ===== */
        .content {
            margin-top: 56px;
            height: calc(100vh - 56px);
        }

        .content.shifted {
            margin-left: 240px;
        }

        /* ===== FIXED CARD ===== */
        .card {
            position: fixed;
            top: 100px;
            left: 50%;
            transform: translateX(-50%);
            width: 650px;
            background: #f9fafb;
            border-radius: 12px;
            padding: 20px;
            border-top: 4px solid teal;
            box-shadow: 0 3px 10px rgba(0,0,0,0.06);
        }

        .card h2 {
            font-size: 22px;
            margin-bottom: 8px;
        }

        .card p {
            font-size: 14px;
            color: #475569;
            line-height: 1.4;
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

<!-- TOP BAR -->
<div class="topbar">
    <div class="topbar-left">
        <div class="menu" onclick="toggleMenu()">
            <div></div>
            <div></div>
            <div></div>
        </div>
        <div class="top-title">POS Application</div>
    </div>

    <form action="${pageContext.request.contextPath}/logout" method="post">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<!-- SIDEBAR -->
<div class="sidebar" id="sidebar">

    <div class="sidebar-title">CORE NAVIGATION</div>

    <a href="${pageContext.request.contextPath}/" class="active-link">
        Home Dashboard
    </a>

    <div class="sidebar-title">MANAGEMENT MODULES</div>

    <c:forEach var="node" items="${nodes}">
        <a href="${pageContext.request.contextPath}${node.path}">
            ${node.identifier}
        </a>
    </c:forEach>

</div>

<!-- CONTENT -->
<div class="content" id="content">
    <div class="card">
        <h2>Welcome to the System</h2>
        <p>
            Efficiently manage your retail operations. Select a module from the
            navigation menu on the left to manage users, configure roles, or monitor system nodes.
        </p>
    </div>
</div>

</body>
</html>