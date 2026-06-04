<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS Retail Management | Home</title>
    <style>
        body {
            margin: 0;
            height: 100vh;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: #F4F5F7;
            color: #1F2937;
            overflow: hidden;
        }

        .header {
            height: 64px;
            background: #0B3C5D;
            color: #FFFFFF;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 32px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            position: relative;
            z-index: 10;
        }

        .header-left {
            font-size: 20px;
            font-weight: 600;
            letter-spacing: 0.8px;
        }

        .logout-btn {
            background: rgba(255, 255, 255, 0.1);
            color: #FFFFFF;
            text-decoration: none;
            padding: 8px 18px;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 600;
            border: 1px solid rgba(255, 255, 255, 0.4);
            transition: all 0.2s ease;
        }

        .logout-btn:hover {
            background: #FFFFFF;
            color: #0B3C5D;
        }

        .container {
            display: flex;
            height: calc(100vh - 64px);
        }

        .sidebar {
            width: 260px;
            background: #FFFFFF;
            padding-top: 24px;
            border-right: 1px solid #E5E7EB;
            flex-shrink: 0;
        }

        .sidebar-title {
            font-size: 11px;
            font-weight: 700;
            color: #9CA3AF;
            padding: 0 24px 12px;
            text-transform: uppercase;
            letter-spacing: 1.2px;
        }

        .sidebar a {
            display: flex;
            align-items: center;
            padding: 12px 24px;
            margin: 2px 12px;
            border-radius: 8px;
            color: #4B5563;
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
            transition: all 0.2s;
        }

        .sidebar a:hover {
            background: #F3F4F6;
            color: #0B3C5D;
        }

        .sidebar a.active {
            background: #F3F4F6;
            color: #0B3C5D;
        }

        .sidebar hr {
            border: none;
            height: 1px;
            background: #E5E7EB;
            margin: 16px 24px;
        }

        .content {
            flex: 1;
            padding: 40px;
            overflow-y: auto;
            display: flex;
            flex-direction: column;
        }

        .content-card {
            background: #FFFFFF;
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
            max-width: 900px;
            width: 100%;
            border-top: 4px solid #0B3C5D;
        }

        h2 {
            margin: 0 0 12px 0;
            font-size: 24px;
            font-weight: 700;
            color: #111827;
        }

        p {
            color: #6B7280;
            font-size: 15px;
            line-height: 1.6;
            margin-bottom: 0;
        }

        .toast {
            position: fixed;
            top: 24px;
            right: 24px;
            min-width: 280px;
            padding: 16px 20px;
            background: #16A34A; /* Success Green */
            color: #FFFFFF;
            border-radius: 12px;
            font-size: 14px;
            font-weight: 600;
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
            z-index: 9999;
            animation: slideIn 0.3s ease-out;
        }

        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }
    </style>
</head>

<body>
    <c:if test="${not empty successToast}">
        <div class="toast">
            ${successToast}
        </div>
    </c:if>

    <div class="header">
        <div class="header-left">POS Retail Management</div>
        <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Logout</a>
    </div>

    <div class="container">

        <div class="sidebar">
            <div class="sidebar-title">Navigation</div>
            <a href="${pageContext.request.contextPath}/" class="active">User Home</a>
            <hr>
            <div class="sidebar-title">Management Modules</div>
            <c:forEach items="${nodes}" var="node">
                <a href="${pageContext.request.contextPath}${node.path}">
                    ${node.identifier}
                </a>
            </c:forEach>
        </div>

        <div class="content">
            <div class="content-card">
                <h2>Welcome to the System</h2>
                <p>
                    Efficiently manage your retail operations. Select a module from the
                    navigation menu on the left to manage users, configure roles, or
                    monitor system nodes.
                </p>
            </div>
        </div>
    </div>
</body>
</html>