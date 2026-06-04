<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Stock List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #1f4037, #99f2c8);
            min-height: 100vh;
        }
        .card {
            border-radius: 16px;
        }
        .card-header {
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
        }
        table th {
            background-color: #0d6efd;
            color: white;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">

            <div class="card shadow-lg">
                <div class="card-header bg-primary text-white text-center">
                    <h4 class="mb-0">Stock List</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty stocks}">
                        <div class="alert alert-warning text-center">
                            No Products Found
                        </div>
                    </c:if>

                    <c:if test="${not empty stocks}">
                        <table class="table table-bordered table-hover text-center align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Product</th>
                                <th>Quantity</th>
                                <th>Minimum Stock</th>
                                <th>Status</th>
                                <th>Update</th>
                                <th>Delete</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="stock" items="${stocks}">
                                <tr>
                                    <td>${stock.id}</td>
                                    <td>${stock.identifier}</td>
                                    <td>${stock.quantity}</td>
                                    <td>${stock.minimumStock}</td>
                                    <td>${stock.stockStatus}</td>
                                    <td>
                                    <a class="btn btn-warning btn-sm"
                                         href="${pageContext.request.contextPath}/stock/get?identifier=${stock.identifier}">
                                             Edit
                                    </a>
                                    </td>
                                    <td>
                                        <a href="/stock/delete?identifier=${stock.identifier}"
                                           class="btn btn-danger btn-sm"
                                           onclick="return confirm('Are you sure you want to delete this role?');">
                                            Delete
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:if>
                </div>

                <div class="card-footer text-center bg-light d-flex justify-content-center gap-3">
                    <a href="/" class="btn btn-secondary">
                        Home
                    </a>

                    <a href="/stock/add" class="btn btn-success">
                        + Add New Product
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>