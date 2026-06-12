<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<html>
<head>
<title>Home</title>

<style>
body {
    margin: 0;
    font-family: "Segoe UI", Arial, sans-serif;
    background: #F6F7F9;
}

.header {
    height: 60px;
    background: #2B2B2B;
    color: white;
    display: flex;
    align-items: center;
    padding: 0 20px;
    font-weight: 600;
}

.sidebar {
    position: fixed;
    top: 60px;
    left: -260px;
    width: 260px;
    height: calc(100vh - 60px);
    background: #FFFFFF;
    border-right: 1px solid #E5E7EB;
    transition: 0.3s;

    overflow-y: auto;
}


.sidebar.active {
    left: 0;
}

.sidebar a {
    display: block;
    padding: 12px 18px;
    text-decoration: none;
    color: #111827;
    border-bottom: 1px solid #F3F4F6;
}

.sidebar a:hover {
    background: #F3F4F6;
}
.content {
    padding: 30px;
    transition: margin-left 0.3s;
}

.content.shift {
    margin-left: 260px;
}

.welcome-box {
    background: #FFFFFF;
    padding: 30px;
    border-radius: 12px;
    border: 1px solid #E5E7EB;
    box-shadow: 0 8px 20px rgba(0,0,0,0.05);
    max-width: 700px;
}


.logout-link {
    margin-left: auto;
    background: #B91C1C;
    color: white;
    padding: 6px 12px;
    border-radius: 6px;
    text-decoration: none;
}

.logout-link:hover {
    background: #7F1D1D;
}

.hamburger {
    font-size: 22px;
    cursor: pointer;
    margin-right: 15px;
}
</style>



</head>

<body>

<div class="header">
    <span class="hamburger" onclick="toggleSidebar()">☰</span>
    POS Dashboard

    <a href="/login?logout" class="logout-link">Logout</a>
</div>

<div class="sidebar" id="sidebar">

    <c:forEach var="node" items="${nodes}">
        <a href="${node.path}">${node.identifier}</a>
    </c:forEach>

</div>

<div class="content" id="content">

    <div class="welcome-box">
        <h2>Welcome </h2>
        <p>Select a module from the sidebar to continue managing your POS system.</p>
    </div>

</div>

<script>
function toggleSidebar() {
    const sidebar = document.getElementById("sidebar");
    const content = document.getElementById("content");

    sidebar.classList.toggle("active");
    content.classList.toggle("shift");
}
</script>

</body>
</html>
