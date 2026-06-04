<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
            padding: 40px;
            color: #333;
        }
        .container {
            max-width: 1000px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 2px solid #eee;
            padding-bottom: 15px;
            margin-bottom: 20px;
        }
        h2 { margin: 0; }
        .header-buttons {
            display: flex;
            gap: 8px;
        }
        .btn {
            padding: 8px 15px;
            text-decoration: none;
            border-radius: 4px;
            font-size: 13px;
            color: white;
            font-weight: bold;
            display: inline-block;
        }
        .btn-home     { background-color: #6c757d; }
        .btn-add      { background-color: #28a745; }
        .btn-edit     { background-color: #007bff; padding: 6px 12px; }
        .btn-delete   { background-color: #dc3545; padding: 6px 12px; }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th {
            background-color: #f8f9fa;
            color: #666;
            text-align: left;
            padding: 12px 15px;
            border-bottom: 2px solid #dee2e6;
            font-size: 14px;
            white-space: nowrap;
        }
        td {
            padding: 12px 15px;
            border-bottom: 1px solid #eee;
            font-size: 14px;
            vertical-align: middle;
        }
        tr:hover { background-color: #fafafa; }
        .badge {
            display: inline-block;
            background: #e9ecef;
            color: #495057;
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: bold;
            text-transform: uppercase;
            border: 1px solid #ddd;
            margin: 2px;
        }
        .text-muted { color: #999; }
        .action-buttons {
            display: flex;
            gap: 8px;
            align-items: center;
        }
        .empty-msg {
            text-align: center;
            padding: 40px;
            color: #999;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h2>User Management</h2>
        <div class="header-buttons">
            <a href="/" class="btn btn-home">Home</a>
        </div>
    </div>
    <c:if test="${empty users}">
        <div class="empty-msg">No users found</div>
    </c:if>
    <c:if test="${not empty users}">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
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
                    <td>${user.id}</td>
                    <td>${user.username}</td>
                    <td>${user.name}</td>
                    <td>${user.phoneNo}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty user.roles}">
                                <c:forEach var="role" items="${user.roles}">
                                    <span class="badge">${role}</span>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted">—</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <div class="action-buttons">
                            <a href="/user/get?username=${user.username}"
                               class="btn btn-edit">Edit</a>
                            <a href="/user/delete?username=${user.username}"
                               class="btn btn-delete"
                               onclick="return confirm('Are you sure you want to delete ${user.name}?');">
                                Delete
                            </a>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>
</body>
</html>