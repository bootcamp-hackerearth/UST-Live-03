<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Home</title>

<!-- Bootstrap CSS -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css">

<style>
body {
    overflow-x: hidden;
    background: #f7f7fb;
}

.sidebar {
    height: 100vh;
    overflow-x: hidden;
    overflow-y: auto;
    position: fixed;
    top: 0;
    left: 0;
    width: 220px;
    background: #2563eb;
    padding-top: 60px;
    display: flex;
    flex-direction: column;
    box-shadow: 8px 0 20px rgba(59, 130, 246, 0.15);
}

.sidebar a {
    color: rgba(255, 255, 255, 0.85);
    padding: 10px 20px;
    display: block;
    text-decoration: none;
    font-weight: 500;
    transition: 0.2s ease;
}

.sidebar a:hover {
    background: rgba(255, 255, 255, 0.15);
    color: #ffffff;
    transform: translateX(3px);
    border-radius: 6px;
}

.sidebar-links {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.content {
    margin-left: 220px;
    padding: 20px;
    background: #f7f7fb; /* matches login page */
}

.logout-container {
    padding: 15px 20px;
    border-top: 1px solid rgba(255, 255, 255, 0.25);
}

.logout-btn {
    width: 100%;
    border-radius: 20px;
    font-weight: 600;

    background: linear-gradient(135deg, #93c5fd, #3b82f6);
    border: none;

    box-shadow: 0 8px 20px rgba(37, 99, 235, 0.25);
    transition: 0.2s ease;
}

.logout-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 25px rgba(37, 99, 235, 0.35);
}

.card {
    border: none;
    border-radius: 14px;
}

.card-body {
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
}
</style>
</head>

<body>
<div class="sidebar">
    <div class="sidebar-links">
        <c:forEach var="node" items="${nodes}">
            <a href="${node.path}">${node.identifier}</a>
        </c:forEach>
    </div>

    <div class="logout-container">
        <form action="/logout" method="post">
            <button class="btn btn-primary logout-btn" type="submit">
                Logout
            </button>
        </form>
    </div>
</div>

<div class="content">

    <div class="container-fluid">
        <div class="card shadow-sm">
            <div class="card-body rounded-pill">
                <h4 class="card-title">Welcome to the dashboard</h4>
                <p class="card-text">
                    You can manage your application features from here. Use the sidebar to navigate through different sections.
                </p>
            </div>
        </div>
    </div>
</div>
</body>
</html>