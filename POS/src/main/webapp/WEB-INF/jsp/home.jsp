<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard | POS</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            min-height: 100vh;
            font-family: 'Inter', 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #f6f8fb, #eef2f7);
        }

        .navbar {
            background: linear-gradient(135deg, #4f46e5, #6366f1);
            box-shadow: 0 4px 20px rgba(0,0,0,0.08);
        }

        .navbar-brand {
            font-weight: 600;
            color: #ffffff !important;
            font-size: 16px;
        }

        .logout-btn {
            background: rgba(255,255,255,0.15);
            color: #ffffff;
            border: none;
            padding: 6px 14px;
            border-radius: 8px;
            font-size: 13px;
            cursor: pointer;
        }

        .logout-btn:hover {
            background: rgba(255,255,255,0.25);
        }

        .offcanvas {
            --bs-offcanvas-width: 190px;
            background: #111827;
            color: #e5e7eb;
        }

        .offcanvas-header {
            border-bottom: 1px solid rgba(255,255,255,0.1);
            padding: 14px;
        }

        .offcanvas-title {
            font-size: 15px;
            font-weight: 600;
        }

        .offcanvas-body {
            padding: 14px;
        }

        .nav-link {
            color: #e5e7eb;
            font-size: 13px;
            padding: 10px 12px;
            border-radius: 8px;
            margin-bottom: 6px;
            text-decoration: none;
            transition: 0.25s;
            display: block;
        }

        .nav-link:hover {
            background: rgba(99,102,241,0.25);
            color: #ffffff;
            transform: translateX(4px);
        }

        .content {
            padding: 2.5rem;
        }

        .welcome-card {
            max-width: 420px;
            margin: 90px auto;
            background: #ffffff;
            border-radius: 16px;
            padding: 28px;
            box-shadow: 0 25px 60px rgba(0,0,0,0.08);
            text-align: center;
        }

        .welcome-card h3 {
            font-size: 20px;
            color: #111827;
            margin-bottom: 6px;
        }

        .welcome-card p {
            color: #6b7280;
            font-size: 13px;
            margin: 0;
        }
    </style>
</head>

<body>

<nav class="navbar navbar-dark">
    <div class="container-fluid d-flex justify-content-between">
        <div class="d-flex align-items-center">
            <button class="navbar-toggler me-2" type="button"
                    data-bs-toggle="offcanvas"
                    data-bs-target="#sidebarMenu">
                ☰
            </button>
            <span class="navbar-brand">POS Dashboard</span>
        </div>

        <form action="/logout" method="post">
            <button class="logout-btn">Logout</button>
        </form>
    </div>
</nav>

<div class="offcanvas offcanvas-start" tabindex="-1" id="sidebarMenu">
    <div class="offcanvas-header">
        <h5 class="offcanvas-title">Menu</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="offcanvas"></button>
    </div>

    <div class="offcanvas-body">
        <c:if test="${empty nodes}">
            <div class="text-center text-muted small">
                No menu items available
            </div>
        </c:if>

        <c:if test="${not empty nodes}">
            <nav class="nav flex-column">
                <c:forEach var="node" items="${nodes}">
                    <a class="nav-link" href="${node.path}">
                        ${node.identifier}
                    </a>
                </c:forEach>
            </nav>
        </c:if>
    </div>
</div>

<div class="content">
    <div class="welcome-card">
        <h3>Welcome</h3>
        <p>Select a module from the menu</p>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>