<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Category</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Roboto, Arial;
        }

        .card {
            border-radius: 10px;
            border: 1px solid #ddd;
            max-width: 520px;
            margin: 60px auto;
        }

        .card-header {
            background-color: #000;
            color: #fff;
            text-align: center;
            font-weight: 600;
            padding: 14px;
        }

        label {
            font-weight: 600;
            font-size: 14px;
        }

        .form-control,
        .form-select {
            border-radius: 6px;
        }
    </style>
</head>

<body>

<div class="card shadow-sm">

    <div class="card-header">
        <i class="bi bi-tags me-2"></i> Add Category
    </div>

    <div class="card-body">

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form:form action="${pageContext.request.contextPath}/category/add"
                   method="post"
                   modelAttribute="category">

            <div class="mb-3">
                <label>Category Name</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            placeholder="Enter category name"
                            required="required"/>
            </div>

            <div class="mb-3">
                <label>Super Category</label>
                <form:select path="superCategory"
                             cssClass="form-select">

                    <form:option value="">-- None --</form:option>

                    <c:forEach var="cat" items="${categories}">
                        <form:option value="${cat.identifier}">
                            ${cat.identifier}
                        </form:option>
                    </c:forEach>

                </form:select>
            </div>

            <div class="d-grid gap-2 mt-4">
                <button type="submit" class="btn btn-success">
                    <i class="bi bi-save me-1"></i> Save Category
                </button>

                <a href="${pageContext.request.contextPath}/category/list"
                   class="btn btn-secondary">
                    Cancel
                </a>
            </div>

        </form:form>

    </div>
</div>

</body>
</html>