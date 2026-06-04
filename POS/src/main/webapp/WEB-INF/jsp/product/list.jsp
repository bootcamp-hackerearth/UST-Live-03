<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Product List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: 'Segoe UI', sans-serif;
        }
        .card {
            width: 900px;
            border-radius: 15px;
            background: rgba(255, 255, 255, 0.9);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
        }
        .card-header {
            background: linear-gradient(135deg, #e5e7eb, #f3f4f6);
            font-weight: 600;
        }
        table th { background: #e5e7eb; }
        table td { background: #f9fafb; }

        .icon-btn {
            border: none;
            padding: 6px 10px;
            border-radius: 8px;
            color: white;
            text-decoration: none;
        }
        .edit-btn { background: #3b82f6; }
        .delete-btn { background: #ef4444; }
    </style>
</head>

<body>

<div class="card shadow-lg">

    <div class="card-header text-center">
        <h4 class="mb-0">Product List</h4>
    </div>

    <div class="card-body">

        <c:if test="${empty products}">
            <div class="alert alert-warning text-center">
                No products available
            </div>
        </c:if>
        <c:if test="${not empty products}">
            <table class="table table-bordered table-hover text-center align-middle">

                <thead>
                <tr>
                    <th>ID</th>
                    <th>Product ID</th>
                    <th>Name</th>
                    <th>Unit</th>
                    <th>Price</th>
                    <th>Category</th>
                    <th>Brand</th>
                    <th>Model</th>
                    <th>Edit</th>
                    <th>Delete</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="product" items="${products}" varStatus="loop">
                    <tr>
                        <td>${loop.index + 1}</td>
                        <td>${product.identifier}</td>
                        <td>${product.name}</td>
                        <td>${product.unit}</td>
                        <td>${product.price}</td>
                        <td>${product.category}</td>
                        <td>${product.brand}</td>
                        <td>${product.models}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/product/get?identifier=${product.identifier}"
                               class="icon-btn edit-btn">
                                <i class="bi bi-pencil-square"></i>
                            </a>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/product/delete?identifier=${product.identifier}"
                               class="icon-btn delete-btn"
                               onclick="return confirm('Are you sure you want to delete this product?');">
                                <i class="bi bi-trash"></i>
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
    <div class="card-footer text-center d-flex justify-content-center gap-3">
        <a href="${pageContext.request.contextPath}/"
           class="btn btn-secondary">
            Home
        </a>
        <a href="${pageContext.request.contextPath}/product/add"
           class="btn btn-primary">
            + Add Product
        </a>
    </div>
</div>
</body>
</html>