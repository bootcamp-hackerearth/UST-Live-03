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
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: #adb5bd;
            transition: .4s;
            border-radius: 24px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: .4s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #007bff;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
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
                   class="btn btn-secondary btn-sm">Home</a>
                <a href="${pageContext.request.contextPath}/customer/add"
                   class="btn btn-primary btn-sm">+ Add Customer</a>
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
                        <th>Sl</th>
                        <th>Name</th>
                        <th>Phone</th>
                        <th>Email</th>
                        <th>Party Type</th>
                        <th>Balance</th>
                        <th>Status</th>
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
                                <form action="${pageContext.request.contextPath}/customer/toggleStatus"
                                      method="get">
                                    <input type="hidden"
                                           name="identifier"
                                           value="${customer.identifier}" />
                                    <label class="switch">
                                        <input type="checkbox"
                                               onchange="this.form.submit()"
                                               <c:if test="${customer.status}">checked</c:if>>
                                        <span class="slider"></span>
                                    </label>
                                </form>
                                <small class="text-primary">
                                    <c:choose>
                                        <c:when test="${customer.status}">
                                            Active
                                        </c:when>
                                        <c:otherwise>
                                            Inactive
                                        </c:otherwise>
                                    </c:choose>
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