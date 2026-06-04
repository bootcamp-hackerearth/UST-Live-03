<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <style>
        body {
            background-color: #f4f6f9;
            min-height: 100vh;
        }

        .card {
            border: none;
            border-radius: 12px;
        }

        .card-header {
            background: #ffffff;
            border-bottom: 1px solid #e5e7eb;
        }

        .card-header h4 {
            font-weight: 600;
        }

        table thead th {
            background-color: #0d6efd;
            color: #ffffff;
            font-weight: 500;
            vertical-align: middle;
        }

        table td {
            vertical-align: middle;
        }

        .table-hover tbody tr:hover {
            background-color: #f1f5ff;
        }

        .btn-outline-danger {
            border-width: 1.5px;
        }

        .btn-outline-primary {
            border-width: 1.5px;
        }

        .card-footer {
            background: #fafafa;
            border-top: 1px solid #e5e7eb;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 60px;
            height: 34px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            cursor: pointer;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: #ccc;
            transition: 0.4s;
            border-radius: 34px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 26px;
            width: 26px;
            left: 4px;
            bottom: 4px;
            background-color: white;
            transition: 0.4s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #4CAF50;
        }

        input:checked + .slider:before {
            transform: translateX(26px);
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
                                        <th>Status</th>
                                        <th>Delete</th>
                                        <th>Update</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="cat" items="${customer}">
                                        <tr>
                                            <td class="fw-semibold">${cat.id}</td>
                                            <td>${cat.name}</td>
                                            <td>${cat.identifier}</td>
                                            <td>${cat.phoneNo}</td>
                                            <td>${cat.userType}</td>
                                            <td>${cat.balance}</td>
                                            <td>${cat.creditLimit}</td>
                                            <td>
                                                <label class="switch">
                                                    <input type="checkbox"
                                                           onclick="toggle('${cat.identifier}', this)"
                                                           ${cat.status ? 'checked' : ''}>
                                                    <span class="slider"></span>
                                                </label>
                                            </td>
                                            <td>
                                                <a href="/customer/delete?identifier=${cat.identifier}&phoneNo=${cat.phoneNo}"
                                                   class="btn btn-outline-danger btn-sm"
                                                   onclick="return confirm('Are you sure you want to delete this customer?');">
                                                    Delete
                                                </a>
                                            </td>
                                            <td>
                                                <a href="/customer/get?identifier=${cat.identifier}"
                                                   class="btn btn-outline-primary btn-sm">
                                                    Update
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

<script>
    function toggle(identifier, checkbox) {
        fetch('<%= request.getContextPath() %>/customer/toggle?identifier=' + identifier)
            .catch(err => {
                console.error(err);
                checkbox.checked = !checkbox.checked;
            });
    }
</script>

</body>
</html>
