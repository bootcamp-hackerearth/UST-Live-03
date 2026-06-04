<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS Dashboard</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            background: #f5f7fb;
        }

        .container {
            display: flex;
            height: 100vh;
        }


        .sidebar {
            width: 240px;
            background: linear-gradient(180deg, #1e272e, #2f3640);
            color: #fff;
            display: flex;
            flex-direction: column;
        }

        .logo {
            text-align: center;
            font-size: 20px;
            font-weight: 600;
            padding: 20px;
            background: #00a8ff;
        }

        .menu {
            flex: 1;
        }

        .menu a {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 14px 20px;
            color: #dcdde1;
            text-decoration: none;
            transition: 0.3s;
        }

        .menu a:hover {
            background: #00a8ff;
            color: #fff;
            padding-left: 25px;
        }


        .logout-section {
            padding: 15px;
            border-top: 1px solid rgba(255,255,255,0.1);
        }

        .logout-btn {
            width: 100%;
            padding: 10px;
            border: none;
            border-radius: 6px;
            background: #e84118;
            color: white;
            cursor: pointer;
            font-weight: 500;
        }

        .logout-btn:hover {
            background: red;
        }


        .content {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            text-align: center;
        }

        .welcome-box {
            background: white;
            padding: 40px 60px;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        }

        .welcome-box h1 {
            margin: 0;
            color: #2f3640;
        }

        .welcome-box p {
            margin-top: 10px;
            color: #7f8fa6;
            font-size: 14px;
        }

    </style>
</head>

<body>

<div class="container">


    <div class="sidebar">

        <div class="logo">POS System</div>

        <div class="menu">
            <c:forEach var="node" items="${nodes}">
                <a href="${pageContext.request.contextPath}${node.path}">
                    <i class="bi bi-grid-fill"></i>
                    ${node.identifier}
                </a>
            </c:forEach>
        </div>


        <div class="logout-section">
            <form action="${pageContext.request.contextPath}/logout" method="post">
                <button type="submit" class="logout-btn">
                    <i class="bi bi-box-arrow-right"></i> Logout
                </button>
            </form>
        </div>

    </div>


    <div class="content">
        <div class="welcome-box">
            <h1>Welcome to POS Application</h1>
            <p>Manage your operations efficiently and navigate using the sidebar.</p>
        </div>
    </div>

</div>

</body>
</html>