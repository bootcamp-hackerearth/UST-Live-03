<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role Management</title>

    <style>
        body {
            margin: 0;
            font-family: 'Segoe UI', sans-serif;
            background: #eef2f7;
        }

        .header {
            background: #1e88e5;
            padding: 15px 25px;
            color: white;
            font-size: 20px;
            font-weight: bold;
        }

        .container {
            display: flex;
            height: calc(100vh - 55px);
        }

        .left {
            width: 70%;
            padding: 20px;
        }

        .right {
            width: 30%;
            background: white;
            border-left: 1px solid #ddd;
            padding: 20px;
        }

        .role-card {
            background: white;
            border-radius: 8px;
            padding: 12px 15px;
            margin-bottom: 10px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 6px rgba(0,0,0,0.05);
            transition: 0.2s;
        }

        .role-card:hover {
            background: #e3f2fd;
        }

        .role-info {
            font-size: 14px;
            font-weight: 500;
            color: #333;
        }

        .actions {
            display: flex;
            gap: 6px;
        }

        .btn {
            padding: 5px 10px;
            border-radius: 5px;
            font-size: 12px;
            border: none;
            cursor: pointer;
            text-decoration: none;
        }

        .edit {
            background: #fff3cd;
            color: #856404;
        }

        .delete {
            background: #f8d7da;
            color: #721c24;
        }

        .big-btn {
            width: 100%;
            padding: 12px;
            margin-bottom: 10px;
            border-radius: 6px;
            border: none;
            cursor: pointer;
            font-weight: bold;
            font-size: 14px;
        }

        .add {
            background: #28a745;
            color: white;
        }

        .home {
            background: #1e88e5;
            color: white;
        }

        .empty {
            text-align: center;
            color: #777;
            margin-top: 50px;
        }

        h3 {
            margin-bottom: 15px;
        }
    </style>
</head>

<body>

<div class="header">
    POS - Role Management
</div>

<div class="container">

    <div class="left">

        <c:if test="${empty roles}">
            <div class="empty">No roles found</div>
        </c:if>

        <c:forEach var="role" items="${roles}">
            <div class="role-card">

                <div class="role-info">
                    ${role.identifier}
                </div>

                <div class="actions">
                    <a href="/role/get?identifier=${role.identifier}" class="btn edit">
                        Edit
                    </a>

                    <a href="/role/delete?identifier=${role.identifier}"
                       onclick="return confirm('Delete this role?')"
                       class="btn delete">
                        Delete
                    </a>
                </div>

            </div>
        </c:forEach>

    </div>

    <div class="right">

        <h3>Quick Actions</h3>

        <button class="big-btn add" onclick="window.location.href='/role/add'">
            + Add Role
        </button>

        <button class="big-btn home" onclick="window.location.href='/'">
            Go to Dashboard
        </button>

    </div>

</div>

</body>
</html>