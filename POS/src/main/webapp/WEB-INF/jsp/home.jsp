<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Home</title>

    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f2f2f2;
            display: flex;
            height: 100vh;
            overflow: hidden;
        }

        .sidebar {
            width: 240px;
            background: #343a40;
            color: #fff;
            padding: 15px;
            height: 100vh;
            overflow-y: auto;
            overflow-x: hidden;
        }

        .sidebar h3 {
            margin: 0 0 15px 0;
            font-size: 18px;
            position: sticky;
            top: 0;
            background: #343a40;
            padding: 10px 0;
            z-index: 10;
        }

        .nav-item {
            margin-bottom: 8px;
        }

        .nav-item a {
            color: #fff;
            text-decoration: none;
            display: block;
            padding: 8px 12px;
            background: #495057;
            border-radius: 4px;
            font-size: 14px;
            transition: background 0.2s ease;
        }

        .nav-item a:hover {
            background: #5a6268;
        }

        .main {
            flex: 1;
            display: flex;
            flex-direction: column;
            height: 100vh;
        }

        .top-bar {
            background: #fff;
            padding: 12px 20px;
            border-bottom: 1px solid #ccc;
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 18px;
            font-weight: bold;
            flex-shrink: 0;
        }

        .logout-btn {
            background: #4b6cb7;
            color: #fff;
            border: none;
            padding: 6px 14px;
            border-radius: 4px;
            cursor: pointer;
        }

        .logout-btn:hover {
            background: #c82333;
        }

        .content {
            padding: 20px;
            flex: 1;
            background: #fff;
            overflow-y: auto;
        }
    </style>
</head>

<body>

<div class="sidebar">
    <h3>Navigation</h3>

    <c:if test="${empty nodes}">
        <p>No navigation items found</p>
    </c:if>

    <c:forEach items="${nodes}" var="node">
        <div class="nav-item">
            <a href="${pageContext.request.contextPath}${node.path}">
                ${node.identifier}
            </a>
        </div>
    </c:forEach>
</div>

<div class="main">

    <div class="top-bar">
        <span>POS</span>
        <a href="${pageContext.request.contextPath}/logout">
            <button class="logout-btn">Logout</button>
        </a>
    </div>

    <div class="content">
        <h2>Welcome</h2>

        <p>
            Use the left navigation panel to manage Users, Roles, Nodes,
            Stocks, Warehouses, Products, and other master data in the system.
        </p>
    </div>

</div>

</body>
</html>