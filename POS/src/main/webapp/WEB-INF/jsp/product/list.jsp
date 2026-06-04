<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS | Product Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        :root {
            --primary-blue: #0d6efd;
            --bg-light: #f4f7f6;
            --text-dark: #2c3e50;
            --border-color: #e2e8f0;
            --shadow-soft: 0 4px 14px rgba(0,0,0,0.06);
            --shadow-deep: 0 12px 30px rgba(0,0,0,0.08);
        }

        body {
            background-color: var(--bg-light);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            color: var(--text-dark);
            min-height: 100vh;
            padding: 40px 0;
        }

        .page-header {
            background: #ffffff;
            padding: 22px 26px;
            border-radius: 14px;
            box-shadow: var(--shadow-soft);
            margin-bottom: 28px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .table-container {
            background: #ffffff;
            border-radius: 14px;
            padding: 25px;
            box-shadow: var(--shadow-deep);
            border: none;
        }

        .table thead th {
            background-color: var(--primary-blue) !important;
            color: #ffffff !important;
            padding: 16px;
            font-weight: 600;
            border: none;
            white-space: nowrap;
        }

        .table tbody td {
            padding: 14px 16px;
            vertical-align: middle;
            border-bottom: 1px solid #f1f4f8;
        }

        .product-desc {
            font-weight: 600;
            color: var(--text-dark);
            max-width: 180px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .identifier-badge {
            font-size: 0.8rem;
            font-weight: 600;
            color: var(--primary-blue);
            background: #eef3ff;
            padding: 4px 8px;
            border-radius: 6px;
            border: 1px dashed rgba(13, 110, 253, 0.3);
        }

        .brand-badge { background-color: #f0fdf4; color: #166534; border: 1px solid #bbf7d0; padding: 4px 10px; border-radius: 6px; font-size: 0.8rem; font-weight: 600; }
        .model-badge { background-color: #faf5ff; color: #6b21a8; border: 1px solid #f3e8ff; padding: 4px 10px; border-radius: 6px; font-size: 0.8rem; font-weight: 600; }
        .category-badge { background-color: #f1f5f9; color: #475569; border: 1px solid var(--border-color); padding: 4px 10px; border-radius: 6px; font-size: 0.8rem; font-weight: 600; }

        .btn-action {
            padding: 7px 14px;
            font-weight: 700;
            font-size: 0.75rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            border-radius: 8px;
            transition: all 0.2s ease;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            border-width: 1px;
            border-style: solid;
        }

        .btn-edit {
            background-color: #fff9db;
            color: #856404;
            border-color: #ffeeba;
        }

        .btn-edit:hover {
            background-color: #ffe066;
            color: #533f03;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.05);
        }

        .btn-delete {
            background-color: #fff5f5;
            color: #c92a2a;
            border-color: #ffa8a8;
        }

        .btn-delete:hover {
            background-color: #ffc9c9;
            color: #a61e1e;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.05);
        }

        .form-check-input:checked { background-color: #10b981 !important; border-color: #10b981 !important; }
        .status-label { font-size: 0.75rem; font-weight: 700; }

        .btn-add {
            background-color: var(--primary-blue);
            color: white;
            font-weight: 600;
            border-radius: 10px;
            padding: 8px 20px;
            transition: all 0.3s ease;
        }
        .btn-add:hover { background-color: #0056b3; color: white; transform: scale(1.02); }
    </style>
</head>
<body>

<div class="container-fluid px-5">

    <div class="page-header">
        <h3 class="mb-0 fw-bold text-primary">
            <i class="bi bi-box-seam me-2"></i>Product Management
        </h3>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary btn-sm px-3">
                <i class="bi bi-house-door"></i> Home
            </a>
            <a href="${pageContext.request.contextPath}/product/add" class="btn btn-add btn-sm">
                <i class="bi bi-plus-circle me-1"></i> Add New Product
            </a>
        </div>
    </div>

    <div class="table-container">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead>
                    <tr>
                        <th class="text-center">ID</th>
                        <th>Identifier</th>
                        <th>Description</th>
                        <th>Category</th>
                        <th>Brand</th>
                        <th>Model</th>
                        <th class="text-center">Status</th>
                        <th class="text-center">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="product" items="${products}">
                        <tr>
                            <td class="text-center text-muted fw-bold">#${product.id}</td>

                            <td>
                                <span class="identifier-badge">
                                    <i class="bi bi-hash"></i>${product.identifier}
                                </span>
                            </td>

                            <td>
                                <div class="product-desc" title="${product.description}">
                                    ${product.description}
                                </div>
                            </td>

                            <td>
                                <span class="category-badge">${product.category}</span>
                            </td>

                            <td>
                                <span class="brand-badge">
                                    <i class="bi bi-award me-1"></i>${product.brand}
                                </span>
                            </td>

                            <td>
                                <span class="model-badge">
                                    <i class="bi bi-cpu me-1"></i>${product.models}
                                </span>
                            </td>

                            <td class="text-center">
                                <form method="get" action="${pageContext.request.contextPath}/product/toggle" class="d-inline">
                                    <input type="hidden" name="identifier" value="${product.identifier}">
                                    <input type="hidden" name="status" value="${!product.status}">

                                    <div class="form-check form-switch d-flex justify-content-center align-items-center gap-2">
                                        <input class="form-check-input" type="checkbox" role="switch"
                                               ${product.status ? "checked" : ""}
                                               onchange="this.form.submit()">
                                        <span class="status-label ${product.status ? 'text-success' : 'text-danger'}">
                                            ${product.status ? 'ACTIVE' : 'INACTIVE'}
                                        </span>
                                    </div>
                                </form>
                            </td>

                            <td class="text-center">
                                <div class="d-flex justify-content-center gap-2">
                                    <a href="${pageContext.request.contextPath}/product/get?identifier=${product.identifier}"
                                       class="btn-action btn-edit">
                                        <i class="bi bi-pencil-square"></i> Edit
                                    </a>

                                    <a href="${pageContext.request.contextPath}/product/delete?identifier=${product.identifier}"
                                       class="btn-action btn-delete"
                                       onclick="return confirm('Confirm deletion of Product: ${product.identifier}?')">
                                        <i class="bi bi-trash3"></i> Delete
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty products}">
                        <tr>
                            <td colspan="9" class="text-center py-5 text-muted fw-bold">
                                No product records found.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>