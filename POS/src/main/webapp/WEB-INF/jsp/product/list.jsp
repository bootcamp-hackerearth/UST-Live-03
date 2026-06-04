<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Product Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        body {
            background-color: #f7f9fc;
        }

        .page-header {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #fff;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
        }

        .table thead th {
            text-transform: uppercase;
            font-size: 13px;
        }

        .form-switch .form-check-input {
            width: 2.8rem;
            height: 1.4rem;
            cursor: pointer;
        }

        .form-switch .form-check-input:checked {
            background-color: #dc3545 !important;
            border-color: #dc3545 !important;
        }

        .form-switch .form-check-input:not(:checked){
            background-color: #198754 !important;
            border-color: #198754 !important;
        }

        .form-check-input:focus {
            box-shadow: none;
        }
    </style>
</head>

<body class="container py-4">

<div class="page-header d-flex justify-content-between align-items-center">
    <h4 class="mb-0">
        <i class="bi bi-box-seam me-2"></i> Product Management
    </h4>

    <div>
        <a href="${pageContext.request.contextPath}/" class="btn btn-light btn-sm me-2">
            <i class="bi bi-house-door-fill me-1"></i> Home
        </a>
        <a href="${pageContext.request.contextPath}/product/add" class="btn btn-light btn-sm">
            <i class="bi bi-plus-circle me-1"></i> Add Product
        </a>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body">

        <table class="table table-hover align-middle">
            <thead class="table-light">
            <tr>
                <th>ID</th>
                <th>Identifier</th>
                <th>Category</th>
                <th>Brand</th>
                <th>Model</th>
                <th>Unit</th>
                <th class="text-center">Status</th>
                <th class="text-center">Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach items="${products}" var="p">
                <tr>
                    <td>${p.id}</td>
                    <td>${p.identifier}</td>
                    <td>${p.category}</td>
                    <td>${p.brand}</td>
                    <td>${p.model}</td>
                    <td>${p.unit}</td>

                    <td class="text-center">
                        <form action="${pageContext.request.contextPath}/product/toggle"
                              method="get">

                            <input type="hidden" name="identifier" value="${p.identifier}" />

                            <div class="form-check form-switch d-flex justify-content-center">
                                <input class="form-check-input"
                                       type="checkbox"
                                       ${p.status == 'ACTIVE' ? 'checked' : ''}
                                       onchange="this.form.submit()">
                            </div>
                        </form>
                    </td>

                    <td class="text-center">
                        <a href="${pageContext.request.contextPath}/product/get?identifier=${p.identifier}"
                           class="btn btn-sm btn-outline-primary">
                            <i class="bi bi-pencil"></i>
                        </a>

                        <a href="${pageContext.request.contextPath}/product/delete?identifier=${p.identifier}"
                           class="btn btn-sm btn-outline-danger"
                           onclick="return confirm('Delete this product?');">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty products}">
                <tr>
                    <td colspan="10" class="text-center text-muted">No products found</td>
                </tr>
            </c:if>

            </tbody>
        </table>

    </div>
</div>

</body>
</html>