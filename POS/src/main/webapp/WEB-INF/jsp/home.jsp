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
            background: #f1f5f9;
            color: #1e293b;
        }

        /* Layout */
        .wrapper {
            display: flex;
            min-height: 100vh;
        }

        /* Sidebar */
        .sidebar {
            width: 250px;
            background: #1e293b;
            padding: 25px 15px;
            display: flex;
            flex-direction: column;
        }

        .menu {
            flex-grow: 1;
        }

        .sidebar h2 {
            text-align: center;
            color: #ffffff;
            margin-bottom: 30px;
            font-weight: 600;
        }

        .menu a {
            display: block;
            padding: 12px 15px;
            margin-bottom: 10px;
            color: #cbd5e1;
            text-decoration: none;
            border-radius: 6px;
            font-size: 14px;
            transition: 0.2s;
        }

        .menu a:hover {
            background: #334155;
            color: #ffffff;
        }

        /* Main Content */
        .content {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .center-box {
            text-align: center;
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