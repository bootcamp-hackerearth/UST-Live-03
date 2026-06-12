

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>

    <style>
        body {
            margin: 0;
            font-family: Arial, Helvetica, sans-serif;
            background-color: #eef2f7;
        }

        .layout {
            display: flex;
            height: 100vh;
        }

        /* Sidebar */
        .sidebar {
            width: 220px;
            background-color: #2c3e50;
            color: white;
        }

        .sidebar h3 {
            text-align: center;
            padding: 19px 0;
            margin: 0;
            border-bottom: 1px solid #3d566e;
            font-weight: normal;
        }

        .menu {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .menu a {
            display: block;
            padding: 12px 20px;
            color: white;
            text-decoration: none;
            font-size: 18px;
        }

        .menu a:hover {
            background-color: #34495e;
        }

        /* Main section */
        .main {
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        .header {
            height: 55px;
            background-color: #ffffff;
            border-bottom: 1px solid #ddd;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 20px;
            color: #2c3e50;
            font-weight: bold;
        }

        /* Logout button */
        .logout-btn {
            background-color: #e74c3c;
            color: white;
            border: none;
            padding: 8px 14px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
        }

        .logout-btn:hover {
            background-color: #c0392b;
        }

        /* Center content */
        .content {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 40px;
            font-weight: bold;
            color: #2c3e50;
        }
    </style>
</head>

<body>

<div class="layout">

    <!-- Sidebar -->
    <div class="sidebar">
        <h3>Admin Panel</h3>

        <ul class="menu">
            <c:forEach var="node" items="${node}">
                <li>
                    <a href="${node.path}">
                        ${node.identifier}
                    </a>
                </li>
            </c:forEach>
        </ul>
    </div>

    <!-- Main Content -->
    <div class="main">

        <!-- Header with Logout -->
        <div class="header">
            <span>Dashboard</span>

            <!-- ✅ Logout Form -->
            <form action="${pageContext.request.contextPath}/logout" method="post">
                <input type="hidden"
                       name="${_csrf.parameterName}"
                       value="${_csrf.token}" />
                <button type="submit" class="logout-btn">Logout</button>
            </form>
        </div>

        <!-- Centered Text -->
        <div class="content">
            Welcome to My POS Application
        </div>

    </div>

</div>

</body>
</html>
