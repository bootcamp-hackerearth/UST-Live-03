<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Home</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css"
          rel="stylesheet"/>

    <style>
        body {
            min-height: 100vh;
            margin: 0;
            background-color: #f1f3f6;
            font-family: "Segoe UI", sans-serif;
        }

        .wrapper {
            display: flex;
        }

        .sidebar {
            width: 260px;
            min-height: 100vh;
            background: linear-gradient(180deg, #1e272e, #2f3640);
            color: #fff;
            flex-shrink: 0;
        }

        .brand {
            font-size: 1.3rem;
            font-weight: 600;
            letter-spacing: 0.5px;
        }

        .sidebar a {
            color: #dcdde1;
            text-decoration: none;
            padding: 10px 14px;
            display: flex;
            align-items: center;
            gap: 10px;
            border-radius: 6px;
            font-size: 0.95rem;
        }

        .sidebar a:hover {
            background-color: #353b48;
            color: #fff;
        }

        .content-area {
            flex-grow: 1;
            padding: 40px;
        }

        .content-card {
            border: none;
            border-radius: 12px;
        }

        .logout-btn {
            border: none;
            background: transparent;
            color: #adb5bd;
            font-size: 1.1rem;
        }

        .logout-btn:hover {
            color: #fff;
        }
    </style>
</head>

<body>
<div class="wrapper">

    <div class="sidebar p-4">

        <div class="d-flex justify-content-between align-items-center mb-4">
            <div class="brand">
                POS System
            </div>

            <form action="/logout" method="post">
                <button type="submit" class="logout-btn">
                    <i class="bi bi-box-arrow-right"></i>
                </button>
            </form>
        </div>

        <hr class="text-secondary">

        <c:if test="${empty node}">
            <div class="alert alert-secondary text-center small">
                No menu items available
            </div>
        </c:if>

        <c:if test="${not empty node}">
            <ul class="nav flex-column gap-1 mt-3">
                <c:forEach var="n" items="${node}">
                    <li class="nav-item">
                        <a href="${n.path}">
                            <i class="bi bi-chevron-right"></i>
                            ${n.identifier}
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </c:if>

    </div>

    <div class="content-area">

        <div class="card content-card shadow-sm">
            <div class="card-body">

                <h5 class="fw-semibold mb-2">
                    Welcome to the POS Management System
                </h5>

                <p class="text-muted mb-0">
                    Use the navigation menu on the left to manage users, roles,
                    configurations, and other system operations.
                </p>
            </div>
        </div>

    </div>

</div>

</body>
</html>