<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html land="en">
<head>
    <title>Edit Role</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <style>
        body {
            min-height: 100vh;
            background-color: #FFF8F0;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .card {
            width: 400px;
            border-radius: 18px;
            border: none;
            box-shadow: 0 18px 35px rgba(75, 46, 43, 0.25);
            background-color: #ffffff;
        }

        h4 {
            font-weight: 600;
            color: #4B2E2B;
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
        }

        .btn-primary:hover {
            background-color: #3a2421;
        }

        .btn-outline-secondary {
            border-color: #4B2E2B;
            color: #4B2E2B;
            font-weight: 600;
        }

        .btn-outline-secondary:hover {
            background-color: #4B2E2B;
            color: #FFF8F0;
        }
    </style>
</head>
<body>
<div class="card shadow-lg">
    <div class="card-body">
        <h4 class="text-center mb-4">Edit Role</h4>
        <c:if test="${empty role}">
            <div class="alert alert-danger text-center">
                Role not found
            </div>
        </c:if>
        <c:if test="${not empty role}">
            <form:form action="/role/update" method="post" modelAttribute="roleDto">
                <form:hidden path="id" value="${role.id}"/>
                <div class="mb-4">
                    <label class="form-label"><b>Role Name</b></label>
                    <form:input path="identifier" cssClass="form-control" readonly="true"/>
                </div>
                <div class="mb-4">
                    <label class="form-label"><b>Description</b></label>
                    <form:input path="description"
                        cssClass="form-control"
                        value="${role.description}"
                        rows="3"
                        required="true"
                    />
                </div>
                <div class="d-flex justify-content-between">
                    <a href="/role/list" class="btn btn-outline-secondary">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-primary">
                        Update
                    </button>
                </div>
            </form:form>
        </c:if>
    </div>
</div>
</body>
</html>