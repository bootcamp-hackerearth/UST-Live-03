<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role Management</title>

    <style>
        body {
            margin: 0;
            font-family: 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #232526, #414345);
            color: #fff;
        }

        .topbar {
            height: 56px;
            background: #111827;
            display: flex;
            align-items: center;
            padding: 0 20px;
            justify-content: space-between;
            box-shadow: 0 2px 10px rgba(0,0,0,0.3);
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
            background: #4facfe;
            margin: 5px 0;
            border-radius: 2px;
        }
        .logout-btn {
            background: linear-gradient(to right, #ff4b2b, #ff416c);
            border: none;
            color: white;
            padding: 8px 16px;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
        }

        .logout-btn:hover {
            opacity: 0.9;
        }
        .sidebar {
            position: fixed;
            top: 56px;
            left: -240px;
            width: 240px;
            height: calc(100vh - 56px);

            background: #111827;
            transition: left 0.3s ease;
            box-shadow: 2px 0 10px rgba(0,0,0,0.3);
        }

        .sidebar.active {
            left: 0;
        }

        .sidebar a {
            display: block;
            padding: 15px 20px;
            color: #d1d5db;
            text-decoration: none;
            font-size: 14px;
            transition: 0.3s;
        }

        .sidebar a:hover {
            background: linear-gradient(to right, #4facfe, #00f2fe);
            color: #fff;
        }
        .content {
            margin-top: 56px;
            padding: 40px;
            text-align: center;
        }

        .welcome {
            font-size: 22px;
            font-weight: 600;
            margin-bottom: 10px;
        }

        .welcome-sub {
            font-size: 14px;
            color: #ccc;
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
    <div class="welcome">Welcome to Role Management</div>
    <div class="welcome-sub">
        Select an option from the menu to continue.
    </div>
</div>

</body>
</html>