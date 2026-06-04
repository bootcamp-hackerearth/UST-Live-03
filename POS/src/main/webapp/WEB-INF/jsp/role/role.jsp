<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>POS | Edit Role</title>

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

.page-title {
    text-align: center;
    margin-bottom: 20px;
    color: #0f172a;
}

.form-control {
    border-radius: 8px;
}

.btn-update {
    background: #0f766e;
    border: none;
    color: white;
}

.btn-update:hover {
    background: #115e59;
}

.btn-cancel {
    background: #334155;
    border: none;
    color: white;
}

.btn-cancel:hover {
    background: #1e293b;
}

.alert-info {
    background: #e0f2fe;
    color: #075985;
    border: none;
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

            <div class="card-body">

                <h3 class="page-title">Edit Role</h3>

                <c:if test="${not empty message}">
                    <div class="alert alert-info text-center">
                        ${message}
                    </div>
                </c:if>

                <form:form method="post"
                           action="${pageContext.request.contextPath}/role/update"
                           modelAttribute="role">

                    <form:hidden path="id"/>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Role Name</label>

                        <form:input path="identifier"
                                    cssClass="form-control"
                                    readonly="true"/>

                        <small class="text-muted">
                            Role name cannot be changed
                        </small>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Description</label>

                        <form:textarea path="description"
                                       cssClass="form-control"
                                       rows="3"
                                       placeholder="Update role description"/>
                    </div>

                    <div class="d-flex gap-2">

                        <button type="submit"
                                class="btn btn-update btn-lg w-100">
                            Update Role
                        </button>

                        <a href="${pageContext.request.contextPath}/role/list"
                           class="btn btn-cancel btn-lg w-100 text-center">
                            Cancel
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