<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Warehouse List</title>

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
        h4{
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
                    <h4 class="mb-0">List of Warehouse</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty warehouses}">
                        <div class="alert alert-warning text-center">
                            No Warehouse found
                        </div>
                    </c:if>

                    <c:if test="${not empty warehouses}">
                        <table class="table table-bordered table-hover text-center align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Warehouse Name</th>
                                <th>Region</th>
                                <th>Country</th>
                                <th>Contact Name</th>
                                <th>Contact Number</th>
                                <th>Location</th>

                                <th>Delete</th>
                                <th>Update</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="warehouse" items="${warehouses}">
                                <tr>
                                    <td>
                                        <a href=""
                                           class="text-decoration-none fw-semibold">
                                            ${warehouse.id}
                                        </a>
                                    </td>
                                    <td>${warehouse.identifier}</td>
                                    <td>${warehouse.region}</td>
                                    <td>${warehouse.country}</td>
                                    <td>${warehouse.contactName}</td>
                                    <td>${warehouse.contactNumber}</td>
                                    <td>${warehouse.location}</td>

                                    <td>
                                        <a href="/warehouse/delete?identifier=${warehouse.identifier}"
                                           class="btn btn-pos-delete btn-sm"
                                           onclick="return confirm('Are you sure you want to delete this warehouse?');">
                                            Delete
                                        </a>
                                    </td>
                                    <td>
                                          <a class="btn btn-pos-update btn-sm"
                                     href="/warehouse/get?identifier=${warehouse.identifier}"
                                                                    >
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

                    <a href="/warehouse/add" class="btn btn-success">
                        + Add New warehouse
                    </a>
                </div>

            </div>

        </div>
    </div>
</div>

</body>
</html>