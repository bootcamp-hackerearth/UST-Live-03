<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS Control Center</title>

    <base href="${pageContext.request.contextPath}/">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <style>
        :root {
            /* SOFT ENTERPRISE PALETTE */
            --bg: #f1f4f9;
            --panel: #ffffff;
            --panel-soft: #f6f8fb;

            --primary: #4f46e5;
            --primary-soft: rgba(79,70,229,0.10);

            --text: #111827;
            --muted: #6b7280;

            --border: #e6eaf0;

            --sidebar-w: 290px;
            --topbar-h: 68px;

            --radius: 16px;

            --shadow: 0 6px 18px rgba(15,23,42,0.06);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: "Inter", sans-serif;
        }

        body {
            background: var(--bg);
            color: var(--text);
        }

        a { text-decoration: none; color: inherit; }

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

            padding: 0 22px;

            background: var(--panel);
            border-bottom: 1px solid var(--border);

            z-index: 1000;
        }

        .brand {
            display: flex;
            align-items: center;
            gap: 10px;

            font-weight: 600;
        }

        .dot {
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background: var(--primary);
        }

        .logout {
            background: var(--panel-soft);
            border: 1px solid var(--border);
            color: var(--text);

            padding: 8px 14px;
            border-radius: 12px;

            transition: 0.2s;
        }

        .logout:hover {
            background: var(--primary-soft);
            transform: translateY(-1px);
        }

        /* LAYOUT */
        .layout {
            display: flex;
            margin-top: var(--topbar-h);
        }

        /* SIDEBAR */
        .sidebar {
            width: var(--sidebar-w);
            height: calc(100vh - var(--topbar-h));

            background: var(--panel);
            border-right: 1px solid var(--border);

            padding: 18px;

            overflow-y: auto;
        }

        .sidebar::-webkit-scrollbar {
            width: 5px;
        }

        .sidebar::-webkit-scrollbar-thumb {
            background: #d1d5db;
            border-radius: 10px;
        }

        .section-title {
            font-size: 11px;
            color: var(--muted);
            letter-spacing: 1px;
            text-transform: uppercase;

            margin: 16px 10px 8px;
        }

        .nav-item {
            display: flex;
            align-items: center;
            gap: 12px;

            padding: 12px 14px;
            margin-bottom: 6px;

            border-radius: 12px;

            color: var(--muted);

            transition: 0.2s;
        }

        .nav-item:hover {
            background: var(--panel-soft);
            color: var(--text);
            transform: translateX(4px);
        }

        .nav-item i {
            color: var(--primary);
            font-size: 18px;
        }

        /* CONTENT */
        .content {
            flex: 1;
            padding: 28px;
        }

        /* HERO */
        .hero {
            background: linear-gradient(180deg, var(--panel), var(--panel-soft));
            border: 1px solid var(--border);

            border-radius: var(--radius);

            padding: 24px;

            box-shadow: var(--shadow);

            margin-bottom: 18px;
        }

        .hero h1 {
            font-size: 20px;
            font-weight: 600;
        }

        .hero p {
            color: var(--muted);
            margin-top: 6px;
            font-size: 14px;
        }

        /* GRID */
        .grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
            gap: 14px;
        }

        .cardx {
            background: var(--panel);
            border: 1px solid var(--border);

            border-radius: var(--radius);
            padding: 16px;

            box-shadow: 0 4px 14px rgba(15,23,42,0.04);

            transition: 0.2s;
        }

        .cardx:hover {
            transform: translateY(-3px);
        }

        .icon {
            width: 40px;
            height: 40px;

            display: flex;
            align-items: center;
            justify-content: center;

            border-radius: 12px;

            background: var(--primary-soft);
            color: var(--primary);

            margin-bottom: 10px;
        }

        .cardx h3 {
            font-size: 14px;
            margin-bottom: 4px;
        }

        .cardx p {
            font-size: 13px;
            color: var(--muted);
        }
    </style>
</head>

<body>

<!-- TOPBAR -->
<div class="topbar">
    <div class="brand">
        <div class="dot"></div>
        POS Control Center
    </div>

    <form action="logout" method="post">
        <button class="logout">
            <i class="bi bi-box-arrow-right"></i> Logout
        </button>
    </form>
</div>

<div class="layout">

    <!-- SIDEBAR -->
    <div class="sidebar">

        <div class="section-title">Modules</div>

        <c:forEach var="node" items="${nodes}">
            <a href="${node.path}" class="nav-item">
                <i class="bi bi-grid-1x2-fill"></i>
                ${node.identifier}
            </a>
        </c:forEach>

    </div>

    <!-- CONTENT -->
    <div class="content">

        <div class="hero">
            <h1>Welcome Back</h1>
            <p>Manage POS operations with a structured and lightweight control center.</p>
        </div>

        <div class="grid">

            <div class="cardx">
                <div class="icon"><i class="bi bi-box-seam"></i></div>
                <h3>Inventory</h3>
                <p>Track stock and product lifecycle.</p>
            </div>

            <div class="cardx">
                <div class="icon"><i class="bi bi-building"></i></div>
                <h3>Warehouses</h3>
                <p>Organize storage and logistics.</p>
            </div>

            <div class="cardx">
                <div class="icon"><i class="bi bi-diagram-3"></i></div>
                <h3>Stock Flow</h3>
                <p>Monitor internal stock movement.</p>
            </div>

        </div>

    </div>

</div>

</body>
</html>