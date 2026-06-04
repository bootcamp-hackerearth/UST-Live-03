<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>User Dashboard</title>

<style>

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Segoe UI', Arial, sans-serif;
    background: linear-gradient(135deg, #eef2ff, #f8fafc);
    color: #1e293b;
    padding: 40px 20px;
}

/* Container */
.container {
    width: 95%;
    max-width: 1200px;
    margin: auto;
}

/* Title */
h2 {
    text-align: center;
    margin-bottom: 30px;
    font-weight: 600;
    color: #0f172a;
}

/* Card */
.table-container {
    background: #ffffff;
    border-radius: 12px;
    padding: 20px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 8px 20px rgba(0,0,0,0.05);
}

/* Table */
table {
    width: 100%;
    border-collapse: collapse;
}

/* Header */
thead {
    background: #6366f1;
    color: white;
}

th {
    padding: 14px;
    font-size: 12px;
    text-transform: uppercase;
}

/* Rows */
td {
    padding: 12px;
    text-align: center;
    font-size: 14px;
    border-bottom: 1px solid #e5e7eb;
}

tbody tr:hover {
    background: #f1f5f9;
}

/* Badge */
.badge {
    padding: 4px 10px;
    border-radius: 6px;
    background: #e0e7ff;
    color: #3730a3;
    font-size: 12px;
}

/* Role tags */
.role-tag {
    display: inline-block;
    padding: 4px 8px;
    margin: 2px;
    border-radius: 6px;
    font-size: 12px;
    background: #e0e7ff;
    color: #3730a3;
}

/* Action buttons */
.action-btn {
    display: inline-block;
    padding: 6px 10px;
    margin: 2px;
    border-radius: 6px;
    font-size: 12px;
    text-decoration: none;
    color: white;
}

.edit-btn {
    background: #6366f1;
}

.edit-btn:hover {
    background: #4f46e5;
}

.delete-btn {
    background: #ef4444;
}

.delete-btn:hover {
    background: #dc2626;
}

/* Empty */
.empty {
    padding: 20px;
    color: #64748b;
}

/* Buttons */
.btn-container {
    text-align: center;
    margin-top: 25px;
}

.btn {
    display: inline-block;
    padding: 10px 20px;
    margin: 8px;
    border-radius: 6px;
    text-decoration: none;
    font-size: 14px;
    color: white;
}

.add-btn {
    background: #6366f1;
}

.add-btn:hover {
    background: #4f46e5;
}

.home-btn {
    background: #0ea5e9;
}

.home-btn:hover {
    background: #0284c7;
}

</style>
</head>

<body>

<div class="container">

    <h2>User Management</h2>

    <div class="table-container">

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

            <c:choose>

                <c:when test="${not empty users}">
                    <c:forEach var="user" items="${users}">
                        <tr>

                            <td>${user.username}</td>

                            <td>${user.name}</td>

                            <td>${user.phoneNo}</td>

                            <!-- Roles -->
                            <td>
                                <c:choose>
                                    <c:when test="${not empty user.roles}">
                                        <c:forEach var="r" items="${user.roles}">
                                            <span class="role-tag">${r}</span>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color:#94a3b8;">-</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <!-- Actions -->
                            <td>
                                <!-- EDIT -->
                                <a class="action-btn edit-btn"
                                   href="${pageContext.request.contextPath}/user/get?username=${user.username}">
                                    Edit
                                </a>

                                <!-- DELETE -->
                                <a class="action-btn delete-btn"
                                   href="${pageContext.request.contextPath}/user/delete?username=${user.username}"
                                   onclick="return confirm('Are you sure you want to delete this user?');">
                                    Delete
                                </a>
                            </td>

                        </tr>
                    </c:forEach>
                </c:when>

                <c:otherwise>
                    <tr>
                        <td colspan="5" class="empty">
                            No users found
                        </td>
                    </tr>
                </c:otherwise>

            </c:choose>

            </tbody>

        </table>

    </div>

    <div class="btn-container">

        <a href="${pageContext.request.contextPath}/" class="btn add-btn">
            Go to Home
        </a>

        <a href="${pageContext.request.contextPath}/register" class="btn home-btn">
            Register User
        </a>

    </div>

</div>

</body>
</html>