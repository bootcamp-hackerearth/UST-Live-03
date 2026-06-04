<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Home</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            min-height: 100vh;
            font-family: "Segoe UI", sans-serif;
            background: radial-gradient(circle at 20% 30%, rgba(255,122,0,0.15), transparent 40%),
                        radial-gradient(circle at 80% 70%, rgba(255,72,0,0.15), transparent 40%),
                        linear-gradient(135deg, #1f1f1f, #3a2e2a);
        }

        .overlay {
            min-height: 100vh;
            background: rgba(255,255,255,0.08);
            backdrop-filter: blur(6px);
        }

        .navbar {
            background: linear-gradient(90deg, #1f1f1f, #3a2e2a);
            box-shadow: 0 2px 10px rgba(0,0,0,0.3);
        }

        .navbar-brand {
            font-weight: 600;
            color: white !important;
        }

        .navbar-toggler {
            border: 1px solid rgba(255,255,255,0.5);
            padding: 6px 10px;
        }

        .navbar-toggler-icon {
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 30 30'%3E%3Cpath stroke='rgba(255,255,255,1)' stroke-width='2' stroke-linecap='round' stroke-miterlimit='10' d='M4 7h22M4 15h22M4 23h22'/%3E%3C/svg%3E");
            width: 1.5em;
            height: 1.5em;
        }

        .sidebar {
            width: 260px;
        }

        .offcanvas {
            background: rgba(255,255,255,0.9);
            backdrop-filter: blur(15px);
            display: flex;
            flex-direction: column;
        }

        .offcanvas-body {
            display: flex;
            flex-direction: column;
            height: 100%;
        }

        .menu-links {
            flex-grow: 1;
        }

        .nav-link {
            font-weight: 500;
            color: #333;
            padding: 10px 12px;
            border-radius: 10px;
            margin-bottom: 5px;
        }

        .nav-link:hover {
            background: #fff3ed;
            color: #ff4800;
        }

        .logout-btn {
            margin-top: auto;
            padding-top: 10px;
            border-top: 1px solid rgba(0,0,0,0.1);
        }

        .logout-btn a {
            display: block;
            text-align: center;
            padding: 10px;
            border-radius: 20px;
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: white;
            text-decoration: none;
            font-weight: 500;
        }

        .content {
            padding: 2rem;
        }

        .welcome-card {
            max-width: 500px;
            margin: 80px auto;
            background: rgba(255,255,255,0.12);
            backdrop-filter: blur(20px);
            border-radius: 16px;
            padding: 30px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
            border: 1px solid rgba(255,255,255,0.15);
            text-align: center;
        }

        .welcome-card h3 {
            color: #fff;
            margin-bottom: 10px;
        }

        .welcome-card p {
            color: #ddd;
        }
    </style>
</head>

<body>

<div class="overlay">

<nav class="navbar navbar-dark">
    <div class="container-fluid">

        <button class="navbar-toggler" type="button"
                data-bs-toggle="offcanvas"
                data-bs-target="#sidebarMenu">
            <span class="navbar-toggler-icon"></span>
        </button>

        <span class="navbar-brand">Application Menu</span>

    </div>
</nav>

<div class="offcanvas offcanvas-start sidebar" tabindex="-1" id="sidebarMenu">

    <div class="offcanvas-header">
        <h5 class="offcanvas-title">Menu</h5>
        <button type="button" class="btn-close" data-bs-dismiss="offcanvas"></button>
    </div>

    <div class="offcanvas-body">

        <div class="menu-links">

            <c:if test="${empty nodes}">
                <div class="alert alert-warning">
                    No menu items available
                </div>
            </c:if>

            <c:if test="${not empty nodes}">
                <nav class="nav flex-column">
                    <c:forEach var="node" items="${nodes}">
                        <a class="nav-link"
                           href="${pageContext.request.contextPath}${node.path}">
                            ${node.identifier}
                        </a>
                    </c:forEach>
                </nav>
            </c:if>

        </div>

        <div class="logout-btn">
            <a href="${pageContext.request.contextPath}/logout">
                Logout
            </a>
        </div>

    </div>
</div>

<div class="content">
    <div class="welcome-card">
        <h3>Welcome</h3>
        <p>Use the menu to navigate</p>
    </div>
</div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>