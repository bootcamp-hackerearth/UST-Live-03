<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Role List</title>

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
    width: 900px;
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

.role-name {
    font-weight: bold;
    color: #00ffff;
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
    display: flex;
    justify-content: center;
    gap: 12px;
}
</style>
</head>

<body>

<div class="container">

    <h2>List of Roles</h2>

    <c:if test="${empty roles}">
        <div class="alert">
            No roles found
        </div>
    </c:if>

    <c:if test="${not empty roles}">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Role</th>
                <th>Description</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="role" items="${roles}">
                <tr>
                    <td>${role.id}</td>

                    <td class="role-name">
                        ${role.identifier}
                    </td>

                    <td>${role.description}</td>

                    <td>
                        <a href="/role/get?identifier=${role.identifier}"
                           class="btn btn-warning">
                            Update
                        </a>

                        <a href="/role/delete?identifier=${role.identifier}"
                           class="btn btn-danger"
                           onclick="return confirm('Are you sure you want to delete this role?');">
                            Delete
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div class="footer">
        <a href="/" class="btn btn-secondary">Home</a>
        <a href="/role/add" class="btn btn-primary">+ Add Role</a>
    </div>

</div>

</body>
</html>