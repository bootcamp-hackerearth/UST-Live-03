<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <title>Customer List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <style>
        body {
            min-height: 100vh;
            background: #fff8f0;
            font-family: 'Poppins', sans-serif;
        }

        h4 {
            color: #4a2e2b;
            font-weight: 700;
        }

        .table-wrapper {
            background: #efe3d9;
            border-radius: 14px;
            overflow: hidden;
            box-shadow: 0 14px 35px rgba(0, 0, 0, 0.15);
        }

        .table {
            margin-bottom: 0;
            background: transparent;
            border-collapse: separate;
            border-spacing: 0;
        }

        .table thead th {
            background: #4a2e2b;
            color: #fff8f0;
            font-weight: 600;
            padding: 14px 16px;
            text-align: center;
            vertical-align: middle;
            border: none;
        }

        .table tbody tr {
            background: #fff8f0;
            height: 68px;
        }

        .table tbody tr:nth-child(even) {
            background: #eadfd6;
        }

        .table td {
            vertical-align: middle !important;
            text-align: center;
            padding: 12px 16px;
            border: none;
            line-height: 1.4;
        }

        .table td:nth-child(4) {
            white-space: nowrap;
        }

        .table td:nth-child(5) {
            word-break: break-word;
        }

        .table td:nth-child(7) {
            white-space: nowrap;
        }

        .table td:last-child {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
            white-space: nowrap;
        }

        .btn-sm {
            height: 36px;
            min-width: 36px;
            padding: 0 14px;
            border-radius: 10px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-weight: 500;
        }

        .btn-info {
            background-color: #8b5e59;
            color: #fff8f0;
            border: none;
        }

        .btn-info:hover {
            background-color: #6f4844;
        }

        .btn-success {
            background-color: #6b4a46;
            color: #fff8f0;
            border: none;
        }

        .btn-success:hover {
            background-color: #543835;
        }

        .btn-danger {
            background-color: #6b4a46;
            color: white;
            border: none;
        }

        .btn-danger:hover {
            background-color: #543835;
        }

        .btn-secondary,
        .btn-primary {
            background-color: #6b4a46;
            color: #fff8f0;
            border: none;
            border-radius: 12px;
            padding: 10px 22px;
        }

        .btn-secondary:hover,
        .btn-primary:hover {
            background-color: #543835;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 22px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            inset: 0;
            background-color: #cfc4bb;
            border-radius: 20px;
            transition: 0.4s;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 16px;
            width: 16px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            border-radius: 50%;
            transition: 0.4s;
        }

        input:checked + .slider {
            background-color: #6b4a46;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }
    </style>
</head>
<body>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-9">
            <h4 class="text-center mb-4">List of Customers</h4>
            <c:if test="${not empty message}">
                <div id="customToast" class="custom-toast">
                    ${message}
                </div>
                <c:remove var="message" scope="session"/>
            </c:if>
            <c:if test="${empty customers}">
                <div class="alert alert-warning text-center">
                    No customers found
                </div>
            </c:if>
            <c:if test="${not empty customers}">
                <div class="table-wrapper">
                    <table class="table align-middle">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Customer Name</th>
                            <th>Party Type</th>
                            <th>Balance</th>
                            <th>Email</th>
                            <th>Party Credit Limit</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="customer" items="${customers}">
                            <tr>
                                <td>${customer.id}</td>
                                <td>${customer.name}</td>
                                <td>${customer.partyType}</td>
                                <td>${customer.balance} ${customer.balanceType}</td>
                                <td>${customer.email}</td>
                                <td>${customer.creditLimit}</td>
                                <td class="text-center">
                                    <form action="${pageContext.request.contextPath}/customer/toggleStatus" method="post">
                                        <input type="hidden" name="identifier" value="${customer.identifier}"/>
                                            <label class="switch">
                                                <input type="checkbox" onchange="this.form.submit()"
                                                    <c:if test="${customer.status}">checked</c:if>>
                                                    <span class="slider"></span>
                                            </label>
                                    </form>
                                    <small class="text-primary">
                                        <c:choose>
                                            <c:when test="${customer.status}">Active</c:when>
                                            <c:otherwise>Inactive</c:otherwise>
                                        </c:choose>
                                    </small>
                                </td>
                                <td class="d-flex justify-content-center gap-2">
                                    <a href="/customer/get?identifier=${customer.identifier}"
                                       class="btn btn-success btn-sm"
                                       title = "Edit Customer">
                                       <i class="fa-solid fa-pen"></i>
                                    </a>
                                    <a href="/customer/delete?identifier=${customer.identifier}"
                                       class="btn btn-danger btn-sm"
                                       onclick="return confirm('Are you sure you want to delete this customer?');"
                                       title = "Delete Customer">
                                       <i class="fa-solid fa-trash"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
            <div class="d-flex justify-content-center gap-3 mt-4">
                <a href="/" class="btn btn-secondary">Home</a>
                <a href="/customer/add" class="btn btn-primary">+ Add New Customer</a>
            </div>
        </div>
    </div>
</div>
<script>
window.addEventListener("DOMContentLoaded", function () {
    const toast = document.getElementById("customToast");
    if (toast) {
        setTimeout(() => {
            toast.classList.add("hide");
            setTimeout(() => toast.remove(), 400);
        }, 3500);
    }
});
</script>
</body>
</html>