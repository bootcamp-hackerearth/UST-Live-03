<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Home</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet"/>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            background-color: #f1f3f6;
            font-family: "Segoe UI", sans-serif;
        }

        .wrapper {
            display: flex;
            min-height: 100vh;
        }

        .sidebar {
            width: 280px;
            background-color: #1e272e;
            color: #ffffff;
            padding: 25px;
            flex-shrink: 0;
        }

        .brand {
            font-size: 1.5rem;
            font-weight: 600;
        }

        .sidebar a {
            color: #dcdde1;
            text-decoration: none;
            padding: 12px 15px;
            display: flex;
            align-items: center;
            gap: 10px;
            border-radius: 8px;
            font-size: 1rem;
            transition: 0.3s;
        }

        .sidebar a:hover {
            background-color: #353b48;
            color: #ffffff;
        }

        .content-area {
            flex-grow: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 30px;
        }

        .card {
            width: 650px;
            border: none;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.12);
        }

        .card-header {
            background-color: #1e272e;
            color: #ffffff;
            text-align: center;
            padding: 25px;
        }

        .card-header h4 {
            margin: 0;
            font-size: 2rem;
            font-weight: 600;
        }

        .card-body {
            background-color: #ffffff;
            padding: 35px;
            text-align: center;
        }

        .card-footer {
            background-color: #f8f9fa;
            text-align: center;
            padding: 15px;
            border-top: 1px solid #dee2e6;
            color: #6c757d;
        }

        .logout-btn {
            border: none;
            background: transparent;
            color: #dcdde1;
            font-size: 1.2rem;
        }

        .logout-btn:hover {
            color: #ffffff;
        }

        .alert {
            border-radius: 8px;
        }

        .welcome-title {
            font-size: 1.8rem;
            font-weight: 600;
            margin-bottom: 15px;
        }

        .welcome-text {
            color: #6c757d;
            font-size: 1rem;
            line-height: 1.7;
        }
    </style>
</head>

<body>

<div class="wrapper">

    <div class="sidebar">

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

        <div class="card">

            <div class="card-header">
                <h4>POS Management System</h4>
            </div>

            <div class="card-body">

                <div class="welcome-title">
                    Welcome to the POS Management System
                </div>

                <div class="welcome-text">
                    Use the navigation menu on the left to manage users, roles,
                    configurations, products, customers, and other system operations.
                </div>

            </div>

            <div class="card-footer">
                POS Management System
            </div>

        </div>

    </div>

</div>

</body>
</html>