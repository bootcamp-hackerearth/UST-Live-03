<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Role</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #667eea, #764ba2);
            min-height: 100vh;
        }
        .card {
            border-radius: 12px;
        }
        .form-control {
            border-radius: 8px;
        }
    </style>
</head>
<body>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-5">
        <div class="card shadow-lg">
            <div class="card-header text-center bg-primary text-white">
                <h4 class="mb-0">Add New Role</h4>
            </div>
            <div class="card-body">
                <form:form method="post"
                           action="/role/add"
                           modelAttribute="roleDto">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Role Name</label>
                        <form:input path="identifier"
                                    cssClass="form-control"
                                    placeholder="Enter role name"
                                    required="required"/>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Description</label>
                        <form:input path="description"
                                    cssClass="form-control"
                                    placeholder="Enter role description"
                                    required="required"/>
                    </div>
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            Add Role
                        </button>
                    </div>
                    <div class="d-grid mt-3">
                        <a href="/role/list" class="btn btn-outline-primary">
                            ← Back to Role List
                        </a>
                    </div>
                </form:form>
            </div>
            <div class="card-footer text-center text-muted small">
                POS Management System
            </div>
        </div>
    </div>
</div>
<c:if test="${not empty message}">
    <script>
        alert("${message}");
    </script>
</c:if>
</body>
</html>
