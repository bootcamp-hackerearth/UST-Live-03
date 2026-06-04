<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
            color: #333;
            display: flex;
        }

        .sidebar {
            width: 240px;
            background-color: #ffffff;
            height: 100vh;
            border-right: 1px solid #ddd;
            position: fixed;
            box-shadow: 2px 0 5px rgba(0,0,0,0.05);
        }

        .sidebar-header {
            padding: 20px;
            text-align: center;
            background-color: #007bff;
            color: white;
            font-size: 1.2em;
            font-weight: bold;
        }

        .menu-label {
            padding: 15px 20px 5px;
            font-size: 12px;
            color: #999;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .sidebar a {
            display: block;
            padding: 12px 20px;
            color: #555;
            text-decoration: none;
            border-bottom: 1px solid #f9f9f9;
            transition: background 0.2s;
        }

        .sidebar a:hover {
            background-color: #e9ecef;
            color: #007bff;
        }

        .content {
            margin-left: 240px;
            flex: 1;
        }

        .navbar {
            background-color: #fff;
            padding: 10px 30px;
            border-bottom: 1px solid #ddd;
            display: flex;
            justify-content: space-between;
            align-items: center;
            height: 60px;
        }

        .nav-right {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .user-profile {
            display: flex;
            align-items: center;
            font-weight: bold;
        }

        .avatar {
            width: 32px;
            height: 32px;
            background-color: #007bff;
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 10px;
            font-size: 14px;
        }

        .btn-logout {
            text-decoration: none;
            color: #dc3545;
            border: 1px solid #dc3545;
            padding: 6px 15px;
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
            padding: 30px;
        }

        .welcome-card {
            background-color: #fff;
            padding: 25px;
            border-radius: 8px;
            border: 1px solid #ddd;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
        }

        .welcome-card h2 {
            margin-top: 0;
            color: #007bff;
        }

        .welcome-card p {
            color: #666;
            line-height: 1.6;
        }
    </style>
</head>
<body>

    <div class="sidebar">
        <div class="sidebar-header">
            Control Panel
        </div>

        <div class="menu-label">Main</div>
        <a href="${pageContext.request.contextPath}/">Home Dashboard</a>

        <div class="menu-label">Modules</div>
        <c:forEach var="node" items="${nodes}">
            <a href="${pageContext.request.contextPath}${node.path}">
                ${node.identifier}
            </a>
        </c:forEach>
    </div>

    <div class="content">
        <div class="navbar">
            <div style="font-weight: bold; font-size: 18px;">Dashboard Overview</div>

            <div class="nav-right">
                <div class="user-profile">
                    <div class="avatar">
                        ${fn:substring(user.name,0,1)}
                    </div>
                    <span>${user.name}</span>
                </div>

                <a href="${pageContext.request.contextPath}/logout"
                   class="btn-logout"
                   onclick="return confirm('Are you sure you want to logout?');">
                   Logout
                </a>
            </div>
        </div>

        <div class="main-body">
            <div class="welcome-card">
                <h2>Welcome Back, ${user.name}!</h2>
                <p>Use the navigation menu on the left to manage system nodes, user profiles, and other available modules.</p>
            </div>
        </div>
    </div>

</body>
</html>