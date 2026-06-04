<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Role</title>

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
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .edit-card {
            width: 460px;
            background: #ffffff;
            padding: 35px 40px;
            border-radius: 18px;
            box-shadow: 0 18px 45px rgba(0, 0, 0, 0.25);
        }

        h4 {
            text-align: center;
            margin-bottom: 25px;
            font-weight: 600;
            color: #333;
        }

        .form-label {
            font-weight: 600;
            font-size: 0.9rem;
            color: #555;
        }

        .form-control {
            border-radius: 10px;
            padding: 10px 14px;
            font-size: 0.9rem;
        }

        .help-text {
            font-size: 0.75rem;
            color: #777;
            margin-top: 6px;
            line-height: 1.4;
        }

        .btn-update {
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            border: none;
            color: #ffffff;
            font-weight: 600;
            border-radius: 25px;
            padding: 10px 26px;
            transition: all 0.3s ease;
        }

        .btn-update:hover {
            background: linear-gradient(90deg, #00c6ff, #0072ff);
            transform: translateY(-1px);
        }

        .btn-cancel {
            border-radius: 25px;
            padding: 10px 26px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="edit-card">

    <h4>Edit Role</h4>

    <c:if test="${empty role}">
        <div class="alert alert-danger text-center">
            Role not found
        </div>
    </c:if>

    <c:if test="${not empty role}">
        <form:form action="/role/update"
                   method="post"
                   modelAttribute="role">

            <form:hidden path="id" />

            <div class="mb-4">
                <label class="form-label">Role Name</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            readonly="true" />

                <div class="help-text">
                    This role name is a system identifier and cannot be edited,
                    as it is used internally for authorization and access control.
                </div>
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold">Description</label>

                <form:input path="description"
                            cssClass="form-control"
                            placeholder="Enter role"
                            required="true"/>
            </div>


            <div class="d-flex justify-content-between">
                <a href="/role/list" class="btn btn-outline-secondary btn-cancel">
                    Cancel
                </a>

                <button type="submit" class="btn btn-update">
                    Update Role
                </button>
            </div>

        </form:form>
    </c:if>

</div>

</body>
</html>
