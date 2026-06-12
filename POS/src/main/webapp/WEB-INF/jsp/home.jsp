<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>POS Dashboard</title>

<style>
body {
    margin: 0;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI",
                 Roboto, Arial, sans-serif;
    height: 100vh;
    overflow: hidden;

    background: linear-gradient(135deg, #dbeafe, #c7d2fe, #e0f2fe);
    color: #1e293b;
}

.header {
    height: 62px;
    background: rgba(30, 41, 59, 0.85);
    backdrop-filter: blur(12px);
    display: flex;
    align-items: center;
    padding: 0 20px;
    position: relative;
    color: #e2e8f0;
    border-bottom: 1px solid rgba(148,163,184,0.3);
}

.hamburger {
    font-size: 24px;
    cursor: pointer;
    margin-right: 15px;
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 10px;
    background: rgba(148,163,184,0.2);
    transition: 0.2s ease;
}

.hamburger:hover {
    background: rgba(148,163,184,0.35);
}

.header h3 {
    font-weight: 600;
    color: #e2e8f0;
}

.logout-top {
    position: absolute;
    right: 20px;
}

.logout-btn-top {
    padding: 8px 14px;
    border: none;
    border-radius: 10px;
    cursor: pointer;
    font-size: 13px;
    font-weight: 600;

    color: #fff;
    background: linear-gradient(135deg, #f43f5e, #ef4444);
    transition: 0.25s ease;
}

.logout-btn-top:hover {
    transform: translateY(-2px);
}

.sidebar {
    position: fixed;
    top: 0;
    left: -270px;
    width: 250px;
    height: 100%;
    background: rgba(30, 41, 59, 0.92);
    backdrop-filter: blur(16px);
    transition: 0.3s ease;
    padding-top: 20px;
    z-index: 4;
    border-right: 1px solid rgba(148,163,184,0.25);

    overflow-y: auto;
}

.sidebar.active {
    left: 0;
}

.sidebar h2 {
    text-align: center;
    color: #e2e8f0;
    margin-bottom: 20px;
    font-weight: 700;
}

.sidebar a {
    display: block;
    padding: 12px 18px;
    color: #cbd5e1;
    text-decoration: none;
    font-weight: 700;
    letter-spacing: 0.3px;
    border-left: 3px solid transparent;
    transition: 0.25s ease;
}

.sidebar a:hover {
    background: rgba(59,130,246,0.25);
    border-left: 3px solid #60a5fa;
    color: #ffffff;
}

.content {
    padding: 40px;
    transition: margin-left 0.3s ease;
}

.content.shift {
    margin-left: 250px;
}

.card {
    background: rgba(30, 41, 59, 0.75);
    padding: 40px;
    border-radius: 18px;
    max-width: 650px;
    margin: 90px auto;
    text-align: center;

    border: 1px solid rgba(148,163,184,0.3);
    box-shadow: 0 20px 40px rgba(0,0,0,0.25);
    color: #e2e8f0;
}

.card h2 {
    font-size: 22px;
    margin-bottom: 10px;
}

.card p {
    color: #cbd5e1;
    font-size: 14px;
}
</style>
</head>

<body>

<div id="sidebar" class="sidebar">
    <h2>POS Menu</h2>

    <c:forEach var="node" items="${nodes}">
        <a href="${pageContext.request.contextPath}${node.path}">
            ${node.identifier}
        </a>
    </c:forEach>
</div>

<div class="header">
    <span class="hamburger" onclick="toggleSidebar()">☰</span>
    <h3>POS Dashboard</h3>

    <div class="logout-top">
        <form action="${pageContext.request.contextPath}/logout" method="post">
            <button type="submit" class="logout-btn-top">
                Logout
            </button>
        </form>
    </div>
</div>

<div id="content" class="content">
    <div class="card">
        <h2>Welcome to the POS Application 🧾</h2>
        <p>Manage Users, Roles, and Nodes efficiently.</p>
        <p>Use the sidebar menu to navigate.</p>
    </div>
</div>

<script>
function toggleSidebar() {
    document.getElementById("sidebar").classList.toggle("active");
    document.getElementById("content").classList.toggle("shift");
}
</script>

</body>
</html>