<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Shelf</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #f7f9fc;
        }

        .page-header {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #ffffff;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            max-width: 480px;
            margin-left: auto;
            margin-right: auto;
            text-align: center;
        }

        .form-wrapper {
            max-width: 480px;
            margin: 0 auto;
        }

        label {
            font-weight: 600;
            font-size: 14px;
        }

        .form-control,
        .form-select {
            border-radius: 8px;
        }

        .btn-gradient {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #ffffff;
            border: none;
        }

        .btn-gradient:hover {
            background: linear-gradient(to right, #134e4a, #0f766e);
            color: #ffffff;
        }
    </style>
</head>

<body class="container py-4">

<div class="page-header">
    <h4 class="mb-0">
        <i class="bi bi-layout-text-window-reverse me-2"></i> Add Shelf
    </h4>
</div>

<div class="form-wrapper">
    <div class="card shadow-sm">
        <div class="card-body">

            <c:if test="${not empty message}">
                <div class="alert alert-danger text-center">
                    ${message}
                </div>
            </c:if>

            <form:form method="post"
                       action="${pageContext.request.contextPath}/shelf/add"
                       modelAttribute="shelf">

                <form:hidden path="id"/>

                <div class="mb-3">
                    <label>Shelf Name</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                placeholder="Enter shelf name"
                                required="true"/>
                </div>

                <div class="mb-3">
                    <label>Status</label>
                    <form:select path="status" cssClass="form-select" required="true">
                        <form:option value="true">ACTIVE</form:option>
                        <form:option value="false">INACTIVE</form:option>
                    </form:select>
                </div>

                <div class="d-flex justify-content-end gap-2 mt-4">
                    <a href="${pageContext.request.contextPath}/shelf/list"
                       class="btn btn-outline-secondary">
                        Cancel
                    </a>

                    <button type="submit" class="btn btn-gradient">
                        <i class="bi bi-save me-1"></i> Save Shelf
                    </button>
                </div>

            </form:form>

        </div>
    </div>
</div>

</body>
</html>