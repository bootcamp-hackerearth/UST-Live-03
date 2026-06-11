<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Node</title>
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css">
    <style>
        body {
            font-family: "Segoe UI", Arial, sans-serif;
            background-color: #FFF8F0;
            height: 100vh;
            margin: 0;
        }

        .card {
            border-radius: 18px;
            background-color: #ffffff;
            box-shadow: 0 18px 35px rgba(75, 46, 43, 0.25);
        }

        h3 {
            color: #4B2E2B;
            font-weight: 600;
        }

        label {
            color: #4B2E2B;
            font-weight: 600;
            font-size: 14px;
        }

        .form-control {
            border-radius: 8px;
            padding: 10px;
            font-size: 14px;
            border: 1px solid #ccb7b2;
        }

        .form-control:focus {
            border-color: #4B2E2B;
            box-shadow: none;
        }

        .multi-role-box {
            background-color: #fff3eb;
            border: 1px solid #e6d3cd;
            border-radius: 10px;
            padding: 10px;
            margin-bottom: 20px;
            max-height: 150px;
            overflow-y: auto;
        }

        .role-item {
            display: block;
            margin-bottom: 6px;
            font-size: 14px;
            cursor: pointer;
            color: #4B2E2B;
            font-weight: 500;
        }

        .role-item input {
            margin-right: 8px;
            accent-color: #4B2E2B;
        }

        .btn-primary {
            background-color: #4B2E2B;
            border: none;
            border-radius: 10px;
            padding: 10px;
            font-size: 15px;
            font-weight: 600;
            width: 100%;
        }

        .btn-primary:hover {
            background-color: #3a2421;
        }
    </style>
</head>
<body>
<div class="container d-flex justify-content-center align-items-center" style="height: 100vh;">
    <div class="card shadow p-4" style="width: 420px;">
        <h3 class="text-center mb-4">Add Node</h3>
        <form action="/node/add" method="post" modelAttribute="nodeDto">
            <div class="form-group">
                <label><b>Node Name</b></label>
                <input type="text"
                       class="form-control"
                       name="identifier"
                       placeholder="Enter the node name"
                       required="required">
            </div>
            <div class="form-group">
                <label><b>Path Name</b></label>
                <input type="text"
                       class="form-control"
                       name="path"
                       placeholder="Enter the path"
                       required>
            </div>
            <label><b>Roles</b></label>
            <div class="multi-role-box">
                <c:forEach items="${roles}" var="role">
                    <label class="role-item">
                        <input type="checkbox"
                               name="roles"
                               value="${role.identifier}">
                        ${role.identifier}
                    </label>
                </c:forEach>
            </div>
            <button type="submit" class="btn btn-primary">
                Add Node
            </button>
        </form>
    </div>
</div>
</body>
</html>