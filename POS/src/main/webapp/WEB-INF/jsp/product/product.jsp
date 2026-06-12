<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Product</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            background: #f4f7f6;
            font-family: 'Segoe UI', sans-serif;
            padding: 40px;
        }

        .card {
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.1);
        }

        .header {
            background: #0d6efd;
            color: white;
            padding: 18px;
            border-radius: 12px 12px 0 0;
        }

        .btn-update {
            background: #0d6efd;
            color: white;
            width: 100%;
            border-radius: 10px;
            padding: 12px;
        }
    </style>
</head>

<body>

<div class="container">

    <!-- BACK BUTTON -->
    <a href="${pageContext.request.contextPath}/product/list" class="btn btn-secondary mb-3">
        ← Back
    </a>

    <div class="card">

        <div class="header">
            <h5>Edit Product - ${product.identifier}</h5>
        </div>

        <div class="p-4">

            <!-- MESSAGE -->
            <c:if test="${not empty message}">
                <div class="alert alert-danger">${message}</div>
            </c:if>

            <form:form method="post"
                       action="${pageContext.request.contextPath}/product/update"
                       modelAttribute="product">

                <!-- ID -->
                <form:hidden path="id"/>

                <!-- IDENTIFIER -->
                <div class="mb-3">
                    <label>Identifier</label>
                    <form:input path="identifier"
                                class="form-control"
                                readonly="true"/>
                </div>

                <!-- ✅ PRODUCT NAME -->
                <div class="mb-3">
                    <label>Product Name</label>
                    <form:input path="productName"
                                class="form-control"
                                required="true"/>
                </div>

                <!-- ✅ DESCRIPTION -->
                <div class="mb-3">
                    <label>Description</label>
                    <form:textarea path="description"
                                   class="form-control"/>
                </div>

                <!-- CATEGORY -->
                <div class="mb-3">
                    <label>Category</label>
                    <form:select path="category" class="form-select">
                        <c:forEach items="${category}" var="c">
                            <form:option value="${c.identifier}">
                                ${c.identifier}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </div>

                <!-- BRAND -->
                <div class="mb-3">
                    <label>Brand</label>
                    <form:select path="brand" class="form-select">
                        <c:forEach items="${brand}" var="b">
                            <form:option value="${b.identifier}">
                                ${b.identifier}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </div>

                <!-- MODEL -->
                <div class="mb-3">
                    <label>Model</label>
                    <form:select path="models" class="form-select">
                        <c:forEach items="${models}" var="m">
                            <form:option value="${m.identifier}">
                                ${m.identifier}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </div>

                <!-- STATUS -->
                <div class="mb-3">
                    <label>Status</label>
                    <form:select path="status" class="form-select">
                        <form:option value="true">Active</form:option>
                        <form:option value="false">Inactive</form:option>
                    </form:select>
                </div>

                <!-- BUTTON -->
                <button type="submit" class="btn-update">
                    Update Product
                </button>

            </form:form>

        </div>

    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
