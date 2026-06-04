<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Home</title>

    <style>
        body {
            margin: 0;
            font-family: Arial, Helvetica, sans-serif;
            background: #f7f3f7;
            color: #fff;
        }


        .navbar {
            height: 55px;
            background: #151515;
            display: flex;
            align-items: center;
            padding: 0 15px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.6);
        }

        .menu-btn {
            font-size: 24px;
            cursor: pointer;
            color: #fff;
        }

        .navbar-title {
            margin-left: 15px;
            font-size: 18px;
            font-weight: 600;
        }

        #menuToggle {
            display: none;
        }

        .side-menu {
            position: fixed;
            top: 55px;
            left: 0;
            width: 260px;
            height: calc(100vh - 55px);
            background: #1c1c1c;
            transform: translateX(-100%);
            transition: transform 0.3s ease;
            padding: 15px;
            box-shadow: 4px 0 15px rgba(0,0,0,0.8);
        }

        #menuToggle:checked ~ .side-menu {
            transform: translateX(0);
        }

        .side-menu h4 {
            margin-top: 0;
            margin-bottom: 15px;
            color: #9b59ff;
        }

        .side-menu a {
            display: block;
            padding: 10px 12px;
            margin-bottom: 8px;
            text-decoration: none;
            color: #ddd;
            border-radius: 6px;
            transition: background 0.2s;
        }

        .side-menu a:hover {
            background: #2d2d2d;
            color: #fff;
        }

        .content {
            padding: 40px 20px;
            text-align: center;
        }

        .content h1 {
            font-size: 28px;
            margin-bottom: 10px;
            color: #9b59ff;
        }

        .content p {
            color: #bbb;
            font-size: 16px;
        }

        .navbar-logout {
            margin-left: auto;
            text-decoration: none;
            color: #fff;
            font-size: 14px;
            padding: 6px 14px;
            border-radius: 6px;
            background: #9b59ff;
            transition: background 0.2s ease;
        }

        .navbar-logout:hover {
            background: #7d3cff;
        }
    </style>
</head>

<body>
    <div class="navbar">
        <label for="menuToggle" class="menu-btn">&#9776;</label>
        <div class="navbar-title">POS Management</div>
        <a href="/logout" class="navbar-logout">Logout</a>
    </div>

    <input type="checkbox" id="menuToggle">
    <div class="side-menu">
        <h4>Menu</h4>
        <c:forEach var="node" items="${nodes}">
            <a href="${node.path}">
                ${node.identifier}
            </a>
        </c:forEach>
    </div>
    <div class="content">
        <h1> Welcome ${name}</h1>
        <p>POS Appplication Dashboard </p>
    </div>
</body>
</html>
