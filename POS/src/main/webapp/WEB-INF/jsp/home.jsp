<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role Management</title>

    <style>
        body {
            margin: 0;
            font-family: 'Inter', sans-serif;
            background-color: #f5f3ff;
        }

        .topbar {
            height: 56px;
            background-color: #6d28d9;
            display: flex;
            align-items: center;
            padding: 0 16px;
            color: #ffffff;
            justify-content: space-between;
        }

        .topbar-left {
            display: flex;
            align-items: center;
        }

        .menu {
            width: 28px;
            cursor: pointer;
            margin-right: 16px;
        }

        .menu div {
            height: 3px;
            background: #ffffff;
            margin: 5px 0;
        }


        .logout-btn {
            background: #c4b5fd;
            border: none;
            color: #4c1d95;
            padding: 8px 16px;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
        }

        .logout-btn:hover {
            background: #b197fc;
        }

        .sidebar {
            position: fixed;
            top: 56px;
            left: -240px;
            width: 240px;
            height: calc(100vh - 56px);
            background: #ede9fe;
            transition: left 0.3s ease;
        }

        .sidebar.active {
            left: 0;
        }

        .sidebar a {
            display: block;
            padding: 16px 24px;
            color: #5b21b6;
            text-decoration: none;
            font-size: 15px;
            font-weight: 500;
        }

        .sidebar a:hover {
            background: #ddd6fe;
            color: #4c1d95;
        }

        .content {
            position: fixed;
            top: 40%;
            left: 40%;
            transform: translate(-50%, -50%);
            text-align: center;
        }

        .welcome {
            font-size: 23px;
            font-weight: 600;
            color: #4c1d95;
        }

        .welcome-sub {
            margin-top: 4px;
            font-size: 14px;
            color: #6d28d9;
        }
    </style>

    <script>
        function toggleMenu() {
            document.getElementById("sidebar").classList.toggle("active");
        }
    </script>
</head>

<body>


<div class="topbar">
    <div class="topbar-left">
        <div class="menu" onclick="toggleMenu()">
            <div></div>
            <div></div>
            <div></div>
        </div>
        <strong>Role Management</strong>
    </div>


    <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
</div>


<div class="sidebar" id="sidebar">
    <c:forEach var="node" items="${nodes}">
        <a href="${pageContext.request.contextPath}${node.path}">
            ${node.identifier}
        </a>
    </c:forEach>
</div>


<div class="content">
    <div class="welcome">Welcome to the Role Management System</div>
    <div class="welcome-sub">
        Please select an option from the menu.
    </div>
</div>

</body>
</html>