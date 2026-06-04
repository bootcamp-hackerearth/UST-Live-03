<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Warehouse List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body { background-color: #E9EEF5; min-height: 100vh; }
        .card { border-radius: 16px; }
        .btn { border-radius: 10px; }
        .table th, .table td { vertical-align: middle; }
    </style>

</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">
        <span class="navbar-brand fw-bold">Warehouse Management</span>

        <div>
            <a href="/" class="btn btn-outline-light btn-sm">Home</a>
            <a href="${pageContext.request.contextPath}/warehouse/add"
               class="btn btn-light btn-sm fw-semibold">+ Add Warehouse</a>
        </div>
    </div>
</nav>

<div class="container mt-5">
    <div class="card shadow p-3">
        <h3 class="text-center fw-bold mb-3">Warehouse List</h3>
        <c:if test="${not empty message}">
            <div class="alert alert-info text-center">
                ${message}
            </div>
        </c:if>

        <table class="table table-hover table-striped">

            <thead class="table-dark">
            <tr>
                <th>Identifier</th>
                <th>Name</th>
                <th>City</th>
                <th>State</th>
                <th>Country</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>

            <c:forEach var="w" items="${warehouses}">
                <tr>
                    <td class="fw-semibold">
                        ${w.identifier}
                    </td>
                    <td>${w.warehouseName}</td>
                    <td>${w.cityName}</td>
                    <td>${w.state}</td>
                    <td>${w.country}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/warehouse/get?identifier=${w.identifier}"
                           class="btn btn-sm btn-outline-primary me-2">
                            Edit
                        </a>

                        <a href="${pageContext.request.contextPath}/warehouse/delete?identifier=${w.identifier}"
                           class="btn btn-sm btn-outline-danger"
                           onclick="return confirm('Delete this warehouse?');">
                            Delete
                        </a>
                    </td>

                </tr>
            </c:forEach>

            </tbody>
        </table>

    </div>

</div>

</body>
</html>