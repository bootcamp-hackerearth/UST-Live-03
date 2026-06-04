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
            background: linear-gradient(135deg, #667eea, #764ba2);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            width: 400px;
            border-radius: 15px;
        }

        h4 {
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="card shadow-lg">
    <div class="card-body">

        <h4 class="text-center mb-4 text-primary">Edit Role</h4>

        <c:if test="${empty role}">
            <div class="alert alert-danger text-center">
                Role not found
            </div>
        </c:if>

        <c:if test="${not empty role}">
            <form:form action="/role/update"
                       method="post"
                       modelAttribute="roleDto">

                <form:hidden path="id" value="${role.id}"/>

                <div class="mb-4">
                    <label class="form-label">Role Name(Identifier:should not be editable)</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                placeholder="Enter role"
                                readonly="true"/>
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