<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS Dashboard</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            background: #f4f6f9;
            color: #2f3640;
        }

        .layout {
            display: flex;
            height: 100vh;
        }

        .sidebar {
            width: 250px;
            background: #1e1f26;
            color: #fff;
            display: flex;
            flex-direction: column;
        }

        .brand {
            padding: 18px 20px;
            font-size: 18px;
            font-weight: 600;
            background: #14151a;
        }

        .menu {
            flex: 1;
            padding-top: 10px;
        }

        .menu a {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px 20px;
            font-size: 14px;
            color: #bfc4d1;
            text-decoration: none;
            transition: 0.2s ease;
        }

        .menu a:hover {
            background: rgba(255,255,255,0.08);
            color: #fff;
        }

        .logout {
            padding: 15px 20px;
            border-top: 1px solid rgba(255,255,255,0.08);
        }

        .logout button {
            width: 100%;
            padding: 10px;
            border-radius: 6px;
            border: none;
            background: #e84118;
            color: #fff;
            font-size: 14px;
            cursor: pointer;
        }

        .main {
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        .header {
            height: 60px;
            background: #ffffff;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 25px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
        }

        .content {
            padding: 30px;
        }

        .welcome {
            text-align: center;
            font-size: 26px;
            font-weight: 600;
            margin-bottom: 35px;
        }

        .cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 20px;
        }

        .card {
            background: #ffffff;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.07);
            cursor: pointer;
        }

        .card i {
            font-size: 26px;
            color: #00a8ff;
        }

        .card h3 {
            margin: 10px 0 5px;
            font-size: 18px;
        }

        .card p {
            font-size: 13px;
            color: #7f8fa6;
        }

        @media (max-width: 768px) {
            .sidebar { width: 70px; }
            .menu a span { display: none; }
            .brand { text-align: center; }
        }
    </style>
</head>

<body>

<div class="layout">

    <div class="sidebar">
        <div class="brand">POS System</div>

        <div class="menu">
            <c:forEach var="node" items="${nodes}">
                <a href="${pageContext.request.contextPath}${node.path}">
                    <i class="bi bi-grid-fill"></i>
                    <span>${node.identifier}</span>
                </a>
            </c:forEach>
        </div>

        <div class="logout">
            <form action="${pageContext.request.contextPath}/logout" method="post">
                <button type="submit">
                    <i class="bi bi-box-arrow-right"></i> Logout
                </button>
            </form>
        </div>
    </div>

    <div class="main">
        <div class="header">
            <h2>Dashboard</h2>
            <div class="user">
                <i class="bi bi-person-circle"></i>
                <c:choose>
                    <c:when test="${not empty pageContext.request.userPrincipal}">
                        ${pageContext.request.userPrincipal.name}
                    </c:when>
                    <c:otherwise>
                        Guest
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="content">

            <div class="welcome">
                Welcome to POS Application
            </div>

            <div class="cards">
                <div class="card">
                    <i class="bi bi-cart-check"></i>
                    <h3>Sales</h3>
                    <p>View daily sales summary</p>
            </div>

            <div class="card">
                <i class="bi bi-box-seam"></i>
                <h3>Inventory</h3>
                <p>Manage stock levels</p>
            </div>

            <div class="card">
                <i class="bi bi-people"></i>
                <h3>Customers</h3>
                <p>Customer management</p>
            </div>

            <div class="card">
                <i class="bi bi-receipt"></i>
                <h3>Reports</h3>
                <p>Generate POS reports</p>
            </div>
            </div>

        </div>
    </div>
</div>

</body>
</html>