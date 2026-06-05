<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Home</title>
<link rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css">
    <style>
        body {
            margin: 0;
            font-family: 'Poppins', "Segoe UI", Arial, sans-serif;
            background: linear-gradient(120deg, #f5ede6, #e8d8c8);
            display: flex;
            height: 100vh;
            overflow: hidden;
        }

        .sidebar {
            position: fixed;
            top: 0;
            left: 0;
            height: 100vh;
            width: 240px;
            background: #2b1a18;
            padding-top: 80px;
            box-shadow: 6px 0 30px rgba(0, 0, 0, 0.3);
            overflow-y: auto;
            overflow-x: hidden;
        }

        .sidebar::-webkit-scrollbar {
            width: 6px;
        }

        .sidebar::-webkit-scrollbar-track {
            background: #2b1a18;
        }

        .sidebar::-webkit-scrollbar-thumb {
            background: #8a6b5f;
            border-radius: 10px;
        }

        .sidebar::-webkit-scrollbar-thumb:hover {
            background: #b38b7d;
        }

        .sidebar a {
            display: block;
            padding: 16px 26px;
            color: #f1e0d3;
            text-decoration: none;
            font-size: 15px;
            font-weight: 500;
            border-left: 4px solid transparent;
            transition: all 0.3s ease;
        }

        .sidebar a:hover {
            background: #3a2321;
            border-left: 4px solid #ffb38a;
            padding-left: 22px;
            color: #ffffff;
        }

        .top-navbar {
            position: fixed;
            top: 0;
            left: 240px;
            right: 0;
            height: 65px;
            background: #ffffff;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0 35px;
            box-shadow: 0 6px 18px rgba(0,0,0,0.08);
            z-index: 1000;
        }

        .app-title {
            font-size: 22px;
            font-weight: 700;
            color: #2b1a18;
            letter-spacing: 1px;
        }

        .nav-logout {
            background: #b94a48;
            color: white;
            padding: 9px 20px;
            border-radius: 30px;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
            transition: all 0.3s ease;
        }

        .nav-logout:hover {
            background: #8f2f2c;
            color: white;
            text-decoration: none;
            transform: scale(1.05);
        }

        .content {
            margin-left: 240px;
            margin-top: 65px;
            padding: 60px;
            width: calc(100% - 240px);
            overflow-y: auto;
        }

        .dashboard-card {
            background: linear-gradient(135deg, #ffffff, #f9f4ef);
            padding: 50px;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.15);
            max-width: 760px;
            transition: all 0.3s ease;
        }

        .dashboard-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 30px 70px rgba(0,0,0,0.2);
        }

        .dashboard-card h1 {
            margin: 0;
            font-size: 40px;
            color: #2b1a18;
            font-weight: 800;
        }

        .dashboard-card p {
            margin-top: 15px;
            font-size: 17px;
            color: #6b4a46;
        }
    </style>
</head>
<body>
<div class="top-navbar">
    <div class="nav-left">
        <span class="app-title">POS SYSTEM</span>
    </div>
    <div class="nav-right">
        <a href="/login?logout" class="nav-logout">Logout</a>
    </div>
</div>
<div class="sidebar">
    <c:forEach items="${nodes}" var="nav">
        <a href="${nav.path}">
            ${nav.identifier}
        </a>
    </c:forEach>
</div>
<div class="content">
    <div class="dashboard-card">
        <h1>Home</h1>
        <p>Welcome to POS Application.</p>
    </div>
</div>
</body>
</html>