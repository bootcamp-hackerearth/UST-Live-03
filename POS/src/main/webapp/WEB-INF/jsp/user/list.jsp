<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Management | POS</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            margin: 0;
            font-family: 'Inter', 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #f6f8fb, #eef2f7);
        }

        .topbar {
            height: 70px;
            background: linear-gradient(135deg, #4f46e5, #6366f1);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 30px;
            color: #fff;
            box-shadow: 0 4px 20px rgba(0,0,0,0.08);
        }

        .topbar-left {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .dashboard-btn {
            background: rgba(255,255,255,0.15);
            border: none;
            color: #fff;
            padding: 8px 14px;
            border-radius: 10px;
            font-size: 13px;
        }

        .dashboard-btn:hover {
            background: rgba(255,255,255,0.25);
        }

        .logout-btn {
            background: rgba(255,255,255,0.15);
            border: none;
            color: #fff;
            padding: 8px 16px;
            border-radius: 10px;
            font-size: 13px;
        }

        .logout-btn:hover {
            background: rgba(255,255,255,0.25);
        }

        .content {
            padding: 40px;
        }

        .page-card {
            max-width: 1000px;
            margin: auto;
            background: #ffffff;
            border-radius: 18px;
            padding: 28px;
            box-shadow: 0 25px 60px rgba(0,0,0,0.08);
        }

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .page-title {
            font-size: 20px;
            font-weight: 600;
            color: #1f2937;
        }

        .primary-btn {
            background: #4f46e5;
            color: #ffffff;
            border: none;
            border-radius: 12px;
            padding: 10px 18px;
            font-size: 13px;
            font-weight: 600;
        }

        .primary-btn:hover {
            background: #4338ca;
        }

        table {
            font-size: 14px;
        }

        thead th {
            background: #f9fafb;
            color: #374151;
        }

        tbody td {
            vertical-align: middle;
        }

        .action-edit {
            color: #4f46e5;
            text-decoration: none;
            margin-right: 12px;
        }

        .action-delete {
            color: #dc2626;
            text-decoration: none;
        }

        .empty {
            text-align: center;
            padding: 40px;
            color: #6b7280;
        }
    </style>
</head>

<body>

<div class="topbar">
    <div class="topbar-left">
        <h5>User Management</h5>

        <button class="dashboard-btn"
                onclick="window.location.href='/'">
            Dashboard
        </button>
    </div>

    <form action="/logout" method="post">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<div class="content">

    <div class="page-card">

        <div class="page-header">
            <div class="page-title">User List</div>

            <button class="primary-btn"
                    onclick="window.location.href='/register'">
                + Add User
            </button>
        </div>

        <table class="table table-hover">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Roles</th>
                    <th>Action</th>
                </tr>
            </thead>

            <tbody>
                <c:if test="${empty users}">
                    <tr>
                        <td colspan="6" class="empty">No users found</td>
                    </tr>
                </c:if>

                <c:forEach var="user" items="${users}" varStatus="i">
                    <tr>
                        <td>${i.count}</td>
                        <td>${user.name}</td>
                        <td>${user.username}</td>
                        <td>${user.phoneNo}</td>
                        <td>${user.roles}</td>
                        <td>
                            <a href="/user/get?username=${user.username}" class="action-edit">Edit</a>
                            <a href="/user/delete?username=${user.username}"
                               onclick="return confirm('Delete this user?')"
                               class="action-delete">Delete</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

    </div>

</div>

</body>
</html>