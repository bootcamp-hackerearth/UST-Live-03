<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

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
        .container {
            max-width: 1100px;
        }
        .card {
            border-radius: 15px;
            border: 1px solid #e5e7eb;
            background: rgba(255, 255, 255, 0.9);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
        }
        .card-header {
            background: linear-gradient(135deg, #e5e7eb, #f3f4f6);
            border-top-left-radius: 15px;
            border-top-right-radius: 15px;
            color: #111827;
            font-weight: 600;
        }
        table thead th {
            background: #e5e7eb;
            font-weight: 600;
        }
        table td {
            background: #f9fafb;
            vertical-align: middle;
        }
        .table-hover tbody tr:hover {
            background-color: #eef2ff;
        }
        .icon-btn {
            width: 38px;
            height: 38px;
            display: inline-flex;
            justify-content: center;
            align-items: center;
            border-radius: 10px;
            color: #fff;
            text-decoration: none;
        }
        .edit-btn { background: #3b82f6; }
        .delete-btn { background: #ef4444; }

        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 24px;
        }
        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }
        .slider {
            position: absolute;
            cursor: pointer;
            top: 0; left: 0; right: 0; bottom: 0;
            background-color: #ef4444;
            border-radius: 24px;
            transition: 0.4s;
        }
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
        input:checked + .slider {
            background-color: #22c55e;
        }
        input:checked + .slider:before {
            transform: translateX(22px);
        }
    </style>
</head>

<body>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-xl-10 col-lg-11">
            <div class="card shadow-sm">
                <div class="card-header text-center">
                    <h4 class="mb-0">Customer List</h4>
                </div>
                <div class="card-body">
                    <c:if test="${empty customer}">
                        <div class="alert alert-warning text-center mb-0">
                            No customers found
                        </div>
                    </c:if>
                    <c:if test="${not empty customer}">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover text-center align-middle">
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Phone</th>
                                    <th>Party Type</th>
                                    <th>Balance</th>
                                    <th>Credit Limit</th>
                                    <th>Status</th>
                                    <th>Update</th>
                                    <th>Delete</th>
                                </tr>
                                </thead>

                                <tbody>
                                <c:forEach var="cat" items="${customer}">

                                    <tr>
                                        <td>${cat.id}</td>
                                        <td>${cat.name}</td>
                                        <td>${cat.identifier}</td>
                                        <td>${cat.phoneNo}</td>
                                        <td>${cat.userType}</td>
                                        <td>${cat.balance}</td>
                                        <td>${cat.creditLimit}</td>
                                        <td>
                                            <label class="switch">
                                                <input type="checkbox"
                                                       ${cat.status ? 'checked' : ''}
                                                       onchange="window.location.href='/customer/toggle?identifier=${cat.identifier}'">
                                                <span class="slider"></span>
                                            </label>
                                        </td>
                                        <td>
                                            <a href="/customer/get?identifier=${cat.identifier}"
                                               class="icon-btn edit-btn"
                                               title="Edit">
                                                <i class="bi bi-pencil-square"></i>
                                            </a>
                                        </td>
                                        <td>
                                            <a href="/customer/delete?identifier=${cat.identifier}"
                                               class="icon-btn delete-btn"
                                               title="Delete"
                                               onclick="return confirm('Are you sure you want to delete this customer?');">
                                                <i class="bi bi-trash"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>
                </div>
                <div class="card-footer d-flex justify-content-center gap-3">
                    <a href="/" class="btn btn-outline-secondary">
                        Home
                    </a>
                    <a href="/customer/add" class="btn btn-success">
                        + Add New Customer
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>