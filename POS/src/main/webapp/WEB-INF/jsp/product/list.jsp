<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Product List</title>

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
        h4{
            background-color: ;
        }
        table th {
            background-color: #0d6efd;
            color: white;
        }
         .toggle-container {
                    cursor: pointer;
                    display: inline-block;
                }

                /* Switch container */
                .switch {
                    position: relative;
                    display: inline-block;
                    width: 46px;
                    height: 24px;
                }

                /* Hide checkbox */
                .switch input {
                    opacity: 0;
                    width: 0;
                    height: 0;
                }

                /* Slider */
                .slider {
                    position: absolute;
                    cursor: pointer;
                    background-color: #d1d5db; /* OFF gray */
                    border-radius: 24px;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    transition: 0.4s;
                }

                /* Circle knob */
                .slider:before {
                    position: absolute;
                    content: "";
                    height: 18px;
                    width: 18px;
                    left: 3px;
                    bottom: 3px;
                    background-color: white;
                    border-radius: 50%;
                    transition: 0.4s;
                }


                input:not(:checked) + .slider {
                background-color: #dc2626; /* RED */
                }

                input:checked + .slider {
                background-color: #22c55e; /* GREEN */
                }

                input:checked + .slider:before {
                    transform: translateX(22px);
                }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">

            <div class="card shadow-lg">
                <div class="card-header bg-primary text-white text-center">
                    <h4 class="mb-0">Product List</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty products}">
                        <div class="alert alert-warning text-center">
                            No Products Found
                        </div>
                    </c:if>

                    <c:if test="${not empty products}">
                        <table class="table table-bordered table-hover text-center align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Identifier</th>
                                <th>Product Name</th>
                                <th>Brand</th>
                                <th>Model</th>
                                <th>Unit</th>
                                <th>Category</th>
                                <th>Status</th>
                                <th>Update</th>
                                <th>Delete</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="product" items="${products}">
                                <tr>
                                    <td>${product.id}</td>
                                    <td>${product.identifier}</td>
                                    <td>${product.name}</td>
                                    <td>${product.brand}</td>
                                    <td>${product.model}</td>
                                    <td>${product.unit}</td>
                                    <td>${product.category}</td>
                                    <td>
                                       <div class="toggle-container"
                                            onclick="window.location.href='${pageContext.request.contextPath}/product/toggle?identifier=${product.identifier}'">
                                            <label class="switch">
                                               <input type="checkbox" ${product.status ? "checked" : ""} disabled>
                                               <span class="slider"></span>
                                            </label>
                                       </div>
                                    </td>
                                    <td>
                                    <a class="btn btn-warning btn-sm"
                                         href="${pageContext.request.contextPath}/product/get?identifier=${product.identifier}">
                                         Edit
                                    </a>
                                    </td>
                                    <td>
                                        <a href="/product/delete?identifier=${product.identifier}"
                                           class="btn btn-danger btn-sm"
                                           onclick="return confirm('Are you sure you want to delete this product?');">
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

                    <a href="/product/add" class="btn btn-success">
                        + Add New Product
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>