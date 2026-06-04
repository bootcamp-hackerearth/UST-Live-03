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
            background: linear-gradient(to bottom, #ffffff, #e5e5e5, #bbbbbb);
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
        h4 {
            background-color: #ffffff;
        }
        .btn-pos-update {
            background-color: #fdfafc;
            border-color: #4b6cb7;
            color: #000;
        }
        .btn-pos-update:hover {
            background-color: #3f5fa7;
            border-color: #3f5fa7;
            color: #fff;
        }
        .btn-pos-delete {
            background-color: #f5f7fa;
            border: 1px solid #dc3545;
            color: #dc3545;
        }
        .btn-pos-delete:hover {
            background-color: #dc3545;
            color: #fff;
        }
    </style>
</head>

<body>
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8">

                <div class="card shadow-lg">
                    <div class="card-header text-black text-center">
                        <h4 class="mb-0">List of Stock</h4>
                    </div>

                    <div class="card-body">
                        <c:if test="${empty stocks}">
                            <div class="alert alert-warning text-center">
                                No Stock found
                            </div>
                        </c:if>

                        <c:if test="${not empty stocks}">
                            <table class="table table-bordered table-hover text-center align-middle">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Stock Identifier</th>
                                        <th>Product</th>
                                        <th>Quantity</th>
                                        <th>Warehouse Location</th>
                                        <th>Status</th>
                                        <th>Delete</th>
                                        <th>Update</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="stock" items="${stocks}">
                                        <tr>
                                            <td>
                                                    ${stock.id}
                                                </a>
                                            </td>

                                            <td>${stock.identifier}</td>
                                            <td>${stock.product}</td>
                                            <td>${stock.quantity}</td>
                                            <td>${stock.warehouse}</td>
                                            <td>${stock.stockStatus}</td>

                                            <td>
                                                <a href="/stock/delete?identifier=${stock.identifier}"
                                                   class="btn btn-pos-delete btn-sm"
                                                   onclick="return confirm('Are you sure you want to delete this stock?');">
                                                    Delete
                                                </a>
                                            </td>
                                            <td>
                                                <a class="btn btn-pos-update btn-sm"
                                                   href="/stock/get?identifier=${stock.identifier}">
                                                    Update
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
                            + Add New Stock
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
