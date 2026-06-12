<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Role</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            min-height: 100vh;

            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);

            display: flex;
            justify-content: center;
            align-items: center;
            font-family: 'Segoe UI', sans-serif;
        }

        .card {
            width: 400px;
            border-radius: 15px;
            border: 1px solid #e5e7eb;
            background: rgba(255, 255, 255, 0.9);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
        }

        h4 {
            font-weight: 600;
            color: #111827;
        }

        .form-label {
            color: #374151;
            font-weight: 500;
        }

        .form-control {
            border: 1px solid #d1d5db;
        }

        .btn-primary {
            background: #3b82f6;
            border: none;
        }

        .btn-primary:hover {
            background: #2563eb;
        }

        .btn-outline-secondary {
            color: #374151;
            border-color: #9ca3af;
        }

        .btn-outline-secondary:hover {
            background: #e5e7eb;
        }
    </style>
</head>

<body>

${message}

<div class="card shadow-lg">
    <div class="card-body">

        <h4 class="text-center mb-4">Edit Role</h4>

        <c:if test="${empty role}">
            <div class="alert alert-danger text-center">
                Role not found
            </div>
        </c:if>

        <c:if test="${not empty role}">
            <form:form action="/role/update"
                       method="post"
                       modelAttribute="role">

                <form:hidden path="id" value="${role.id}"/>

                <div class="mb-4">
                    <label class="form-label">Role Name</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                placeholder="Enter role"
                                required="true"
                                readOnly="true"/>
                </div>

                <div class="mb-4">
                    <label class="form-label">Description</label>
                    <form:input path="description"
                                cssClass="form-control"
                                placeholder="Enter role"
                                required="true"/>
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