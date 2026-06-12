<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">
    <title>Stock List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
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

        .table th,
        .table td {
            vertical-align: middle;
        }

        .form-switch .form-check-input {
            width: 45px;
            height: 22px;
            cursor: pointer;
        }

        .low-stock {
            background-color: #fff3cd !important;
        }

        .out-stock {
            background-color: #f8d7da !important;
        }

        .inactive-row {
            background-color: #e2e3e5 !important;
        }

    </style>

</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">
        <span class="navbar-brand fw-bold">
            Stock Management
        </span>

        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-outline-light btn-sm">
                Home
            </a>

            <a href="${pageContext.request.contextPath}/stock/add"
               class="btn btn-light btn-sm fw-semibold">
                + Add Stock
            </a>
        </div>

    </div>

</nav>

<div class="container mt-5">
    <div class="card shadow p-3">

        <h3 class="fw-bold mb-3 text-center">
            Stock List
        </h3>

        <c:if test="${not empty message}">

            <div class="alert alert-info text-center">
                ${message}
            </div>

        </c:if>
        <div class="table-responsive">
            <table class="table table-hover table-striped mb-0">
                <thead class="table-dark">
                    <tr>
                        <th>Identifier</th>
                        <th>Product</th>
                        <th>Warehouse</th>
                        <th>Qty</th>
                        <th>Reorder</th>
                        <th>Status</th>
                        <th>Stock State</th>
                        <th class="text-center">Active</th>
                        <th class="text-center">Actions</th>
                    </tr>
                </thead>
                <tbody>

                    <c:if test="${empty stocks}">
                        <tr>
                            <td colspan="9" class="text-center py-4 text-muted">
                                No stock data found.
                            </td>
                        </tr>
                    </c:if>
                    <c:forEach items="${stocks}" var="s">
                        <c:set var="rowClass" value=""/>
                        <c:choose>
                            <c:when test="${!s.status}">
                                <c:set var="rowClass" value="inactive-row"/>
                            </c:when>
                            <c:when test="${s.stockState == 'LOW_STOCK'}">
                                <c:set var="rowClass" value="low-stock"/>
                            </c:when>
                            <c:when test="${s.stockState == 'OUT_OF_STOCK'}">
                                <c:set var="rowClass" value="out-stock"/>
                            </c:when>
                        </c:choose>
                        <tr class="${rowClass}">
                            <td>
                                <a href="${pageContext.request.contextPath}/stock/get?identifier=${s.identifier}"
                                   class="fw-semibold text-decoration-none">
                                    ${s.identifier}
                                </a>
                            </td>

                            <td>
                                ${s.productIdentifier}
                            </td>

                            <td>
                                ${s.warehouseIdentifier}
                            </td>

                            <td>
                                <span class="badge bg-primary">
                                    ${s.availableQuantity}
                                </span>
                            </td>

                            <td>
                                <span class="badge bg-warning text-dark">
                                    ${s.reorderLevel}
                                </span>
                            </td>

                            <td>
                                <c:choose>
                                    <c:when test="${s.status}">
                                        <span class="badge bg-success">
                                            Active
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">
                                            Inactive
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>
                                <c:choose>
                                    <c:when test="${s.stockState == 'AVAILABLE'}">
                                        <span class="badge bg-success">
                                            Available
                                        </span>
                                    </c:when>
                                    <c:when test="${s.stockState == 'LOW_STOCK'}">
                                        <span class="badge bg-warning text-dark">
                                            Low
                                        </span>
                                    </c:when>
                                    <c:when test="${s.stockState == 'OUT_OF_STOCK'}">
                                        <span class="badge bg-danger">
                                            Out
                                        </span>
                                    </c:when>
                                    <c:when test="${s.stockState == 'DISCONTINUED'}">
                                        <span class="badge bg-secondary">
                                            Discontinued
                                        </span>
                                    </c:when>
                                </c:choose>
                            </td>

                            <td class="text-center">
                                <div class="form-check form-switch d-flex justify-content-center">
                                    <input class="form-check-input" type="checkbox"
                                           <c:if test="${s.status}">
                                               checked
                                           </c:if>
                                           onclick="window.location.href='${pageContext.request.contextPath}/stock/toggle?identifier=${s.identifier}'"/>
                                </div>
                            </td>
                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/stock/get?identifier=${s.identifier}"
                                   class="btn btn-sm btn-outline-primary me-2">
                                    Update
                                </a>

                                <a href="${pageContext.request.contextPath}/stock/delete?identifier=${s.identifier}"
                                   class="btn btn-sm btn-outline-danger"
                                   onclick="return confirm('Delete this stock?');">
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

</body>

</html>