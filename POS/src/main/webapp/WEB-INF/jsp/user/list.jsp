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
            background-color: #f6f7f9;
            color: #111827;
        }

        .topbar {
            height: 56px;
            background-color: #020617;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 20px;
            border-bottom: 1px solid #1e293b;
        }

        .topbar-left {
            display: flex;
            align-items: center;
            gap: 16px;
        }

        .top-title {
            font-size: 16px;
            font-weight: 600;
            color: #e5e7eb;
        }

        .home-btn {
            padding: 7px 16px;
            background-color: #1e293b;
            color: #e5e7eb;
            text-decoration: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
            border: 1px solid #334155;
        }

        .logout-btn {
            background: #dc2626;
            border: none;
            color: #ffffff;
            padding: 7px 16px;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
        }

        .page-title {
            text-align: center;
            padding: 22px 0 14px;
            font-size: 28px;
            font-weight: 700;
            color: #020617;
        }

        .container {
            width: 95%;
            max-width: 1100px;
            margin: 0 auto 28px;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 6px 18px rgba(0,0,0,0.08);
            padding: 26px;
        }

        table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            font-size: 15px;
        }

        thead {
            background-color: #e5e7eb;
        }

        th {
            padding: 16px;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 0.06em;
            color: #020617;
            border-bottom: 1px solid #cbd5e1;
            text-align: center;
            font-weight: 700;
        }

        td {
            padding: 16px;
            text-align: center;
            border-bottom: 1px solid #e5e7eb;
            color: #111827;
            font-weight: 500;
        }

        tbody tr:hover {
            background-color: #f1f5f9;
        }

        .action-link {
            padding: 7px 16px;
            border-radius: 6px;
            text-decoration: none;
            font-weight: 600;
            font-size: 14px;
            color: #ffffff;
            display: inline-block;
        }

        .edit {
            background-color: #2563eb;
        }

        .delete {
            background-color: #dc2626;
            margin-left: 8px;
        }
    </style>
</head>

<body>

<div class="topbar">
    <div class="topbar-left">
        <div class="top-title">POS Application</div>
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>
    </div>

    <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
</div>

<div class="page-title">User Management</div>

<c:if test="${not empty message}">
    <div style="
        margin: 0 24px 18px;
        padding: 10px;
        border-radius: 6px;
        background-color: #fee2e2;
        color: #991b1b;
        text-align: center;
        font-size: 14px;
        font-weight: 600;
    ">
        ${message}
    </div>
</c:if>

<div class="container">

    <c:if test="${empty users}">
        <div style="text-align:center; padding:28px; color:#374151; font-size:16px;">
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
           class="action-link edit">
            Edit
        </a>

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