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
    font-family: 'Poppins', sans-serif;
}

body {
    min-height: 100vh;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
    display: flex;
    justify-content: center;
    align-items: center;
}

.container {
    width: 1000px;
    padding: 30px;
    border-radius: 15px;

    background: rgba(255,255,255,0.05);
    backdrop-filter: blur(12px);

    border: 1px solid rgba(255,255,255,0.2);
    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
}

h2 {
    text-align: center;
    color: #fff;
    margin-bottom: 20px;
}

table {
    width: 100%;
    border-collapse: collapse;
    color: #fff;
}

th {
    background: rgba(0,255,255,0.2);
    color: #00ffff;
    padding: 12px;
}

td {
    padding: 12px;
    text-align: center;
    border-bottom: 1px solid rgba(255,255,255,0.1);
}

tr:hover {
    background: rgba(0,255,255,0.1);
}

.btn {
    padding: 6px 10px;
    border-radius: 6px;
    font-size: 12px;
    text-decoration: none;
    margin: 0 4px;
}

.btn-warning {
    background: #ffc107;
    color: #000;
}

.btn-danger {
    background: #ff4d4d;
    color: #fff;
}

.btn-primary {
    background: #00ffff;
    color: #000;
}

.btn-secondary {
    background: #666;
    color: #fff;
}

.btn:hover {
    box-shadow: 0 0 10px #00ffff;
}

.alert {
    text-align: center;
    padding: 10px;
    color: #aaa;
}

.footer {
    margin-top: 20px;
    text-align: center;
}

.footer .actions {
    display: flex;
    justify-content: center;
    gap: 12px;
    margin-bottom: 10px;
}

.footer small {
    color: #aaa;
}
</style>
</head>

<body>

<div class="container">

    <h2>User Management</h2>

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
                <th>Actions</th>
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
                        <a class="btn btn-warning"
                           href="/user/get?username=${user.username}">
                            Edit
                        </a>

                        <a class="btn btn-danger"
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

    <div class="footer">
        <div class="actions">
            <a href="/" class="btn btn-secondary">Home</a>
            <a href="/register" class="btn btn-primary">Register User</a>
        </div>

        <small>POS Management System</small>
    </div>

</div>

</body>
</html>