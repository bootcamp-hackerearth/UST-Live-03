<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Node</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        body {
            min-height: 100vh;
            background-color: #f6f7fb;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .page-container {
            max-width: 640px;
            width: 100%;
        }

        .card-custom {
            background: #fff;
            border-radius: 16px;
            padding: 30px 35px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            font-weight: 600;
            margin-bottom: 25px;
        }

        .form-label {
            font-weight: 600;
            font-size: 14px;
        }

        .form-control {
            border-radius: 8px;
            padding: 10px 12px;
        }

        .roles-box {
            background: #f8f9fa;
            border-radius: 12px;
            padding: 15px;
        }

        .form-check {
            margin-bottom: 8px;
        }

        .btn-success {
            padding: 12px 32px;
            border-radius: 30px;
            font-weight: 600;
        }

        .btn-secondary {
            padding: 12px 32px;
            border-radius: 30px;
            font-weight: 600;
        }

        .action-buttons {
            text-align: center;
            margin-top: 30px;
        }
    </style>
</head>

<body>

<div class="page-container">
    <div class="card-custom">

        <h2>
            <i class="bi bi-plus-circle"></i>
            Add Node
        </h2>

        <p class="text-danger text-center">${message}</p>

        <form:form method="post"
                   action="${pageContext.request.contextPath}/node/add"
                   modelAttribute="nodeDto">

            <div class="mb-3">
                <label class="form-label">Identifier</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            placeholder="USER"
                            required="required"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Node Path</label>
                <form:input path="path"
                            cssClass="form-control"
                            placeholder="/user/**"
                            required="required"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Assign Roles</label>

                <div class="roles-box">
                    <c:forEach items="${roles}" var="role">
                        <div class="form-check">
                            <form:checkbox path="roles"
                                           value="${role.identifier}"
                                           cssClass="form-check-input"
                                           id="role_${role.identifier}"/>

                            <label class="form-check-label" for="role_${role.identifier}">
                                ${role.identifier}
                            </label>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <div class="action-buttons">
                <button type="submit" class="btn btn-success me-3">
                    <i class="bi bi-check-circle"></i>
                    Add Node
                </button>

                <a href="${pageContext.request.contextPath}/node/list"
                   class="btn btn-secondary">
                    <i class="bi bi-arrow-left-circle"></i>
                    Cancel
                </a>
            </div>

        </form:form>

    </div>
</div>

</body>
</html>