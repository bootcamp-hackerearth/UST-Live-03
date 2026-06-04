<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <style>
        body {
            background-color: #f3f5f7;
            font-family: "Segoe UI", sans-serif;
        }
        .page-container {
            max-width: 1300px;
            margin: 50px auto;
        }
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }
        .page-title {
            font-size: 38px;
            font-weight: 700;
            color: #2c3e50;
            margin: 0;
        }
        .card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.08);
            overflow: hidden;
        }
        .card-header {
            background: white;
            border-bottom: 1px solid #e9ecef;
            padding: 20px 25px;
        }
        .card-header h4 {
            margin: 0;
            color: #2c3e50;
            font-weight: 600;
        }
        .table {
            margin-bottom: 0;
        }
        .table th {
            background-color: #f8f9fa;
            color: #495057;
            font-weight: 600;
            text-align: center;
            border-bottom: 2px solid #dee2e6;
        }
        .table td {
            text-align: center;
            vertical-align: middle;
        }
        .table tbody tr:hover {
            background-color: #f8f9fa;
        }
        .card-footer {
            background: white;
            border-top: 1px solid #e9ecef;
            padding: 20px;
        }
        .btn-add {
            padding: 10px 20px;
            font-weight: 500;
        }
        .btn-back {
            min-width: 100px;
        }
        .action-buttons {
            display: flex;
            justify-content: center;
            gap: 8px;
        }
        .empty-box {
            padding: 50px;
            text-align: center;
            color: #6c757d;
            font-size: 18px;
        }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="page-container">
        <div class="page-header">
            <h1 class="page-title">
                Role Management
            </h1>
            <a href="/role/add" class="btn btn-primary btn-add">
                + Add Role
            </a>
        </div>
        <div class="card">
            <div class="card-header">
                <h4>Roles List</h4>
            </div>
            <div class="card-body p-0">
                <c:if test="${empty roles}">
                    <div class="empty-box">
                        No Roles Found
                    </div>
                </c:if>
                <c:if test="${not empty roles}">
                    <table class="table table-hover">
                        <thead>
                        <tr>
                            <th style="width: 10%">ID</th>
                            <th style="width: 25%">Role</th>
                            <th style="width: 40%">Description</th>
                            <th style="width: 25%">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="role" items="${roles}">
                            <tr>
                                <td>${role.id}</td>
                                <td>${role.identifier}</td>
                                <td>${role.description}</td>
                                <td>
                                    <div class="action-buttons">
                                        <a href="/role/get?identifier=${role.identifier}"
                                           class="btn btn-outline-primary btn-sm">
                                            Edit
                                        </a>
                                        <a href="/role/delete?identifier=${role.identifier}"
                                           class="btn btn-outline-danger btn-sm"
                                           onclick="return confirm('Are you sure you want to delete this role?');">
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
            <div class="card-footer d-flex justify-content-between">
                <a href="/" class="btn btn-secondary btn-back">
                    Back
                </a>
            </div>
        </div>
    </div>
</div>
</body>
</html>