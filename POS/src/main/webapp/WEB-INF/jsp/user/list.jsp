<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Management</title>

    <!-- Google Font -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        body {
            background-color: #f4f6fb;
            background-image:
                radial-gradient(circle at top right, rgba(78,115,223,0.12), transparent 45%),
                radial-gradient(circle at bottom left, rgba(111,66,193,0.12), transparent 45%);
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            margin: 0;
        }

        .container {
            width: 90%;
            max-width: 1100px;
            margin: 40px auto;
        }

        .card {
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 8px 24px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .card-body {
            padding: 24px;
        }

        .card-footer {
            background-color: #f8f9fc;
            padding: 12px;
            text-align: center;
            font-size: 13px;
            color: #777;
        }

        h3 {
            text-align: center;
            color: #4e73df;
            font-weight: 600;
            margin-bottom: 24px;
        }

        /* Buttons */
        .btn {
            padding: 8px 16px;
            font-size: 14px;
            font-weight: 600;
            border-radius: 8px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            text-align: center;
        }

        .btn-home {
            background-color: #4e73df;
            color: #ffffff;
        }

        .btn-home:hover {
            background-color: #2e59d9;
        }


.btn-update {
    background-color: #1cc88a;
    color: #ffffff;
    margin-right: 6px;
}


        .btn-delete {
            background-color: #e74a3b;
            color: #ffffff;
        }


.btn-update:hover {
    background-color: #17a673;
}


        .btn-delete:hover {
            background-color: #c0392b;
        }

        /* Alerts */
        .alert {
            background-color: #fff3cd;
            color: #856404;
            padding: 12px;
            border-radius: 8px;
            text-align: center;
            font-size: 14px;
            margin-bottom: 16px;
        }

        /* Table */
        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background-color: #4e73df;
            color: #ffffff;
            padding: 12px;
            font-size: 14px;
            font-weight: 600;
            text-align: center;
        }

        td {
            padding: 12px;
            border-bottom: 1px solid #e0e4ee;
            font-size: 14px;
            color: #555;
            text-align: center;
            vertical-align: middle;
        }

        tr:hover {
            background-color: #eef3ff;
        }

        .top-bar {
            margin-bottom: 16px;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="card">
        <div class="card-body">

            <h3>User Management</h3>

            <div class="top-bar">
                <a href="/" class="btn btn-home">
                    Home
                </a>
            </div>

            <c:if test="${empty users}">
                <div class="alert">
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
                            <th style="width:180px;">Action</th>
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
                                    <a class="btn btn-update"
                                       href="/user/get?username=${user.username}">
                                        Update
                                    </a>

                                    <a class="btn btn-delete"
                                       href="/user/delete?username=${user.username}"
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

        <div class="card-footer">
            User Management System
        </div>
    </div>
</div>

</body>
</html>