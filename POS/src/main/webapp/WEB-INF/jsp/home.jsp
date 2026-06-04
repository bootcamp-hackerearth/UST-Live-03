<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS Dashboard</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            background: linear-gradient(135deg, #eef2ff, #f8fafc);
            color: #1e293b;
        }

        .wrapper {
            display: flex;
            min-height: 100vh;
        }

        .sidebar {
            width: 250px;
            background: #ffffff;
            padding: 25px 15px;
            display: flex;
            flex-direction: column;
            border-right: 1px solid #e2e8f0;
        }

        .menu {
            flex-grow: 1;
        }

        .sidebar h2 {
            text-align: center;
            color: #0f172a;
            margin-bottom: 30px;
            font-weight: 600;
        }

        .menu a {
            display: block;
            padding: 12px 15px;
            margin-bottom: 10px;
            color: #3730a3;
            text-decoration: none;
            border-radius: 6px;
            font-size: 14px;
            background: #e0e7ff;
            transition: 0.2s;
        }

        .menu a:hover {
            background: #6366f1;
            color: #ffffff;
        }

        .content {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .center-box {
            text-align: center;
            background: #ffffff;
            padding: 30px;
            border-radius: 12px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 8px 20px rgba(0,0,0,0.05);
        }

        .center-box h1 {
            font-size: 34px;
            font-weight: 600;
            color: #0f172a;
            margin-bottom: 10px;
        }

        .center-box p {
            color: #64748b;
            font-size: 14px;
        }

        .logout-btn {
            width: 100%;
            padding: 12px;
            background: #ef4444;
            color: #fff;
            border: none;
            border-radius: 6px;
            font-size: 14px;
            cursor: pointer;
            transition: 0.2s;
        }

        .logout-btn:hover {
            background: #dc2626;
        }

    </style>
</head>

<body>

<div class="wrapper">

    <div class="sidebar">
        <h2>POS System</h2>

        <div class="menu">

            <c:if test="${not empty nodes}">
                <c:forEach var="n" items="${nodes}">
                    <a href="${pageContext.request.contextPath}${n.path}">
                        ${n.identifier}
                    </a>
                </c:forEach>
            </c:if>

            <c:if test="${empty nodes}">
                <a href="#">
                    No Nodes Available
                </a>
            </c:if>

        </div>

            <div style="margin-top: auto;">
                <form action="${pageContext.request.contextPath}/logout" method="post">
                    <button type="submit" class="logout-btn">Sign Out</button>
                </form>
            </div>

    </div>

    <div class="content">
        <div class="center-box">
            <h1>POS Dashboard</h1>
            <p>Welcome to your system management panel</p>
        </div>
    </div>

</div>

</body>
</html>