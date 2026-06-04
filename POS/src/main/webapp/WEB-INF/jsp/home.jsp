<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS Dashboard</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            background: #f1f5f9;
        }

        /* Layout */
        .wrapper {
            display: flex;
            min-height: 100vh;
        }

        /* Sidebar */
        .sidebar {
            position: fixed;
            left: -260px;
            top: 0;
            width: 260px;
            height: 100vh;
            background: linear-gradient(180deg, #0f766e, #022c43);
            padding: 25px 15px;
            display: flex;
            flex-direction: column;
            transition: 0.3s ease;
            box-shadow: 5px 0 20px rgba(0,0,0,0.2);
        }

        .sidebar.active {
            left: 0;
        }

        .sidebar h2 {
            text-align: center;
            color: #ffffff;
            margin-bottom: 30px;
            font-weight: 600;
        }

        .menu {
            flex-grow: 1;
            overflow-y: auto;
            overflow-x: hidden;
        }

        .menu::-webkit-scrollbar {
            width: 6px;
        }

        .menu::-webkit-scrollbar-thumb {
            background: rgba(255,255,255,0.3);
            border-radius: 10px;
        }

        .menu::-webkit-scrollbar-track {
            background: transparent;
        }

        .menu a {
            display: block;
            padding: 12px 15px;
            margin-bottom: 10px;
            color: #d1fae5;
            text-decoration: none;
            border-radius: 8px;
            font-size: 14px;
            transition: all 0.25s ease;
        }

        .menu a:hover {
            background: rgba(255,255,255,0.15);
            color: #ffffff;
            transform: translateX(6px);
        }

        /* Logout */
        .logout-btn {
            width: 100%;
            padding: 12px;
            background: #ef4444;
            color: #fff;
            border: none;
            border-radius: 8px;
            font-size: 14px;
            cursor: pointer;
            transition: 0.2s;
        }

        .logout-btn:hover {
            background: #dc2626;
        }

        /* Main */
        .main {
            flex: 1;
            width: 100%;
            transition: 0.3s;
        }

        .main.shift {
            margin-left: 260px;
        }

        /* Topbar */
        .topbar {
            height: 60px;
            background: linear-gradient(90deg, #0f766e, #022c43);
            display: flex;
            align-items: center;
            padding: 0 20px;
            color: white;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .toggle-btn {
            font-size: 22px;
            cursor: pointer;
            margin-right: 15px;
        }

        /* Content */
        .content {
            height: calc(100vh - 60px);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .center-box {
            text-align: center;
            padding: 40px;
            background: white;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }

        .center-box h1 {
            font-size: 34px;
            font-weight: 600;
            color: #0f172a;
            margin-bottom: 10px;
        }

        .center-box p {
            color: #64748b;
            font-size: 14px;
        }

    </style>
</head>

<body>
<div class="wrapper">

    <div class="sidebar" id="sidebar">
        <h2>POS System</h2>

        <div class="menu">
            <c:if test="${not empty nodes}">
                <c:forEach var="n" items="${nodes}">
                    <a
                        href="${pageContext.request.contextPath}${n.path}">
                        ${n.identifier}
                    </a>
                </c:forEach>
            </c:if>

            <c:if test="${empty nodes}">
                <a href="#">No Nodes Available</a>
            </c:if>
        </div>

        <div>
            <form action="${pageContext.request.contextPath}/logout" method="post">
                <button type="submit" class="logout-btn">Sign Out</button>
            </form>
        </div>
    </div>

    <div class="main" id="main">
        <div class="topbar">
            <span class="toggle-btn" onclick="toggleSidebar()">☰</span>
            <h3>POS Dashboard</h3>
        </div>

        <div class="content">
            <div class="center-box">
                <h1>POS Dashboard</h1>
                <p>Welcome to your POS system management panel</p>
            </div>
        </div>
    </div>
</div>

<script>
    function toggleSidebar() {
        const sidebar = document.getElementById("sidebar");
        const main = document.getElementById("main");
        sidebar.classList.toggle("active");
        main.classList.toggle("shift");
    }
</script>
</body>
</html>