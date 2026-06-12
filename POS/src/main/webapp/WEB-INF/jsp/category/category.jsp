<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Category</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            background-color: #f7f9fc;
            color: #1e293b;
        }

        .page-header {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #ffffff;
            padding: 20px;
            border-radius: 10px;
            margin: 20px auto;
            max-width: 480px;
            text-align: center;
        }

        .form-wrapper {
            max-width: 480px;
            margin: 0 auto;
        }

        .card-custom {
            border-radius: 12px;
            box-shadow: 0 10px 28px rgba(0,0,0,0.08);
            border: none;
        }

        label {
            font-weight: 600;
            font-size: 14px;
        }

        .form-control,
        .form-select {
            border-radius: 8px;
            padding: 10px 12px;
        }

        .form-control[readonly] {
            background-color: #f1f5f9;
        }

        .helper-text {
            font-size: 12px;
            color: #64748b;
        }

        .btn-gradient {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #ffffff;
            border: none;
            padding: 10px;
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
        <i class="bi bi-pencil-square me-2"></i> Edit Category
    </h4>
</div>

<div class="form-wrapper">
    <div class="card card-custom">
        <div class="card-body p-4">

            <!-- Error Message -->
            <c:if test="${not empty message}">
                <div class="alert alert-danger text-center">
                    <i class="bi bi-exclamation-triangle me-1"></i>
                    ${message}
                </div>
            </c:if>

            <form:form action="${pageContext.request.contextPath}/category/update"
                       method="post"
                       modelAttribute="category">

                <form:hidden path="id"/>

                <div class="mb-3">
                    <label>Category Name</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                readonly="true"/>
                    <div class="helper-text">
                        Category name cannot be changed
                    </div>
                </div>

                <div class="mb-4">
                    <label>Super Category</label>
                    <form:select path="superCategory" cssClass="form-select">
                        <form:option value="">-- None --</form:option>

                        <c:forEach var="cat" items="${categories}">
                            <c:if test="${cat.identifier ne category.identifier}">
                                <form:option value="${cat.identifier}">
                                    ${cat.identifier}
                                </form:option>
                            </c:if>
                        </c:forEach>
                    </form:select>
                    <div class="helper-text">
                        Only root categories can be selected
                    </div>
                </div>

                <div class="d-flex justify-content-end gap-2 mt-4">
                    <a href="${pageContext.request.contextPath}/category/list"
                       class="btn btn-outline-secondary">
                        Cancel
                    </a>

                    <button type="submit" class="btn btn-gradient">
                        <i class="bi bi-save me-1"></i> Update Category
                    </button>
                </div>

            </form:form>

        </div>
    </div>
</div>

</body>
</html>
