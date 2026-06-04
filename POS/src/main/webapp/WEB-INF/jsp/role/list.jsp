<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Role Dashboard</title>

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

.container {
    width: 95%;
    max-width: 900px;
    margin: auto;
}

h2 {
    text-align: center;
    margin-bottom: 30px;
    font-weight: 600;
    color: #0f172a;
}

.table-container {
    background: #ffffff;
    border-radius: 12px;
    padding: 20px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 8px 20px rgba(0,0,0,0.05);
}

table {
    width: 100%;
    border-collapse: collapse;
}

thead {
    background: #6366f1;
    color: white;
}

th {
    padding: 14px;
    font-size: 12px;
    text-transform: uppercase;
}

td {
    padding: 12px;
    text-align: center;
    font-size: 14px;
    border-bottom: 1px solid #e5e7eb;
}

tbody tr:hover {
    background: #f1f5f9;
}

.badge {
    padding: 4px 10px;
    border-radius: 6px;
    background: #e0e7ff;
    color: #3730a3;
    font-size: 12px;
}

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

.empty {
    padding: 20px;
    color: #64748b;
}

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

    <h2>Role Management</h2>

    <div class="table-container">

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

            <c:choose>

                <c:when test="${not empty roles}">
                    <c:forEach var="role" items="${roles}">
                        <tr>

                            <td>
                                  <span class="badge">${role.id}</span>
                            </td>

                            <td>${role.identifier}</td>

                            <td>${role.description}</td>

                            <td>
                                <a class="action-btn edit-btn"
                                   href="${pageContext.request.contextPath}/role/get?identifier=${role.identifier}">
                                    Edit
                                </a>

                                <a class="action-btn delete-btn"
                                   href="${pageContext.request.contextPath}/role/delete?identifier=${role.identifier}"
                                   onclick="return confirm('Are you sure you want to delete this role?');">
                                    Delete
                                </a>
                            </td>

                        </tr>
                    </c:forEach>
                </c:when>

                <c:otherwise>
                    <tr>
                        <td colspan="3" class="empty">
                            No roles found
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

        <a href="${pageContext.request.contextPath}/role/add" class="btn home-btn">
            Add New Role
        </a>

    </div>

</div>

</body>
</html>