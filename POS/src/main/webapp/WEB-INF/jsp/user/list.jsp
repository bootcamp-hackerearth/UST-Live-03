<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Management</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }

        html, body {
            height: 100%;
        }

        body {
            background-color: #f5f6fc;
            color: #667eea;
        }

        .header {
            height: 50px;
            background-color: #f5f6fc;
            display: flex;
            align-items: center;
            padding: 0 20px;
            box-shadow: 0px 0px 7px 1px rgba(75, 108, 183, 0.25);
        }

        .hamburger {
            font-size: 22px;
            cursor: pointer;
            margin-right: 15px;
        }

        .header h3 {
            flex-grow: 1;
        }

        .header a {
            text-decoration: none;
            color: white;
            padding: 5px 10px;
            font-size: 12px;
            background: #667eea;
            border-radius: 5px;
        }

        .sidebar {
            position: fixed;
            top: 0;
            left: -250px;
            width: 250px;
            height: 100vh;
            background-color: #f5f6fc;
            transition: 0.3s ease;
            padding-top: 15px;
        }

        .sidebar.active {
            left: 0;
            border-radius: 10px;
            box-shadow: 0px 0px 20px 10px rgba(75, 108, 183, 0.25);
            overflow: overlay;
        }

        .close-btn {
            font-size: 20px;
            cursor: pointer;
            text-align: right;
            padding: 10px 20px;
        }

        .sidebar a {
            display: block;
            padding: 12px 20px;
            color: #667eea;
            text-decoration: none;
            border: solid 0.5px #667eea;
            margin: 5px 10px;
            border-radius: 5px;
            font-size: 14px;
        }

        .sidebar a:hover {
            background-color: #667eea;
            color: white;
        }

        .content {
            padding: 30px;
            transition: margin-left 0.3s ease;
        }

        .content.shift {
            margin-left: 250px;
        }

        .card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0px 0px 15px rgba(75, 108, 183, 0.25);
        }

        .card h3 {
            text-align: center;
            margin-bottom: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: center;
        }

        th {
            background-color: #667eea;
            color: white;
            padding: 10px;
        }

        td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }

        tr:hover {
            background-color: #f0f2ff;
        }

        .btn {
            padding: 6px 10px;
            border-radius: 5px;
            font-size: 12px;
            text-decoration: none;
            color: white;
            margin: 0 3px;
        }

        .btn-danger {
            background-color: #e53e3e;
        }

        .btn-primary {
            background-color: #667eea;
        }

        .btn-secondary {
            background-color: #718096;
        }

        .alert {
            text-align: center;
            padding: 10px;
            background: #fff3cd;
            color: #856404;
            border-radius: 5px;
            margin-bottom: 15px;
        }

        .footer {
            margin-top: 20px;
            text-align: center;
            font-size: 12px;
            color: #777;
        }
    </style>
</head>

<body>

<div id="sidebar" class="sidebar">
    <div class="close-btn" onclick="toggleSidebar()">✖</div>

    <c:forEach var="node" items="${nodes}">
    <a href="${node.path}"> ${node.identifier}</a>
    </c:forEach>
</div>


<div class="header">
    <span class="hamburger" onclick="toggleSidebar()">☰</span>
    <h3>User Management</h3>
    <a href="/logout">Logout</a>
</div>


<div id="content" class="content">
    <div class="card">

        <h3>User Management</h3>

        <c:if test="${empty users}">
            <div class="alert">No users found</div>
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
                            <a class="btn btn-danger"
                               href="/user/delete?username=${user.username}"
                               onclick="return confirm('Are you sure you want to delete this user?');">
                                Delete
                            </a>
                            <a class="btn btn-primary"
                               href="/user/get?username=${user.username}">
                                Update
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            ${message}
        </c:if>

        <div class="footer">
            <a href="/" class="btn btn-secondary">Home</a>
        </div>
    </div>
</div>

<script>
    function toggleSidebar() {
        document.getElementById("sidebar").classList.toggle("active");
        document.getElementById("content").classList.toggle("shift");
    }
</script>

</body>
</html>