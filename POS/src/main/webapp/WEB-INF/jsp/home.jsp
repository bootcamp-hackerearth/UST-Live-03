<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>POS | Home</title>

<style>
body {
    margin: 0;
    font-family: "Segoe UI", Arial, sans-serif;
    background: #020617;
}

.topbar {
    height: 55px;
    background: #0f172a;
    color: #e2e8f0;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 25px;
    box-shadow: 0 2px 10px rgba(0,0,0,0.4);
}

.topbar h2 {
    margin: 0;
    font-size: 18px;
}

.logout-btn {
    padding: 8px 14px;
    background: #dc2626;
    border: none;
    border-radius: 6px;
    color: white;
    font-size: 13px;
    cursor: pointer;
}

.logout-btn:hover {
    background: #b91c1c;
}

.layout {
    display: flex;
    height: calc(100vh - 55px);
}

.sidebar {
    width: 240px;
    background: #020617;
    padding: 20px;
    border-right: 1px solid #1e293b;
}

.sidebar-title {
    color: #94a3b8;
    font-size: 13px;
    margin-bottom: 15px;
}

.nav-link {
    display: block;
    padding: 12px;
    margin-bottom: 10px;
    background: #0f172a;
    color: #e5e7eb;
    text-decoration: none;
    border-radius: 8px;
    font-size: 14px;
    transition: 0.2s;
}

.nav-link:hover {
    background: #0f766e;
}

.empty {
    color: #64748b;
    font-size: 13px;
    text-align: center;
    margin-top: 20px;
}

.content {
    flex: 1;
    background: #f8fafc;
    padding: 30px;
}

.content h1 {
    margin-top: 0;
    color: #0f172a;
}

.content p {
    color: #475569;
    font-size: 15px;
}

</style>
</head>

<body>

<div class="topbar">
    <h2>POS SYSTEM</h2>

    <form action="${pageContext.request.contextPath}/logout" method="post">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
</div>

<div class="layout">

    <div class="sidebar">
        <div class="sidebar-title">MODULES</div>

        <c:if test="${empty nodes}">
            <p class="empty">No modules available</p>
        </c:if>

        <c:forEach var="node" items="${nodes}">
            <a class="nav-link"
               href="${pageContext.request.contextPath}${node.path}">
                ${node.identifier}
            </a>
        </c:forEach>
    </div>

    <div class="content">
        <h1>Welcome to POS</h1>
        <p>Select a module from the left menu to continue.</p>

    </div>
</div>
</body>
</html>
