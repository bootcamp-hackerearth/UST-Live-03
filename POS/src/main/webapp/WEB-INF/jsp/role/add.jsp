<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Role</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #f8fafc, #eef2ff);
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: Arial, sans-serif;
        }

        .role-card {
            width: 450px;
            border: none;
            border-radius: 18px;
            overflow: hidden;
        }

        .card-header {
            background: #0d6efd;
            color: white;
            text-align: center;
            padding: 18px;
            font-size: 22px;
            font-weight: 600;
        }

        .card-body {
            padding: 30px;
        }

        .form-label {
            font-weight: 600;
            color: #495057;
        }

        .form-control {
            border-radius: 10px;
            height: 45px;
        }

        .btn-primary {
            border-radius: 10px;
            width: 100%;
            height: 45px;
            font-weight: 600;
        }

        .success-msg {
            background: #d1e7dd;
            color: #0f5132;
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 20px;
            text-align: center;
        }
    </style>
</head>

<body>

<div class="card shadow-lg role-card">

    <div class="card-header">
        Add New Role
    </div>

    <div class="card-body">

        <c:if test="${not empty role}">
            <div class="success-msg">
                ${role}
            </div>
        </c:if>

        <form:form method="post"
                   action="/role/add"
                   modelAttribute="roleDto">

            <div class="mb-3">
                <label class="form-label">Role Name</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            placeholder="Enter role name"/>
            </div>

            <div class="mb-4">
                <label class="form-label">Description</label>
                <form:input path="description"
                            cssClass="form-control"
                            placeholder="Enter description"/>
            </div>

            <button type="submit" class="btn btn-primary">
                Add Role
            </button>

        </form:form>

    </div>

</div>

</body>
</html>