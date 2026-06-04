<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Brand</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
        }

        .card {
            border-radius: 10px;
            border: 1px solid #ddd;
            max-width: 480px;
            margin: 60px auto;
            box-shadow: 0 6px 18px rgba(0,0,0,.08);
        }

        .card-header {
            background-color: #000;
            color: #fff;
            text-align: center;
            font-weight: 600;
            padding: 14px;
        }

        .form-control,
        textarea {
            border-radius: 6px;
        }
    </style>
</head>

<body>

<div class="card">

    <div class="card-header">
        Add Brand
    </div>

    <div class="card-body">

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form:form action="${pageContext.request.contextPath}/brand/add"
                   method="post"
                   modelAttribute="brand">

            <input type="hidden"
                   name="${_csrf.parameterName}"
                   value="${_csrf.token}" />

            <div class="mb-3">
                <label class="form-label">Brand Name</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            placeholder="Enter brand name"
                            required="required"/>
            </div>

            <div class="mb-4">
                <label class="form-label">Description</label>
                <form:textarea path="description"
                               cssClass="form-control"
                               rows="3"
                               placeholder="Enter description"/>
            </div>

            <div class="d-grid gap-2">
                <button type="submit" class="btn btn-success">
                    Save
                </button>

                <a href="${pageContext.request.contextPath}/brand/list"
                   class="btn btn-secondary">
                    Back
                </a>
            </div>

        </form:form>

    </div>
</div>

</body>
</html>