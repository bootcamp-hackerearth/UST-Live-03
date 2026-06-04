<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Brand</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
        }

        .card {
            border-radius: 10px;
            border: 1px solid #e0e0e0;
        }

        .card-header {
            background-color: #000000;
            color: #ffffff;
            font-weight: 600;
            text-align: center;
            padding: 14px;
        }

        .form-control {
            border-radius: 6px;
        }
    </style>
</head>
<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">

            <div class="card shadow">
                <div class="card-header">
                    Edit Brand
                </div>

                <div class="card-body">

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger text-center">
                            ${message}
                        </div>
                    </c:if>

                    <form:form method="post"
                               action="${pageContext.request.contextPath}/brand/update"
                               modelAttribute="brand"
                               enctype="multipart/form-data">

                        <form:hidden path="id"/>

                        <div class="mb-3">
                            <label class="form-label">Brand Name</label>
                            <form:input path="identifier"
                                        cssClass="form-control"
                                        required="true"
                                        readonly="true"/>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Description</label>
                            <form:textarea path="description"
                                           cssClass="form-control"
                                           rows="3"
                                           placeholder="Describe the brand"/>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-circle"></i> Update Brand
                            </button>

                            <a href="${pageContext.request.contextPath}/brand/list"
                               class="btn btn-secondary">
                                Cancel
                            </a>
                        </div>

                    </form:form>

                </div>
            </div>

        </div>
    </div>
</div>

</body>
</html>