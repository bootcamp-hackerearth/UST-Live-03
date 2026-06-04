<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Product</title>
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
        <i class="bi bi-box-seam me-2"></i> Edit Product
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
                       action="${pageContext.request.contextPath}/product/update"
                       modelAttribute="product">

                <form:hidden path="id"/>

                <div class="mb-3">
                    <label>Product Identifier</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                readonly="true"/>
                </div>

                <div class="mb-3">
                    <label>Category</label>
                    <form:select path="category" cssClass="form-select" required="true">
                        <form:option value="" label="-- Select Category --"/>
                        <c:forEach items="${categories}" var="c">
                            <form:option value="${c.identifier}">
                                ${c.identifier}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </div>

                <div class="mb-3">
                    <label>Brand</label>
                    <form:select path="brand" cssClass="form-select" required="true">
                        <form:option value="" label="-- Select Brand --"/>
                        <c:forEach items="${brands}" var="b">
                            <form:option value="${b.identifier}">
                                ${b.identifier}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </div>

                <div class="mb-3">
                    <label>Model</label>
                    <form:select path="model" cssClass="form-select" required="true">
                        <form:option value="" label="-- Select Model --"/>
                        <c:forEach items="${models}" var="m">
                            <form:option value="${m.identifier}">
                                ${m.identifier}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </div>

                <div class="mb-3">
                    <label>Unit</label>
                    <form:select path="unit" cssClass="form-select" required="true">
                        <form:option value="" label="-- Select Unit --"/>
                        <c:forEach items="${units}" var="u">
                            <form:option value="${u.identifier}">
                                ${u.identifier}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </div>

                <div class="mb-3">
                    <label>Quantity</label>
                    <form:input path="quantity"
                                type="number"
                                min="0"
                                cssClass="form-control"
                                required="true"/>
                </div>

                <div class="mb-3">
                    <label>Status</label>
                    <form:select path="status" cssClass="form-select">
                        <form:option value="true">ACTIVE</form:option>
                        <form:option value="false">INACTIVE</form:option>
                    </form:select>
                </div>

                <div class="d-flex justify-content-end gap-2 mt-4">
                    <a href="${pageContext.request.contextPath}/product/list"
                       class="btn btn-outline-secondary">
                        Cancel
                    </a>

                    <button type="submit" class="btn btn-gradient">
                        <i class="bi bi-save me-1"></i> Update Product
                    </button>
                </div>

            </form:form>

        </div>
    </div>
</div>

</body>
</html>