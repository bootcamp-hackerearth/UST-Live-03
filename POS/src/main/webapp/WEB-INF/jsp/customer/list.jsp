<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Customer List</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet">

<style>
    body {
        background-color: #E9EEF5;
        min-height: 100vh;
    }

    .card {
        border-radius: 16px;
    }

    .btn {
        border-radius: 10px;
    }

    .table th, .table td {
        vertical-align: middle;
    }

    .form-switch .form-check-input {
        width: 45px;
        height: 22px;
        cursor: pointer;
    }
</style>
</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">
        <span class="navbar-brand fw-bold">Customer Management</span>

        <div class="d-flex gap-2">
            <a href="/" class="btn btn-outline-light btn-sm">Home</a>
            <a href="/customer/add" class="btn btn-light btn-sm fw-semibold">+ Add Customer</a>
        </div>
    </div>
</nav>

<div class="container mt-5">

    <div class="card shadow p-3">

        <h3 class="fw-bold mb-3 text-center">Customer List</h3>

        <div class="table-responsive">

            <table class="table table-hover table-striped mb-0">

                <thead class="table-dark">
                <tr>
                    <th>Identifier</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Balance</th>
                    <th>Balance Type</th>
                    <th>Party Type</th>
                    <th>Credit Limit</th>
                    <th>Status</th>
                    <th class="text-center">Actions</th>
                </tr>
                </thead>

                <tbody>

                <c:if test="${empty customers}">
                    <tr>
                        <td colspan="10" class="text-center py-4 text-muted">
                            No customers found.
                        </td>
                    </tr>
                </c:if>

                <c:forEach items="${customers}" var="customer">
                    <tr>

                        <td>
                            <a href="/customer/get?identifier=${customer.identifier}"
                               class="fw-semibold text-decoration-none">
                                ${customer.identifier}
                            </a>
                        </td>

                        <td class="fw-semibold">${customer.name}</td>
                        <td>${customer.email}</td>
                        <td>${customer.phoneNo}</td>
                        <td>${customer.balance}</td>
                        <td>${customer.balanceType}</td>
                        <td>${customer.partyType}</td>
                        <td>${customer.creditLimit}</td>

                        <td class="text-center">
                            <div class="form-check form-switch">
                                <input class="form-check-input"
                                       type="checkbox"
                                       onclick="toggleCustomer('${customer.identifier}')"
                                       <c:if test="${customer.status}">checked</c:if> />
                            </div>
                        </td>

                        <td class="text-center">

                            <a href="/customer/get?identifier=${customer.identifier}"
                               class="btn btn-sm btn-outline-primary me-2">
                                Update
                            </a>

                            <a href="/customer/delete?identifier=${customer.identifier}"
                               class="btn btn-sm btn-outline-danger"
                               onclick="return confirm('Delete this customer?');">
                                Delete
                            </a>

                        </td>

                    </tr>
                </c:forEach>

                </tbody>

            </table>

        </div>

    </div>

</div>

<script>
    function toggleCustomer(identifier) {
        window.location.href = "/customer/toggle?identifier=" + identifier;
    }
</script>

</body>
</html>