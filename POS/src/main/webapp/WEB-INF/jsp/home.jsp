<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS</title>

    <base href="${pageContext.request.contextPath}/">

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

    <style>
        :root {
            --bg: #f6fff8;
            --panel: #ffffff;

            --text: #1f2937;
            --muted: #6b7280;

            --primary: #28a745;
            --primary-hover: #218838;

            --accent: #ffc107;

            --danger: #dc3545;

            --border: #e5e7eb;

            --radius: 14px;

            --topbar-h: 64px;
            --sidebar-w: 260px;

            --shadow: 0 10px 30px rgba(0,0,0,0.08);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Inter', sans-serif;
        }

        body {
            background: var(--bg);
            color: var(--text);
        }

        a {
            text-decoration: none;
            color: inherit;
        }

        /* TOPBAR */
        .topbar {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            height: var(--topbar-h);

            display: flex;
            justify-content: space-between;
            align-items: center;

            padding: 0 20px;

            background: white;
            border-bottom: 1px solid var(--border);

            z-index: 1000;
        }

        .topbar-left {
            display: flex;
            align-items: center;
            gap: 14px;
        }

        .title {
            font-weight: 600;
            font-size: 16px;
        }

        /* MENU */
        .menu {
            width: 40px;
            height: 36px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            gap: 5px;
            cursor: pointer;
            padding: 8px;
            border-radius: 10px;
        }

        .menu:hover {
            background: #f1f5f9;
        }

        .menu div {
            height: 2px;
            background: #333;
        }

        /* BUTTON */
        .btn-danger {
            background: var(--danger);
            border-radius: 10px;
            padding: 8px 14px;
        }

        /* SIDEBAR */
        .sidebar {
            position: fixed;
            top: var(--topbar-h);
            left: 0;

            width: var(--sidebar-w);
            height: calc(100vh - var(--topbar-h));

            background: #ffffff;
            border-right: 1px solid var(--border);

            transform: translateX(-100%);
            transition: 0.3s;

            padding: 16px 10px;
            z-index: 999;
        }

        .sidebar.active {
            transform: translateX(0);
        }

        .sidebar a {
            display: block;
            padding: 12px 14px;
            margin: 6px 8px;
            border-radius: 10px;

            font-size: 14px;
            color: var(--muted);
            transition: 0.2s;
        }

        .sidebar a:hover {
            background: #f1f5f9;
            color: var(--primary);
            transform: translateX(4px);
        }

        /* CONTENT */
        .content {
            margin-top: var(--topbar-h);
            padding: 30px;
            transition: margin-left 0.3s;
        }

        .sidebar.active ~ .content {
            margin-left: var(--sidebar-w);
        }

        /* CARD */
        .card {
            max-width: 800px;
            padding: 24px;

            border-radius: var(--radius);
            background: var(--panel);
            border: 1px solid var(--border);

            box-shadow: var(--shadow);
        }

        .h1 {
            font-size: 22px;
            font-weight: 700;
            margin-bottom: 8px;
        }

        .sub {
            font-size: 14px;
            color: var(--muted);
        }

        .highlight {
            color: var(--primary);
            font-weight: 600;
        }
    </style>

    <script>
        function toggleMenu() {
            document.getElementById("sidebar").classList.toggle("active");
        }
    </script>
</head>

<body>

<!-- TOPBAR -->
<div class="topbar">
    <div class="topbar-left">
        <div class="menu" onclick="toggleMenu()">
            <div></div>
            <div></div>
            <div></div>
        </div>
        <div class="title">POS</div>
    </div>

    <form action="logout" method="post" style="margin:0;">
        <button type="submit" class="btn btn-danger">Logout</button>
    </form>
</div>

<!-- SIDEBAR -->
<div class="sidebar" id="sidebar">
    <c:forEach var="node" items="${nodes}">
        <a href="${node.path}">
            ${node.identifier}
        </a>
    </c:forEach>
</div>

<!-- CONTENT -->
<div class="content">
    <div class="card">
        <div class="h1">Welcome</div>
        <div class="sub">
            Select a <span class="highlight">module</span> from the sidebar to manage roles and permissions.
        </div>
    </div>
</div>

</body>
</html>