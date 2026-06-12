<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Home</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css">

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            background-color: #f4f6fb;
        }

        .sidebar {
            position: fixed;
            top: 0;
            left: 0;
            width: 250px;
            height: 100vh;
            background: linear-gradient(180deg, #4e73df, #224abe);
            padding-top: 20px;
            box-shadow: 3px 0 15px rgba(0,0,0,0.1);
        }

        .sidebar h4 {
            color: #ffffff;
            text-align: center;
            margin-bottom: 20px;
            font-weight: 600;
            font-size: 20px;
        }

        /* NODES SCROLL */
        .sidebar-menu {
            max-height: calc(100vh - 140px); /* header + logout space */
            overflow-y: auto;
        }

        .sidebar-menu a {
            display: block;
            padding: 14px 25px;
            color: rgba(255,255,255,0.9);
            text-decoration: none;
            font-size: 16px;
            font-weight: 500;
            letter-spacing: 0.3px;
            transition: all 0.3s ease;
        }

        .sidebar-menu a:hover {
            background: rgba(255,255,255,0.15);
            color: #ffffff;
            padding-left: 32px;
        }

        .logout-btn {
            position: absolute;
            bottom: 20px;
            width: 100%;
            padding: 0 20px;
        }

        .logout-btn button {
            width: 100%;
            border-radius: 8px;
            font-size: 13px;
            padding: 8px;
            background: #ffffff;
            color: #4e73df;
            border: none;
            font-weight: 500;
        }

        .content {
            margin-left: 250px;
            padding: 30px;
        }

        .header-banner {
            background: linear-gradient(135deg, #4e73df, #6f42c1);
            color: #fff;
            padding: 25px;
            border-radius: 12px;
            margin-bottom: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }

        .header-banner h2 {
            margin: 0;
            font-weight: 600;
        }

        .header-banner p {
            margin: 5px 0 0;
            opacity: 0.9;
        }

        .welcome-card {
            margin-top: 25px;
            background: #ffffff;
            border-radius: 10px;
            padding: 25px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.06);
        }
    </style>
</head>

<body>

<div class="sidebar">
    <h4>POS System</h4>

    <!-- SCROLLABLE -->
    <div class="sidebar-menu">
        <c:if test="${not empty nodes}">
            <c:forEach items="${nodes}" var="nav">
                <a href="${pageContext.request.contextPath}${nav.path}">
                    ${nav.identifier}
                </a>
            </c:forEach>
        </c:if>

        <c:if test="${empty nodes}">
            <p style="color:#ccc;text-align:center;">No access</p>
        </c:if>
    </div>

    <!-- LOGOUT FIXED -->
    <div class="logout-btn">
        <form action="${pageContext.request.contextPath}/logout" method="post">
            <button type="submit">Logout</button>
        </form>
    </div>
</div>

<div class="content">
    <div class="header-banner">
        <h2>Welcome to POS System</h2>
        <p>Manage sales, products, and transactions quickly and efficiently</p>
    </div>

    <div class="welcome-card">
        <h4>Getting Started</h4>
        <p>
            Select a module from the sidebar to continue managing your application.
        </p>
    </div>
</div>

</body>
</html>