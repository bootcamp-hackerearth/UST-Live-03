<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Home</title>

    <style>
        body {
            margin: 0;
            height: 100vh;
            font-family: "Segoe UI", Arial, sans-serif;
            background: #f4f6f9;
        }

        .layout {
            display: flex;
            height: 100%;
        }

        .sidebar {
            width: 260px;
            background: #2c3e50;
            padding: 20px;
            box-shadow: 2px 0 8px rgba(0,0,0,0.15);
            display: flex;
            flex-direction: column;
        }

        .sidebar h2 {
            color: #ecf0f1;
            text-align: center;
            margin-bottom: 25px;
            font-size: 20px;
        }

        .menu {
            flex-grow: 1;
        }

        .nav-link {
            display: block;
            padding: 12px 15px;
            margin-bottom: 10px;
            background: #34495e;
            color: #ecf0f1;
            text-decoration: none;
            border-radius: 6px;
            font-size: 16px;
            transition: background-color 0.2s ease;
        }

        .nav-link:hover {
            background: #1abc9c;
        }

        .empty {
            color: #bdc3c7;
            text-align: center;
            margin-top: 20px;
        }

        .logout-form {
            margin-top: auto;
        }

        .logout-btn {
            width: 100%;
            padding: 12px;
            border: none;
            background: #e74c3c;
            color: #fff;
            font-size: 16px;
            border-radius: 6px;
            cursor: pointer;
        }

        .logout-btn:hover {
            background: #c0392b;
        }

        .content {
            flex: 1;
            padding: 40px;
        }

        .content h1 {
            color: #333;
        }

        .content p {
            color: #555;
            font-size: 16px;
        }
    </style>
</head>

<body>

<div class="layout">

    <div class="sidebar">
        <h2>Home</h2>

        <div class="menu">
            <c:if test="${empty nodes}">
                <p class="empty">No modules available</p>
            </c:if>

            <c:forEach var="node" items="${nodes}">
                <a class="nav-link"
                   href="${pageContext.request.contextPath}${node.path}">
                    ${node.identifier}
                </a>
            </c:forEach>
        </div>

        <form class="logout-form"
              action="${pageContext.request.contextPath}/logout"
              method="post">
            <button class="logout-btn" type="submit">Logout</button>
        </form>
    </div>

    <div class="content">
        <h1>Welcome</h1>
        <p>Please select a module from the left navigation menu.</p>


    </div>

</div>

</body>
</html>