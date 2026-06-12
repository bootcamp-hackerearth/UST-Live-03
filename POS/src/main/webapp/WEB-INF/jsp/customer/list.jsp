<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Customer List</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css">

    <!-- Google Font -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            background-color: #f4f6fb;
        }

        .content {
            margin: 50px auto;
            padding: 20px;
        }

        .page-wrapper {
            max-width: 1000px;
            margin: 0 auto;
        }

        .header-banner {
            background: linear-gradient(135deg, #4e73df, #6f42c1);
            color: #ffffff;
            padding: 18px 22px;
            border-radius: 10px;
            margin-bottom: 20px;
        }

        .welcome-card {
            background: #ffffff;
            border-radius: 10px;
            padding: 22px;
        }

        table th {
            font-weight: 600;
            font-size: 14px;
            background-color: #f8f9fc;
        }

        table td {
            font-size: 14px;
            vertical-align: middle;
        }
    </style>
</head>

<body>

<div class="content">
    <div class="page-wrapper">

        <div class="header-banner">
            <h2>Customer List</h2>
            <p>View and manage customers</p>
        </div>

        <div class="welcome-card">

            <div class="d-flex justify-content-between mb-3">
                <a href="${pageContext.request.contextPath}/"
                   class="btn btn-secondary btn-sm">
                    Home
                </a>

                <a href="${pageContext.request.contextPath}/customer/add"
                   class="btn btn-primary btn-sm">
                    + Add Customer
                </a>
            </div>

            <c:if test="${empty customers}">
                <div class="alert alert-info text-center">
                    No customers found.
                </div>
            </c:if>


            <c:if test="${not empty customers}">
                <table class="table table-bordered table-hover">
                    <thead>
                    <tr>
                        <th>Id</th>
                        <th>Name</th>
                        <th>Phone</th>
                        <th>Email</th>
                        <th>Party Type</th>
                        <th>Balance</th>
                        <th style="width: 180px;">Action</th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:forEach items="${customers}" var="customer" varStatus="status">
                        <tr>
                            <td>${status.index + 1}</td>
                            <td>${customer.name}</td>
                            <td>${customer.phoneNo}</td>
                            <td>${customer.email}</td>
                            <td>${customer.partyType}</td>
                            <td>
                                ${customer.balance}
                                <small class="text-muted">
                                    (${customer.balanceType})
                                </small>
                            </td>

                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/customer/get?identifier=${customer.identifier}"
                                   class="btn btn-success btn-sm mr-2">
                                    Update
                                </a>

                                <a href="${pageContext.request.contextPath}/customer/delete?identifier=${customer.identifier}"
                                   class="btn btn-danger btn-sm"
                                   onclick="return confirm('Are you sure you want to delete this customer?');">
                                    Delete
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>

        </div>
    </div>
</div>

</body>
</html>
