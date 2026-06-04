<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User List</title>

    <style>
        body {
            font-family: "Segoe UI", Arial, sans-serif;
            background: #F6F7F9;
            padding: 30px;
        }

        h2 {
            text-align: center;
            color: #111827;
            margin-bottom: 20px;
            font-weight: 600;
        }

        .table-card {
            background: #FFFFFF;
            border-radius: 16px;
            padding: 20px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.06);
            overflow-x: auto;
            border: 1px solid #E5E7EB;
        }

        table {
            border-collapse: collapse;
            width: 100%;
            min-width: 900px;
        }

        th, td {
            padding: 12px 10px;
            text-align: center;
            font-size: 14px;
        }

        th {
            background: #2B2B2B;
            color: #FFFFFF;
            font-weight: 600;
        }

        tr:nth-child(even) {
            background-color: #F9FAFB;
        }

        tr:hover {
            background-color: #F3F4F6;
        }

        .edit-btn {
            padding: 6px 12px;
            margin-right: 6px;
            background: #2B2B2B;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 500;
            cursor: pointer;
        }

        .edit-btn:hover {
            background: #111111;
        }

        .delete-btn {
            padding: 6px 12px;
            background: #B91C1C;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 500;
            cursor: pointer;
        }

        .delete-btn:hover {
            background: #7F1D1D;
        }

        a {
            text-decoration: none;
        }

        .top-right-actions {
            display: flex;
            justify-content: flex-end;
            margin-bottom: 15px;
            gap: 10px;
        }

        .top-right-actions button {
            padding: 6px 14px;
            border: none;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 500;
            cursor: pointer;
        }

        .add-btn {
            background: #2B2B2B;
            color: white;
        }

        .add-btn:hover {
            background: #111111;
        }

        .back-btn {
            background: #E5E7EB;
            color: #111827;
        }

        .back-btn:hover {
            background: #D1D5DB;
        }

        .msg {
            color: #166534;
            background: #DCFCE7;
            padding: 10px 14px;
            border-radius: 10px;
            text-align: center;
            width: fit-content;
            margin: 0 auto 16px auto;
            font-size: 14px;
            font-weight: 500;
        }
    </style>
</head>

<body>

<h2>User List</h2>

<div class="top-right-actions">
    <a href="/register">
        <button class="add-btn">Add User</button>
    </a>
    <a href="/">
        <button class="back-btn">Back</button>
    </a>
</div>

<c:if test="${not empty message}">
    <div class="msg">${message}</div>
</c:if>

<div class="table-card">
    <table>
        <tr>
            <th>Name</th>
            <th>Username</th>
            <th>Phone</th>
            <th>Roles</th>
            <th>Action</th>
        </tr>

        <c:forEach items="${users}" var="user">
            <tr>
                <td>${user.name}</td>
                <td>${user.username}</td>
                <td>${user.phoneNo}</td>
                <td>${user.roles}</td>
                <td>
                    <a href="/user/get?username=${user.username}">
                        <button class="edit-btn">Edit</button>
                    </a>

                    <a href="/user/delete?username=${user.username}"
                       onclick="return confirm('Are you sure you want to delete this user?');">
                        <button class="delete-btn">Delete</button>
                    </a>
                </td>
            </tr>
        </c:forEach>

    </table>
</div>

</body>
</html>
