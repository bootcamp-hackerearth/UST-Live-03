<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Stock List</title>

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
            padding: 40px 20px;
        }

        .page-wrapper {
            width: 100%;
            max-width: 1100px;
            margin: 0 auto;
        }

        .list-card {
            background: #ffffff;
            border-radius: 12px;
            padding: 25px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
        }

        .header-banner {
            background: linear-gradient(135deg, #4e73df, #6f42c1);
            color: #ffffff;
            padding: 20px 25px;
            border-radius: 10px;
            margin-bottom: 25px;
        }

        .header-banner h4 {
            margin-bottom: 5px;
            font-weight: 600;
        }

        .header-banner p {
            margin: 0;
            font-size: 14px;
        }

        table {
            width: 100%;
            table-layout: fixed;
            word-wrap: break-word;
        }

        table th {
            background-color: #f8f9fc;
            font-weight: 600;
            font-size: 14px;
            vertical-align: middle;
            text-align: center;
        }

        table td {
            font-size: 13px;
            vertical-align: middle;
            text-align: center;
            white-space: normal;
            word-break: break-word;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 23px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            inset: 0;
            background-color: #ccc;
            border-radius: 34px;
            transition: .4s;
            cursor: pointer;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 17px;
            width: 17px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            border-radius: 50%;
            transition: .4s;
        }

        input:checked + .slider {
            background-color: #007bff;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }

        .status-text {
            font-size: 12px;
            margin-top: 5px;
            font-weight: 500;
        }

        .action-buttons {
            display: flex;
            flex-direction: column;
            gap: 6px;
            align-items: center;
        }

        .action-buttons .btn {
            width: 85px;
        }

    </style>
</head>
<body>
<div class="content">
    <div class="page-wrapper">
        <div class="list-card">
            <div class="header-banner">
                <h4>Stock List</h4>
                <p>View and manage stock</p>
            </div>
            <div class="d-flex justify-content-between mb-4">
                <a href="${pageContext.request.contextPath}/"
                   class="btn btn-secondary btn-sm">
                    Home
                </a>
                <a href="${pageContext.request.contextPath}/stock/add"
                   class="btn btn-primary btn-sm">
                    + Add Stock
                </a>
            </div>
            <c:if test="${empty stocks}">
                <div class="alert alert-info text-center">
                    No stock found.
                </div>
            </c:if>
            <c:if test="${not empty stocks}">
                <table class="table table-bordered table-hover">
                    <thead>
                    <tr>
                        <th>Stock Identifier</th>
                        <th>Warehouse</th>
                        <th>Product</th>
                        <th>Rack</th>
                        <th>Shelf</th>
                        <th>Quantity</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="stock" items="${stocks}">
                        <tr>
                            <td>${stock.identifier}</td>
                            <td>${stock.wareHouse}</td>
                            <td>${stock.product}</td>
                            <td>${stock.racks}</td>
                            <td>${stock.shelves}</td>
                            <td>${stock.quantity}</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/stock/toggleStatus"
                                      method="get">
                                      <input type="hidden"
                                           name="identifier"
                                           value="${stock.identifier}"/>
                                            <label class="switch mb-0">
                                             <input type="checkbox"
                                               onchange="this.form.submit()"
                                               <c:if test="${stock.status}">
                                                   checked
                                               </c:if>>
                                        <span class="slider"></span>
                                    </label>
                                </form>
                                <div class="status-text text-primary">
                                    <c:choose>
                                        <c:when test="${stock.status}">
                                            Active
                                        </c:when>
                                        <c:otherwise>
                                            Inactive
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </td>
                            <td>
                                <div class="action-buttons">
                                    <a href="${pageContext.request.contextPath}/stock/get?identifier=${stock.identifier}"
                                       class="btn btn-success btn-sm">
                                        Update
                                    </a>
                                    <a href="${pageContext.request.contextPath}/stock/delete?identifier=${stock.identifier}"
                                       class="btn btn-danger btn-sm"
                                       onclick="return confirm('Are you sure you want to delete this stock?');">
                                        Delete
                                    </a>
                                </div>
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