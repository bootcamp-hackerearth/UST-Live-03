<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #1f4037, #99f2c8);
            min-height: 100vh;
        }
        .card {
            border-radius: 15px;

        }
        .card-body {
        .table th {
            background-color: #ffffff;
            color: black;
        }
        a.customer-link {
            text-decoration: none;
            font-weight: 500;
        }
        a.customer-link:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>
<div class="container mt-5">
    <div class="card shadow-lg">
           <div class="card-header bg-primary text-white text-center">
            <h3 class="mb-4">Customer Management</h3>
            </div>

        <div class="card-body">

            <c:if test="${empty customer}">
                <div class="alert alert-warning text-center">
                    No customers found
                </div>
            </c:if>

            <c:if test="${not empty customer}">
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle text-center">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Party Type</th>
                            <th>Balance</th>
                            <th>Credit Limit</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="cat" items="${customer}">
                            <tr>
                                <td>${cat.id}</td>
                                <td>${cat.name}</td>
                                <td>${cat.identifier}</td>
                                <td>${cat.phoneNo}</td>
                                <td>${cat.partyType}</td>
                                <td>${cat.balance}</td>
                                <td>${cat.creditLimit}</td>

                                <td>
                                    <div class="d-flex justify-content-center gap-2">
                                        <a class="btn btn-warning btn-sm"
                                           href="/customer/get?identifier=${cat.identifier}">
                                            Update
                                        </a>

                                        <a class="btn btn-danger btn-sm"
                                           href="/customer/delete?identifier=${cat.identifier}&phoneNo=${cat.phoneNo}"
                                           onclick="return confirm('Are you sure you want to delete this customer?');">
                                            Delete
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>

        </div>

        <div class="card-footer text-center">
            <div class="d-flex justify-content-center gap-3">
                <a href="/" class="btn btn-success">
                    Home
                </a>
                <a href="/customer/add" class="btn btn-success">
                    + Add New Customer
                </a>
            </div>

            <div class="text-muted small mt-2">
                Customer Management System
            </div>
        </div>

    </div>
</div>

</body>
</html>