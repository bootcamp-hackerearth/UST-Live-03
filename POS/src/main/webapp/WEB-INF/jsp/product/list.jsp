<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Product Management</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            color: #2e2e2e;
        }

        .page-header {
            background-color: #000;
            padding: 14px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            color: #fff;
        }

        .card {
            border-radius: 10px;
            border: 1px solid #e0e0e0;
        }

        .card.shadow {
            box-shadow: 0 6px 18px rgba(0,0,0,0.06);
        }

        .table thead th {
            background-color: #f1f3f5;
            font-weight: 600;
            white-space: nowrap;
        }

        .form-check-input {
            cursor: pointer;
            transform: scale(1.2);
        }

        .action-icon {
            font-size: 1.2rem;
            margin: 0 8px;
            transition: transform 0.15s ease;
            text-decoration: none;
        }

        .action-icon:hover {
            transform: scale(1.2);
        }

        .edit-icon {
            color: #0d6efd;
        }

        .delete-icon {
            color: #dc3545;
        }

        .add-btn {
            background-color: #198754;
            color: #fff;
            border-radius: 6px;
        }
    </style>
</head>

<body>

<div class="container mt-4">

    <div class="page-header d-flex justify-content-between align-items-center">
        <h3>Product Management</h3>

        <div>
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary me-2">
                <i class="bi bi-house-door"></i> Home
            </a>

            <a href="${pageContext.request.contextPath}/product/add"
               class="btn add-btn">
                <i class="bi bi-plus-circle"></i> Add Product
            </a>
        </div>
    </div>

    <div class="card shadow">
        <div class="card-body p-0">

            <table class="table table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Identifier</th>
                    <th>Category</th>
                    <th>Brand</th>
                    <th>Model</th>
                    <th>Unit</th>
                    <th class="text-center">Status</th>
                    <th class="text-center" style="width:120px;">Actions</th>
                </tr>
                </thead>

                <tbody>

                <c:forEach items="${products}" var="p">
                    <tr>
                        <td>${p.id}</td>
                        <td class="fw-semibold">${p.identifier}</td>
                        <td>${p.category}</td>
                        <td>${p.brand}</td>
                        <td>${p.model}</td>
                        <td>${p.unit}</td>

                        <td class="text-center">
                            <form action="${pageContext.request.contextPath}/product/toggle"
                                  method="get">

                                <input type="hidden"
                                       name="identifier"
                                       value="${p.identifier}"/>

                                <div class="form-check form-switch d-flex justify-content-center">
                                    <input class="form-check-input"
                                           type="checkbox"
                                           ${p.status ? 'checked' : ''}
                                           onchange="this.form.submit()">
                                </div>

                            </form>
                        </td>

                        <td class="text-center">

                            <a href="${pageContext.request.contextPath}/product/get?identifier=${p.identifier}"
                               class="action-icon edit-icon">
                                <i class="bi bi-pencil-square"></i>
                            </a>

                            <a href="${pageContext.request.contextPath}/product/delete?identifier=${p.identifier}"
                               class="action-icon delete-icon"
                               onclick="return confirm('Delete this product?');">
                                <i class="bi bi-trash"></i>
                            </a>

                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty products}">
                    <tr>
                        <td colspan="10" class="text-center text-muted py-4">
                            No products found.
                        </td>
                    </tr>
                </c:if>

                </tbody>
            </table>

        </div>
    </div>

</div>

</body>
</html>
