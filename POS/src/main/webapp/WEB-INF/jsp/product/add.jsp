<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Management System | Add Product</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: 'Segoe UI', sans-serif;
        }

        .product-card {
            width: 100%;
            max-width: 550px;
            background: #fff;
            border-radius: 16px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.3);
        }

        .card-header {
            background: #f8f9fa;
            padding: 20px;
            text-align: center;
            font-weight: bold;
        }

        .card-body {
            padding: 30px;
        }

        .form-control, .form-select {
            border-radius: 8px;
        }

        .btn-submit {
            background: #28a745;
            color: white;
            border-radius: 8px;
        }

        .btn-cancel {
            background: #6c757d;
            color: white;
            text-decoration: none;
            padding: 10px 20px;
            border-radius: 8px;
        }
    </style>
</head>

<body>

<div class="product-card">

    <div class="card-header">
        <h3>Add New Product</h3>
    </div>

    <div class="card-body">

        <!-- MESSAGE -->
        <c:if test="${not empty message}">
            <div class="alert ${success ? 'alert-success' : 'alert-danger'}">
                ${message}
            </div>
        </c:if>

        <form:form action="${pageContext.request.contextPath}/product/add"
                   method="post"
                   modelAttribute="productDto">

            <form:hidden path="id"/>

            <!-- ✅ PRODUCT NAME (NEW FIELD) -->
            <div class="mb-3">
                <label class="form-label">Product Name</label>
                <form:input path="productName"
                            cssClass="form-control"
                            placeholder="Enter product name"
                            required="true"/>
            </div>

            <!-- IDENTIFIER -->
            <div class="mb-3">
                <label class="form-label">Product Identifier</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            placeholder="e.g. SKU-9901"
                            required="true"/>
            </div>

            <!-- CATEGORY -->
            <div class="mb-3">
                <label class="form-label">Category</label>
                <form:select path="category" cssClass="form-select" required="true">
                    <option value="" disabled selected>-- Select Category --</option>

                    <c:forEach var="c" items="${category}">
                        <option value="${c.identifier}">
                            ${c.identifier}
                        </option>
                    </c:forEach>
                </form:select>
            </div>

            <!-- BRAND -->
            <div class="mb-3">
                <label class="form-label">Brand</label>
                <form:select path="brand" class="form-select">
                    <form:option value="" label="-- Select Brand --"/>
                    <c:forEach items="${brand}" var="b">
                        <form:option value="${b.identifier}">
                            ${b.identifier}
                        </form:option>
                    </c:forEach>
                </form:select>
            </div>

            <!-- MODEL -->
            <div class="mb-3">
                <label class="form-label">Model</label>
                <form:select path="models" class="form-select">
                    <form:option value="" label="-- Select Model --"/>
                    <c:forEach items="${models}" var="m">
                        <form:option value="${m.identifier}">
                            ${m.identifier}
                        </form:option>
                    </c:forEach>
                </form:select>
            </div>

            <!-- ✅ DESCRIPTION -->
            <div class="mb-4">
                <label class="form-label">Description</label>
                <form:textarea path="description"
                               cssClass="form-control"
                               rows="3"/>
            </div>

            <!-- BUTTONS -->
            <div class="d-flex justify-content-between">
                <a href="${pageContext.request.contextPath}/product/list" class="btn-cancel">
                    Back
                </a>

                <button type="submit" class="btn-submit">
                    Save Product
                </button>
            </div>

        </form:form>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
