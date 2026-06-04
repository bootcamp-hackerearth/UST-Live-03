<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>POS Retail System | Management Console</title>
    <style>
        :root {
            --navy: #0B3C5D;
            --slate-bg: #F4F7F9;
            --border-gray: #D1D5DB;
            --text-primary: #111827;
            --text-secondary: #4B5563;
        }

        body {
            margin: 0;
            height: 100vh;
            font-family: "Inter", "Segoe UI", Tahoma, sans-serif;
            background: var(--slate-bg);
            color: var(--text-primary);
            display: flex;
            flex-direction: column;
            overflow: hidden;
        }

        .header {
            height: 56px;
            background: var(--navy);
            color: white;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 24px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            z-index: 10;
        }

        .header-brand { font-weight: 700; font-size: 1.1rem; letter-spacing: -0.02em; }

        .user-pill {
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: 13px;
        }

        .role-tag {
            background: rgba(255,255,255,0.1);
            border: 1px solid rgba(255,255,255,0.3);
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 11px;
            text-transform: uppercase;
        }

        .main-container {
            display: flex;
            flex: 1;
            overflow: hidden;
        }

        .sidebar {
            width: 260px;
            background: white;
            border-right: 1px solid var(--border-gray);
            display: flex;
            flex-direction: column;
        }

        .nav-wrapper {
            flex: 1;
            overflow-y: auto;
            padding: 24px 0;
        }

        .nav-label {
            font-size: 12px;
            font-weight: 700;
            color: #9CA3AF;
            padding: 0 24px 12px;
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }

        .sidebar a {
            display: block;
            padding: 12px 24px;
            color: var(--text-secondary);
            text-decoration: none;
            font-size: 15px;
            font-weight: 500;
            transition: all 0.2s ease;
        }

        .sidebar a:hover {
            background: #F9FAFB;
            color: var(--navy);
            padding-left: 28px; /* Subtle slide effect */
        }

        .sidebar a.active {
            background: #EFF6FF;
            color: var(--navy);
            font-weight: 700;
            border-right: 4px solid var(--navy);
        }

        .sidebar-footer {
            padding: 20px;
            border-top: 1px solid var(--border-gray);
        }

        .logout-btn {
            display: block;
            text-align: center;
            padding: 12px;
            background: #FFF5F5; /* Lil shade of red */
            color: #C53030;      /* Deep red text for contrast */
            border: 1px solid #FEB2B2;
            border-radius: 8px;
            text-decoration: none;
            font-size: 14px;
            font-weight: 700;
            transition: all 0.2s;
        }

        .logout-btn:hover {
            background: #FEE2E2;
            border-color: #F87171;
            color: #9B2C2C;
        }

        .content {
            flex: 1;
            padding: 40px;
            overflow-y: auto;
        }

        .page-header {
            margin-bottom: 30px;
            border-bottom: 1px solid var(--border-gray);
            padding-bottom: 20px;
        }

        .page-header h1 { margin: 0; font-size: 24px; color: var(--navy); }
        .page-header p { margin: 5px 0 0; color: var(--text-secondary); font-size: 14px; }

        .system-card {
            background: white;
            border: 1px solid var(--border-gray);
            border-radius: 8px;
            padding: 30px;
            max-width: 900px;
            box-shadow: 0 1px 2px rgba(0,0,0,0.05);
        }

        .grid-info {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
            margin-top: 25px;
        }

        .info-box {
            padding: 15px;
            background: #F9FAFB;
            border-radius: 6px;
            border-left: 4px solid var(--navy);
        }

        .info-box label {
            display: block;
            font-size: 11px;
            text-transform: uppercase;
            color: var(--text-secondary);
            margin-bottom: 5px;
            font-weight: 700;
        }

        .info-box span {
            font-size: 15px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<header class="header">
    <div class="header-brand">POS MANAGEMENT SYSTEM</div>
    <div class="user-pill">
        <span>Logged in as: <strong>${userDetails.name}</strong></span>
        <c:forEach items="${userDetails.roles}" var="r">
            <span class="role-tag">${r}</span>
        </c:forEach>
    </div>
</header>

<div class="main-container">
    <nav class="sidebar">
        <div class="nav-wrapper">
            <div class="nav-label">Navigation</div>
            <a href="${pageContext.request.contextPath}/" class="active">Dashboard Home</a>

            <div style="margin: 24px 0 10px;" class="nav-label">Modules</div>
            <c:forEach items="${nodes}" var="node">
                <a href="${pageContext.request.contextPath}${node.path}">
                    ${node.identifier}
                </a>
            </c:forEach>
        </div>

        <div class="sidebar-footer">
            <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Sign Out</a>
        </div>
    </nav>

    <main class="content">
        <div class="page-header">
            <h1>User Home</h1>
            <p>System Overview and Management Console</p>
        </div>

        <div class="system-card">
            <h3 style="margin-top: 0; color: var(--navy);">Welcome, ${userDetails.name}</h3>
            <p style="line-height: 1.6; color: var(--text-secondary);">
                You are currently accessing the POS Retail Management System. This interface provides
                centralized control over inventory, sales data, and warehouse distribution.
                Please select a module from the sidebar to begin managing your data.
            </p>

            <div class="grid-info">
                <div class="info-box">
                    <label>System Status</label>
                    <span>Online / Active</span>
                </div>
                <div class="info-box">
                    <label>Available Modules</label>
                    <span>${nodes.size()} Integrated Nodes</span>
                </div>
                <div class="info-box">
                    <label>Account Level</label>
                    <span>Authorized Personnel</span>
                </div>
                <div class="info-box">
                    <label>Last Login</label>
                    <span id="timestamp">...</span>
                </div>
            </div>
        </div>
    </main>
</div>

<script>
    document.getElementById('timestamp').innerText = new Date().toLocaleString();
</script>

</body>
</html>