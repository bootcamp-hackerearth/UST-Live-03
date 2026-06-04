<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>POS Dashboard</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background: #f7f9fc;
            font-family: "Segoe UI", system-ui, sans-serif;
            margin: 0;
        }

        .navbar-custom {
            background: linear-gradient(to right, #0f766e, #134e4a);
            height: 56px;
        }

        .navbar-brand {
            font-weight: 600;
            color: #ffffff !important;
        }

        .sidebar {
            width: 260px;
            background: #020617;
            height: calc(100vh - 56px);
            position: fixed;
            top: 56px;
            left: 0;
            padding: 20px 15px;
            transition: width 0.3s ease;
            overflow-x: hidden;
        }

        .sidebar.collapsed {
            width: 70px;
        }

        .sidebar-title {
            color: #94a3b8;
            font-size: 12px;
            text-transform: uppercase;
            margin-bottom: 15px;
        }

        .nav-link-custom {
            display: flex;
            align-items: center;
            padding: 12px;
            margin-bottom: 8px;
            border-radius: 10px;
            color: #e5e7eb;
            text-decoration: none;
            font-size: 14px;
            transition: 0.2s;
            white-space: nowrap;
        }

        .nav-link-custom i {
            font-size: 16px;
            min-width: 30px;
            text-align: center;
        }

        .nav-link-custom span {
            transition: opacity 0.2s ease;
        }

        .sidebar.collapsed .nav-link-custom span,
        .sidebar.collapsed .sidebar-title {
            opacity: 0;
        }

        .nav-link-custom:hover {
            background: #0f766e;
            color: white;
        }

        .main-content {
            margin-left: 260px;
            margin-top: 56px;
            padding: 30px;
            transition: margin-left 0.3s ease;
        }

        .main-content.collapsed {
            margin-left: 70px;
        }

        .welcome-card {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: white;
            border-radius: 16px;
            padding: 30px;
        }

        .toggle-btn {
            border: none;
            background: transparent;
            color: white;
            font-size: 20px;
            margin-right: 12px;
            cursor: pointer;
        }
    </style>

    <script>
        function toggleSidebar() {
            document.getElementById("sidebar").classList.toggle("collapsed");
            document.getElementById("content").classList.toggle("collapsed");
        }
    </script>
</head>

<body>

<nav class="navbar navbar-expand navbar-dark navbar-custom fixed-top px-3">
    <button class="toggle-btn" onclick="toggleSidebar()">
        <i class="bi bi-list"></i>
    </button>

    <span class="navbar-brand">
        <i class="bi bi-cash-coin me-2"></i> POS SYSTEM
    </span>

    <div class="ms-auto">
        <form action="${pageContext.request.contextPath}/logout" method="post">
            <button type="submit" class="btn btn-danger btn-sm">
                <i class="bi bi-box-arrow-right me-1"></i> Logout
            </button>
        </form>
    </div>
</nav>

<div class="sidebar" id="sidebar">
    <div class="sidebar-title">Modules</div>

    <c:if test="${empty nodes}">
        <div class="text-muted text-center small">
            No modules available
        </div>
    </c:if>

    <c:forEach var="node" items="${nodes}">
        <a class="nav-link-custom"
           href="${pageContext.request.contextPath}${node.path}">
            <i class="bi bi-grid-fill"></i>
            <span>${node.identifier}</span>
        </a>
    </c:forEach>
</div>

<div class="main-content" id="content">

    <div class="welcome-card">
        <h3 class="mb-2">Welcome to POS Dashboard</h3>
        <p class="mb-0">
            Use the menu on the left to manage products, inventory, customers,
            warehouses, and system modules.
        </p>
    </div>

</div>

</body>
</html>