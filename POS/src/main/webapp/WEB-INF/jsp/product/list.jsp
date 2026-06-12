<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS | Product Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            background-color: #f4f7f6;
            font-family: 'Segoe UI', sans-serif;
            padding: 40px;
        }

        .page-header {
            background: #fff;
            padding: 20px;
            border-radius: 12px;
            margin-bottom: 20px;
        }

        .table-container {
            background: #fff;
            padding: 20px;
            border-radius: 12px;
        }

        .badge-style {
            padding: 4px 10px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 600;
        }

        .category { background: #e9ecef; }
        .brand { background: #d1f7d6; }
        .model { background: #ede1ff; }

        .btn-action {
            padding: 5px 10px;
            border-radius: 6px;
            font-size: 13px;
        }
    </style>
</head>

<body>

<div class="container">

    <!-- ✅ HEADER -->
    <div class="page-header d-flex justify-content-between">
        <h4>Product Management</h4>

        <div>
            <a href="${pageContext.request.contextPath}/" class="btn btn-secondary btn-sm">Home</a>
            <a href="${pageContext.request.contextPath}/product/add" class="btn btn-primary btn-sm">Add Product</a>
        </div>
    </div>

    <!-- ✅ TABLE -->
    <div class="table-container">
        <table class="table table-bordered table-striped">

            <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Identifier</th>
                <th>Product Name</th> <!-- ✅ ADDED -->
                <th>Description</th>
                <th>Category</th>
                <th>Brand</th>
                <th>Model</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>

            <c:forEach var="product" items="${products}">
                <tr>

                    <td>#${product.id}</td>

                    <td>${product.identifier}</td>

                    <!-- ✅ NEW FIELD -->
                    <td><strong>${product.productName}</strong></td>

                    <td>${product.description}</td>

                    <td>
                        <span class="badge-style category">
                            ${product.category}
                        </span>
                    </td>

                    <td>
                        <span class="badge-style brand">
                            ${product.brand}
                        </span>
                    </td>

                    <td>
                        <span class="badge-style model">
                            ${product.models}
                        </span>
                    </td>

                    <!-- ✅ TOGGLE STATUS -->
                                        <td class="text-center">
                                            <form method="get"
                                                  action="${pageContext.request.contextPath}/product/toggle"
                                                  class="d-inline">
                    
                                                <input type="hidden" name="identifier" value="${product.identifier}"/>
                                                <input type="hidden" name="status" value="${!product.status}"/>
                    
                                                <div class="form-check form-switch d-flex justify-content-center align-items-center gap-2">
                                                    <input class="form-check-input"
                                                           type="checkbox"
                                                           ${product.status ? "checked" : ""}
                                                           onchange="this.form.submit()"/>
                    
                                                    <span class="status-label ${product.status ? 'text-success' : 'text-danger'}">
                                                        ${product.status ? 'Active' : 'Inactive'}
                                                    </span>
                                                </div>
                                            </form>
                                        </td>

                    <!-- ✅ ACTIONS -->
                    <td>
                        <a href="${pageContext.request.contextPath}/product/get?identifier=${product.identifier}"
                           class="btn btn-warning btn-action">
                            Edit
                        </a>

                        <a href="${pageContext.request.contextPath}/product/delete?identifier=${product.identifier}"
                           class="btn btn-danger btn-action"
                           onclick="return confirm('Delete ${product.productName}?')">
                            Delete
                        </a>
                    </td>

                </tr>
            </c:forEach>

            <!-- ✅ EMPTY CASE -->
            <c:if test="${empty products}">
                <tr>
                    <td colspan="9" class="text-center">
                        No products found
                    </td>
                </tr>
            </c:if>

            </tbody>
        </table>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>