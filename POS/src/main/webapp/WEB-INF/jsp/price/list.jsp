<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>prices List</title>

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
        h4{
            background-color: ;
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
                <div class="card-header bg-primary text-white text-center">
                    <h4 class="mb-0">List of Price</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty price}">
                        <div class="alert alert-warning text-center">
                            No Stock found
                        </div>
                    </c:if>

                    <c:if test="${not empty price}">
                        <table class="table table-bordered table-hover text-center align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Identifier</th>
                                <th>Product</th>
                                <th>Price</th>
                                <th>Price Type</th>
                                <th>Update</th>
                                <th>Delete</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="itemPrice" items="${price}">
                                <tr>
                                    <td>${itemPrice.id}</td>
                                    <td>${itemPrice.identifier}</td>
                                    <td>${itemPrice.product}</td>
                                    <td>${itemPrice.priceAmount}</td>
                                    <td>${itemPrice.priceType}</td>
                                    <td>
                                          <a class="btn btn-warning btn-sm"
                                     href="/price/get?identifier=${itemPrice.identifier}"
                                     >Update</a>
                                    </td>
                                    <td>
                                       <a href="/price/delete?identifier=${itemPrice.identifier}"
                                       class="btn btn-danger btn-sm"
                                       onclick="return confirm('Are you sure you want to delete this price?');">
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
                    <a href="/price/add" class="btn btn-success">
                        + Add New Product Price
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>