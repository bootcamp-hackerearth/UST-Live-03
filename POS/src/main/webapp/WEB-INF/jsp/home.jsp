<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS System - Dashboard</title>

    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
        }

        .header {
            background-color: #2c3e50;
            color: white;
            padding: 15px 30px;
            font-size: 22px;
            font-weight: bold;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .logout-btn {
            background-color: #e74c3c;
            color: white;
            padding: 8px 16px;
            text-decoration: none;
            font-size: 14px;
            border-radius: 4px;
            font-weight: normal;
        }

        .logout-btn:hover {
            background-color: #c0392b;
        }

        .container {
            display: flex;
            height: calc(100vh - 60px);
        }

        .sidebar {
            width: 220px;
            background-color: #34495e;
            padding-top: 20px;
        }

        .sidebar a {
            display: block;
            padding: 12px 20px;
            color: #ecf0f1;
            text-decoration: none;
            font-size: 15px;
        }

        .sidebar a:hover {
            background-color: #1abc9c;
        }

        .content {
            flex-grow: 1;
            padding: 30px;
        }

        .card {
            background-color: white;
            padding: 25px;
            border-radius: 6px;
            max-width: 700px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }

        .card h2 {
            margin-top: 0;
        }
    </style>
</head>

<body>

<div class="header">
    <span>POS System Dashboard</span>

    <!-- Logout button on right -->
    <a href="${pageContext.request.contextPath}/logout" class="logout-btn">
        Logout
    </a>
</div>

<div class="container">

    <div class="sidebar">
        <c:forEach var="node" items="${nodes}">
            <a href="${node.path}">
                ${node.identifier}
            </a>
        </c:forEach>
    </div>

    <div class="content">
        <div class="card">
            <h2>Welcome to POS System</h2>
            <p>
                Use the navigation menu on the left to manage users, roles, and nodes.
            </p>
            <ul>
                <li>Add new records</li>
                <li>Edit existing records</li>
                <li>Delete entries</li>
                <li>View detailed listings</li>
            </ul>
        </div>
    </div>

</div>

</body>
</html>