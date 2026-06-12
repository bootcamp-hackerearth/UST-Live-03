<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
            color: #333;
            display: flex;
        }

        .sidebar {
            width: 60px;
            background-color: #ffffff;
            height: 100vh;
            position: fixed;
            border-right: 1px solid #ddd;
            box-shadow: 2px 0 6px rgba(0,0,0,0.08);
            display: flex;
            flex-direction: column;
            overflow: hidden;
            transition: width 0.3s ease;
            z-index: 1000;
        }

        .sidebar:hover {
            width: 220px;
        }

        .sidebar-header {
            padding: 18px 0;
            background-color: #007bff;
            color: white;
            font-size: 15px;
            font-weight: bold;
            white-space: nowrap;
            overflow: hidden;
            min-height: 60px;
            display: flex;
            align-items: center;
            flex-shrink: 0;        /* never shrink the header */
        }

        .sidebar-header .icon {
            font-size: 20px;
            min-width: 60px;
            text-align: center;
        }

        .sidebar-header .label {
            opacity: 0;
            transition: opacity 0.2s ease 0.1s;
            white-space: nowrap;
        }

        .sidebar:hover .sidebar-header .label {
            opacity: 1;
        }

        /* scrollable area for menu items */
        .sidebar-scroll {
            flex: 1;
            overflow-y: auto;
            overflow-x: hidden;
        }

        /* hide scrollbar when collapsed, show when expanded */
        .sidebar-scroll::-webkit-scrollbar {
            width: 4px;
        }

        .sidebar-scroll::-webkit-scrollbar-track {
            background: transparent;
        }

        .sidebar-scroll::-webkit-scrollbar-thumb {
            background: #ccc;
            border-radius: 4px;
        }

        .menu-label {
            padding: 14px 0 4px 20px;
            font-size: 11px;
            color: #aaa;
            text-transform: uppercase;
            letter-spacing: 1px;
            white-space: nowrap;
            overflow: hidden;
            opacity: 0;
            transition: opacity 0.2s ease 0.1s;
        }

        .sidebar:hover .menu-label {
            opacity: 1;
        }

        .sidebar a {
            display: flex;
            align-items: center;
            padding: 11px 0;
            color: #555;
            text-decoration: none;
            font-size: 13px;
            border-left: 3px solid transparent;
            white-space: nowrap;
            overflow: hidden;
            transition: all 0.2s;
        }

        .sidebar a:hover {
            background-color: #f0f4ff;
            color: #007bff;
            border-left: 3px solid #007bff;
        }

        /* NO icon span — just indent for collapsed state */
        .sidebar a .nav-indent {
            min-width: 60px;
            flex-shrink: 0;
        }

        .sidebar a .nav-text {
            opacity: 0;
            transition: opacity 0.2s ease 0.1s;
        }

        .sidebar:hover a .nav-text {
            opacity: 1;
        }

        .content {
            margin-left: 60px;
            flex: 1;
            min-height: 100vh;
        }

        .navbar {
            background-color: #ffffff;
            padding: 0 30px;
            border-bottom: 1px solid #ddd;
            display: flex;
            justify-content: space-between;
            align-items: center;
            height: 60px;
            box-shadow: 0 1px 4px rgba(0,0,0,0.05);
        }

        .navbar-title {
            font-weight: bold;
            font-size: 16px;
            color: #333;
        }

        .nav-right {
            display: flex;
            align-items: center;
            gap: 16px;
        }

        .user-profile {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            font-weight: bold;
            color: #333;
        }

        .avatar {
            width: 34px;
            height: 34px;
            background-color: #007bff;
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 14px;
            font-weight: bold;
        }

        .btn-logout {
            text-decoration: none;
            color: #dc3545;
            border: 1px solid #dc3545;
            padding: 6px 14px;
            border-radius: 4px;
            font-size: 13px;
            font-weight: bold;
            transition: all 0.2s;
        }

        .btn-logout:hover {
            background-color: #dc3545;
            color: white;
        }

        .main-body {
            padding: 40px 30px;
        }

        .welcome-title {
            font-size: 22px;
            font-weight: bold;
            color: #333;
            margin-bottom: 6px;
        }

        .welcome-sub {
            font-size: 14px;
            color: #888;
        }
    </style>
</head>
<body>

<!-- SIDEBAR -->
<div class="sidebar">

    <div class="sidebar-header">
        <span class="icon">☰</span>
        <span class="label">Control Panel</span>
    </div>

    <!-- scrollable menu area -->
    <div class="sidebar-scroll">

        <div class="menu-label">Main</div>
        <a href="${pageContext.request.contextPath}/">
            <span class="nav-indent"></span>
            <span class="nav-text">🏠 Home Dashboard</span>
        </a>

        <div class="menu-label">Modules</div>
        <c:forEach var="node" items="${nodes}">
            <a href="${pageContext.request.contextPath}${node.path}">
                <span class="nav-indent"></span>
                <span class="nav-text">${node.identifier}</span>
            </a>
        </c:forEach>

    </div>

</div>

<!-- MAIN CONTENT -->
<div class="content">

    <!-- NAVBAR -->
    <div class="navbar">
        <div class="navbar-title">Dashboard Overview</div>

        <div class="nav-right">
            <div class="user-profile">
                <div class="avatar">${fn:substring(user.name, 0, 1)}</div>
                <span>${user.name}</span>
            </div>

            <a href="${pageContext.request.contextPath}/logout"
               class="btn-logout"
               onclick="return confirm('Are you sure you want to logout?');">
                Logout
            </a>
        </div>
    </div>

    <!-- BODY -->
    <div class="main-body">
        <div class="welcome-title">Welcome Back, ${user.name}!</div>
        <div class="welcome-sub">Use the navigation menu on the left to manage modules.</div>
    </div>

</div>

</body>
</html>