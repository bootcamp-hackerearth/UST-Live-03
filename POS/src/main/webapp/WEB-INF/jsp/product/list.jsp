<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>product List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(to bottom, #ffffff, #e5e5e5, #bbbbbb);
            min-height: 100vh;
        }

        .card-body {
            overflow-x: auto;
        }

        .table {
            white-space: nowrap;
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
                    <h4 class="mb-0">List of product</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty products}">
                        <div class="alert alert-warning text-center">
                            No Product found
                        </div>
                    </c:if>

                    <c:if test="${not empty products}">
                        <table class="table table-bordered table-hover text-center align-middle">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Product Identifier</th>
                                    <th>Product Name</th>
                                    <th>Description</th>
                                    <th>Unit</th>
                                    <th>Brand</th>
                                    <th>Model</th>
                                    <th>Price</th>
                                    <th>Category</th>
                                    <th>Delete</th>
                                    <th>Update</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="product" items="${products}">
                                    <tr>
                                        <td>

                                                ${product.id}
                                            </a>
                                        </td>

                                        <td>${product.identifier}</td>
                                        <td>${product.name}</td>
                                        <td>${product.description}</td>
                                        <td>${product.unit}</td>
                                        <td>${product.brand}</td>
                                        <td>${product.model}</td>
                                        <td>${product.price}</td>
                                        <td>${product.category}</td>

                                        <td>
                                            <a href="/product/delete?identifier=${product.identifier}"
                                               class="btn btn-pos-delete btn-sm"
                                               onclick="return confirm('Are you sure you want to delete this product?');">
                                                Delete
                                            </a>
                                        </td>

                                        <td>
                                            <a class="btn btn-pos-update btn-sm"
                                               href="/product/get?identifier=${product.identifier}">
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

                    <a href="/product/add" class="btn btn-success">
                        + Add New product
                    </a>
                </div>

            </div>

        </div>
    </div>
</div>

</body>
</html>