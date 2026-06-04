<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            min-height: 100vh;
            font-family: "Segoe UI", sans-serif;
            background: linear-gradient(to right, #eef2f3, #dfe9f3);
        }

        .navbar {
            background: linear-gradient(to right, #4facfe, #00c6ff);
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .navbar-brand {
            font-weight: 600;
            color: white !important;
        }

        .logout-btn {
            background: #000;
            color: #fff;
            border: 1px solid #222;
            padding: 6px 14px;
            border-radius: 6px;
            font-weight: 600;
            cursor: pointer;
            transition: 0.2s;
        }

        .logout-btn:hover {
            background: #000;
            transform: scale(1.05);
        }

        .sidebar {
            width: 170px;
        }

        .offcanvas {
            background: linear-gradient(to bottom, #4facfe, #00c6ff);
            color: white;
        }

        .offcanvas-header {
            border-bottom: 1px solid rgba(255,255,255,0.25);
        }

        .offcanvas-title {
            color: white;
            font-weight: 700;
            font-size: 16px;
        }

        .nav-link {
            background: #ffffff;
            color: #111;
            font-size: 13px;
            font-weight: 600;
            padding: 8px 10px;
            border-radius: 6px;
            margin-bottom: 8px;
            display: block;
            transition: 0.25s;
            box-shadow: 0 2px 6px rgba(0,0,0,0.08);
            text-decoration: none;
        }

        .nav-link:hover {
            background: #f2f6ff;
            color: #000;
            transform: translateX(5px);
        }

        .content {
            padding: 2rem;
        }

        .welcome-card {
            max-width: 400px;
            margin: 80px auto;
            background: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            text-align: center;
        }

        .welcome-card h3 {
            color: #333;
        }

        .welcome-card p {
            color: #666;
            font-size: 13px;
        }
    </style>
</head>

<body>

<nav class="navbar navbar-dark">
    <div class="container-fluid d-flex justify-content-between">

        <div>
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

<div class="offcanvas offcanvas-start sidebar" tabindex="-1" id="sidebarMenu">

    <div class="offcanvas-header">
        <h5 class="offcanvas-title">Menu</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="offcanvas"></button>
    </div>

    <div class="offcanvas-body">

        <c:if test="${empty nodes}">
            <div class="alert alert-light text-dark">
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