<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>POS | Add Role</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
      rel="stylesheet">

<style>
body {
    background: #f8fafc;
    min-height: 100vh;
    font-family: "Segoe UI", Arial, sans-serif;
}

.card {
    border-radius: 16px;
    border: none;
}

.card-header {
    background: #0f172a;
    color: #e2e8f0;
    border-top-left-radius: 16px;
    border-top-right-radius: 16px;
}

.form-control {
    border-radius: 8px;
}

.btn-primary {
    background: #0f766e;
    border: none;
}

.btn-primary:hover {
    background: #115e59;
}

.back-link {
    color: #0f766e;
    font-weight: 600;
    text-decoration: none;
}

.back-link:hover {
    text-decoration: underline;
}

.card-footer {
    background: #f1f5f9;
}
</style>
</head>

<body>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-5">

        <div class="card shadow-lg">

            <div class="card-header text-center">
                <h4 class="mb-0">Add New Role</h4>
            </div>

            <div class="card-body">

                <c:if test="${not empty message}">
                    <div class="alert alert-danger text-center">
                        ${message}
                    </div>
                </c:if>

                <form:form method="post"
                           action="${pageContext.request.contextPath}/role/add"
                           modelAttribute="roleDto">

                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            Role Identifier <span class="text-danger">*</span>
                        </label>

                        <form:input
                                path="identifier"
                                cssClass="form-control"
                                placeholder="e.g. ADMIN, USER"
                                required="true"/>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            Description
                        </label>

                        <form:textarea
                                path="description"
                                cssClass="form-control"
                                rows="3"
                                placeholder="Describe role responsibilities"/>
                    </div>

                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            Add Role
                        </button>
                    </div>

                    <div class="text-center mt-3">
                        <a href="${pageContext.request.contextPath}/role/list"
                           class="back-link">
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

</body>
</html>
