<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Product Management</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        .add-product-btn {
            font-size: 16px;
            padding: 10px 25px;
            border-radius: 25px;
        }

        .card-custom {
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            border-radius: 12px;
        }

        .badge-category {
            background-color: #4f46e5;
            margin-right: 4px;
        }
    </style>
</head>

<body>

<div class="container mt-4">

    <div class="card card-custom p-4">

        <h2 class="text-center mb-4">Product Management</h2>

        <div class="text-center mb-4">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary me-2">
                <i class="bi bi-arrow-left-circle"></i> Home
            </a>

            <a href="${pageContext.request.contextPath}/product/add"
               class="btn btn-success add-product-btn">
                <i class="bi bi-plus-circle"></i> Add Product
            </a>
        </div>

        <c:if test="${empty products}">
            <div class="text-center text-muted p-5">
                No products available
            </div>
        </c:if>

        <c:if test="${not empty products}">
            <table class="table table-hover table-bordered align-middle">

                <thead class="table-dark text-center">
                <tr>
                    <th>ID</th>
                    <th>SKU</th>
                    <th>Categories</th>
                    <th>Brand</th>
                    <th>Unit</th>
                    <th>Model</th>
                    <th>Name</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="product" items="${products}">
                    <tr>

                        <td class="text-center">${product.id}</td>
                        <td>${product.identifier}</td>
                        <td class="text-center">
                            <c:forEach var="cat" items="${product.categories}">
                                <span class="badge badge-category text-white">
                                    ${cat}
                                </span>
                            </c:forEach>
                        </td>

                        <td class="text-center">
                            ${empty product.brand ? '-' : product.brand}
                        </td>

                        <td class="text-center">
                            ${empty product.unit ? '-' : product.unit}
                        </td>

                        <td class="text-center">
                            ${empty product.model ? '-' : product.model}
                        </td>

                        <td class="text-center">${product.name}</td>
                        <td class="text-center">
                            <form method="post"
                                  action="${pageContext.request.contextPath}/product/toggle">

                                <input type="hidden"
                                       name="identifier"
                                       value="${product.identifier}" />

                                <div class="form-check form-switch d-flex justify-content-center align-items-center">
                                    <input class="form-check-input me-2"
                                           type="checkbox"
                                           ${product.status ? "checked" : ""}
                                           onchange="this.form.submit()" />

                                    <span class="${product.status ? 'text-success' : 'text-danger'} fw-semibold">
                                        ${product.status ? "Active" : "Inactive"}
                                    </span>
                                </div>
                            </form>
                        </td>
                        <td class="text-center">

                            <a href="${pageContext.request.contextPath}/product/get?identifier=${product.identifier}"
                               class="btn btn-sm btn-warning">
                                <i class="bi bi-pencil-square"></i>
                            </a>

                            <a href="${pageContext.request.contextPath}/product/delete?identifier=${product.identifier}"
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('Delete this product?')">
                                <i class="bi bi-trash"></i>
                            </a>

                        </td>

                    </tr>
                </c:forEach>
                </tbody>

            </table>
        </c:if>

    </div>

</div>

</body>
</html>