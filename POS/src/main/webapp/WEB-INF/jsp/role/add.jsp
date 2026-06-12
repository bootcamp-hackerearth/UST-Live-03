<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Role</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <style>
        body {
            background-color: #FFF8F0;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .card {
            width: 420px;
            border-radius: 18px;
            border: none;
            box-shadow: 0 18px 35px rgba(75, 46, 43, 0.25);
            background: #ffffff;
        }

        .card-header {
            background-color: #4B2E2B;
            color: #FFF8F0;
            text-align: center;
            border-top-left-radius: 18px;
            border-top-right-radius: 18px;
        }

        h4 {
            font-weight: 600;
        }

        .form-label {
            font-weight: 600;
            font-size: 14px;
            color: #4B2E2B;
        }

        .form-control {
            border-radius: 8px;
            border: 1px solid #ccb7b2;
        }

        .form-control:focus {
            border-color: #4B2E2B;
            box-shadow: none;
        }

        .btn-primary {
            background-color: #4B2E2B;
            border: none;
            font-weight: 600;
            padding: 10px;
            border-radius: 10px;
        }

        .btn-primary:hover {
            background-color: #3a2421;
        }

        .card-footer {
            background-color: #fff3eb;
            border-top: none;
            text-align: center;
            font-size: 13px;
            color: #6b4a46;
        }
    </style>
</head>
<body>
<div class="card">
    <div class="card-header">
        <h4 class="mb-0">Add New Role</h4>
    </div>
    <div class="card-body">
        <c:if test="${not empty role}">
            <div class="alert alert-success text-center">
                ${role}
            </div>
        </c:if>
        <form:form method="post" action="/role/add" modelAttribute="roleDto">
            <div class="mb-3">
                <label class="form-label"><b>Role Name</b></label>
                <form:input path="identifier"
                    cssClass="form-control"
                    placeholder="Enter role name"
                    required="true"
                />
            </div>
            <div class="mb-4">
                <label class="form-label"><b>Role Description</b></label>
                <form:input path="description"
                    cssClass="form-control"
                    placeholder="Enter role description"
                    required="true"
                />
            </div>
            <div class="d-grid">
                <button type="submit" class="btn btn-primary">
                    Add Role
                </button>
            </div>
        </form:form>
    </div>
    <div class="card-footer">
        POS Management System
    </div>
</div>
</body>
</html>