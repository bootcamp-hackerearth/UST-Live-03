<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Role</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #2f2f2f;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", sans-serif;
        }

        .container {
            max-width: 420px;
        }

        .card {
            border: none;
            border-radius: 18px;
            background-color: #ffffff;
            box-shadow: 0 15px 40px rgba(0, 0, 0, 0.25);
        }

        .card-header {
            background: transparent;
            border-bottom: none;
            text-align: center;
        }

        .form-control {
            border-radius: 10px;
            padding: 10px 14px;
        }

        .form-label {
            font-weight: 600;
            color: #555;
        }

        .btn-primary {
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            border: none;
            border-radius: 25px;
            padding: 10px;
        }

        .btn-primary:hover {
            background: linear-gradient(90deg, #00c6ff, #0072ff);
        }

        .btn-back {
            border-radius: 25px;
            padding: 10px;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="card">

        <div class="card-header">
            <h4>Add New Role</h4>
        </div>

        <div class="card-body">

            <c:if test="${not empty role}">
                <div class="alert alert-success text-center">
                    ${role}
                </div>
            </c:if>

            <c:if test="${not empty message}">
                <div class="alert alert-danger text-center">
                    ${message}
                </div>
            </c:if>

            <form:form method="post" action="/role/add" modelAttribute="roleDto">

                <div class="mb-3">
                    <label class="form-label">Role Name</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                placeholder="Enter role name"
                                required="true"/>
                </div>

                <div class="mb-3">
                    <label class="form-label">Description</label>
                    <form:input path="description"
                                cssClass="form-control"
                                placeholder="Enter description" required="true"/>
                </div>

                <div class="d-grid gap-2">
                    <button type="submit" class="btn btn-primary">
                        Add Role
                    </button>

                    <a href="/role/list" class="btn btn-outline-secondary btn-back">
                        ← Back to Roles
                    </a>
                </div>

            </form:form>

        </div>

    </div>
</div>

</body>
</html>