<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background-color: #ffffff;
        }

        .container {
            width: 95%;
            max-width: 1000px;
            margin: 40px auto;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            padding: 18px;
        }

        .app-title {
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #14b8a6; /* light teal */
            margin-bottom: 4px;
        }

        h2 {
            text-align: center;
            font-size: 24px;
            margin-bottom: 10px;
        }

        .list-actions {
            display: flex;
            justify-content: flex-end;
            gap: 8px;
            margin-bottom: 12px;
        }

        .home-btn {
            padding: 7px 16px;
            background: #ffffff;
            color: teal;
            text-decoration: none;
            border-radius: 18px;
            font-size: 13px;
            font-weight: 600;
            border: 1px solid teal;
        }

        .msg {
            margin: 0 auto 12px;
            max-width: 1000px;
            padding: 8px;
            border-radius: 6px;
            background-color: #fee2e2;
            color: #991b1b;
            text-align: center;
            font-size: 13px;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
        }

        th, td {
            padding: 12px;
            text-align: center;
        }

        th {
            background-color: #f1f5f9;
            font-weight: 700;
            border-bottom: 1px solid #e5e7eb;
        }

        td {
            border-bottom: 1px solid #e5e7eb;
        }

        tr {
            line-height: 1.3;
        }

        tbody tr:hover {
            background-color: #f8fafc;
        }

        .action-link {
            padding: 6px 12px;
            border-radius: 18px;
            font-size: 12px;
            font-weight: 600;
            color: #ffffff;
            text-decoration: none;
        }

        .edit {
            background-color: teal;
        }

        .delete {
            background-color: #ef4444;
            margin-left: 6px;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="app-title">POS Application</div>

    <h2>User Management</h2>

    <div class="list-actions">
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>
    </div>
    <c:if test="${not empty message}">
        <div class="msg">${message}</div>
    </c:if>

    <c:if test="${empty users}">
        <div style="text-align:center; padding:20px; font-size:14px;">
            No users found
        </div>
    </c:if>

    <c:if test="${not empty users}">
        <table>
            <thead>
                <tr>
                    <th>Email</th>
                    <th>Name</th>
                    <th>Phone</th>
                    <th>Roles</th>
                    <th>Action</th>
                </tr>
            </thead>

            <tbody>
                <c:forEach var="user" items="${users}">
                    <tr>
                        <td>${user.username}</td>
                        <td>${user.name}</td>
                        <td>${user.phoneNo}</td>
                        <td>${user.roles}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/user/get?username=${user.username}"
                               class="action-link edit">Edit</a>

                            <a href="${pageContext.request.contextPath}/user/delete?username=${user.username}"
                               class="action-link delete"
                               onclick="return confirm('Are you sure you want to delete this user?');">
                               Delete
                            </a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>

</body>
</html>
