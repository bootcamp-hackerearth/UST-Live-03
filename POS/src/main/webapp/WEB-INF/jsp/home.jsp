<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>POS Home</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif;
}

body {
    min-height: 100vh;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
    color: #fff;
}

.wrapper {
    display: flex;
}

.sidebar {
    width: 250px;
    min-height: 100vh;

    background: rgba(255,255,255,0.05);
    backdrop-filter: blur(12px);

    border-right: 1px solid rgba(255,255,255,0.1);
    padding: 20px;
}

.brand {
    font-size: 1.3rem;
    font-weight: 600;
    color: #00ffff;
}

.logout-btn {
    background: none;
    border: none;
    color: #aaa;
    cursor: pointer;
    font-size: 18px;
}

.logout-btn:hover {
    color: #fff;
}

.sidebar a {
    display: block;
    padding: 10px;
    margin: 6px 0;

    border-radius: 6px;
    text-decoration: none;
    color: #ccc;

    border: 1px solid transparent;
}

.sidebar a:hover {
    background: rgba(0,255,255,0.1);
    border-color: #00ffff;
    color: #00ffff;
}

.content-area {
    flex-grow: 1;
    padding: 40px;
}

.content-card {
    border-radius: 15px;
    padding: 30px;

    background: rgba(255,255,255,0.05);
    backdrop-filter: blur(12px);

    border: 1px solid rgba(255,255,255,0.2);
    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
}

h5 {
    color: #fff;
    margin-bottom: 10px;
}

p {
    color: #aaa;
}

.alert {
    text-align: center;
    margin-top: 10px;
    color: #aaa;
}
</style>
</head>

<body>

<div class="wrapper">

    <div class="sidebar">

        <div style="display:flex; justify-content:space-between; align-items:center;">
            <div class="brand">POS System</div>

            <form action="/logout" method="post">
                <button type="submit" class="logout-btn">⎋</button>
            </form>
        </div>

        <hr style="margin:15px 0; border-color: rgba(255,255,255,0.1);"/>

        <c:if test="${empty node}">
            <div class="alert">
                No menu items available
            </div>
        </c:if>

        <c:if test="${not empty node}">
            <c:forEach var="n" items="${node}">
                <a href="${n.path}">
                    ➤ ${n.identifier}
                </a>
            </c:forEach>
        </c:if>

    </div>

    <div class="content-area">

        <div class="content-card">

            <h5>Welcome to the POS Management System</h5>

            <p>
                Use the navigation menu on the left to manage users, roles,
                configurations, and other system operations.
            </p>

        </div>

    </div>

</div>

</body>
</html>