<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>User List</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            padding: 30px;
        }

        .container {
            background: #ffffff;
            max-width: 1100px;
            margin: auto;
            padding: 28px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        h2 {
            text-align: center;
            margin-bottom: 22px;
            color: #1e293b;
            font-size: 22px;
            font-weight: 700;
        }

        .top-actions {
            display: flex;
            justify-content: space-between;
            margin-bottom: 18px;
        }

        .btn {
            padding: 8px 14px;
            border-radius: 6px;
            border: none;
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            color: #ffffff;
            background-color: #1e293b;
            transition: background 0.2s ease, box-shadow 0.2s ease;
        }

        .btn:hover {
            background-color: #0f172a;
            box-shadow: 0 6px 18px rgba(15, 23, 42, 0.35);
        }

        .btn-edit {
            background-color: #16a34a;
        }

        .btn-edit:hover {
            background-color: #15803d;
        }

        .btn-delete {
            background-color: #dc2626;
        }

        .btn-delete:hover {
            background-color: #b91c1c;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background-color: #ffffff;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
        }

        th, td {
            padding: 12px;
            border-bottom: 1px solid #e5e7eb;
            text-align: center;
            font-size: 13px;
        }

        th {
            background-color: #1e293b;
            color: #ffffff;
            font-weight: 600;
        }

        tr:nth-child(even) {
            background-color: #f8fafc;
        }

        tr:hover {
            background-color: #e2e8f0;
        }

        .action-group {
            display: flex;
            gap: 10px;
            justify-content: center;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>User List</h2>

    <div class="top-actions">
        <a href="${pageContext.request.contextPath}/" class="btn">Home</a>
    </div>

    <table>
        <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Name</th>
            <th>Phone</th>
            <th>Roles</th>
            <th>Action</th>
        </tr>

        <c:forEach var="user" items="${users}">
            <tr>
                <td>${user.id}</td>
                <td>${user.username}</td>
                <td>${user.name}</td>
                <td>${user.phoneNo}</td>
                <td>${user.roles}</td>

                <td>
                    <div class="action-group">
                        <a href="/user/get?username=${user.username}"
                           class="btn btn-edit">Edit</a>

                        <a href="/user/delete?username=${user.username}"
                           class="btn btn-delete">Delete</a>
                    </div>
                </td>
            </tr>
        </c:forEach>

    </table>
</div>

</body>
</html>