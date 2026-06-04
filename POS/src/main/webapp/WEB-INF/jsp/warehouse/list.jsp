<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Warehouse Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #f7f9fc;
            color: #1e293b;
        }

        .page-header {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #ffffff;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
        }

        .table thead th {
            text-transform: uppercase;
            font-size: 13px;
            letter-spacing: 0.03em;
            vertical-align: middle;
        }

        .card {
            border-radius: 10px;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 48px;
            height: 24px;
        }

        .switch input {
            display: none;
        }

        .slider {
            position: absolute;
            inset: 0;
            background-color: #dc3545;
            border-radius: 24px;
            transition: .3s;
        }

        .slider:before {
            content: "";
            position: absolute;
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: #ffffff;
            border-radius: 50%;
            transition: .3s;
        }

        input:checked + .slider {
            background-color: #198754;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }

        .action-icon {
            font-size: 1.2rem;
            margin: 0 6px;
        }

        .edit-icon { color: #0d6efd; }
        .delete-icon { color: #dc3545; }
    </style>
</head>

<body class="container py-4">

<div class="page-header d-flex justify-content-between align-items-center">
    <h4 class="mb-0">
        <i class="bi bi-building me-2"></i> Warehouse Management
    </h4>

    <div>
        <a href="${pageContext.request.contextPath}/"
           class="btn btn-light btn-sm me-2">
            <i class="bi bi-house-door-fill me-1"></i> Home
        </a>

        <a href="${pageContext.request.contextPath}/warehouse/add"
           class="btn btn-light btn-sm">
            <i class="bi bi-plus-circle me-1"></i> Add Warehouse
        </a>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body p-0">

        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
            <tr>
                <th>Name</th>
                <th>Location</th>
                <th>Capacity</th>
                <th>Status</th>
                <th class="text-center" style="width:140px;">Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach items="${warehouses}" var="w">
                <tr>
                    <td class="fw-semibold">${w.identifier}</td>
                    <td>${w.location}</td>
                    <td>${w.capacity}</td>

                    <td>
                        <a href="${pageContext.request.contextPath}/warehouse/toggle?identifier=${w.identifier}"
                           onclick="return confirm('Change warehouse status?')"
                           style="display:flex; align-items:center; gap:8px; text-decoration:none;">

                            <label class="switch" style="pointer-events:none;">
                                <input type="checkbox" ${w.status ? "checked" : ""}>
                                <span class="slider"></span>
                            </label>

                            <span class="fw-semibold">
                                <c:choose>
                                    <c:when test="${w.status}">ACTIVE</c:when>
                                    <c:otherwise>INACTIVE</c:otherwise>
                                </c:choose>
                            </span>
                        </a>
                    </td>

                    <td class="text-center">
                        <a href="${pageContext.request.contextPath}/warehouse/get?identifier=${w.identifier}"
                           class="action-icon edit-icon"
                           title="Edit">
                            <i class="bi bi-pencil-square"></i>
                        </a>

                        <a href="${pageContext.request.contextPath}/warehouse/delete?identifier=${w.identifier}"
                           class="action-icon delete-icon"
                           title="Delete"
                           onclick="return confirm('Are you sure you want to delete this warehouse?');">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty warehouses}">
                <tr>
                    <td colspan="5" class="text-center text-muted py-4">
                        <i class="bi bi-info-circle me-1"></i>
                        No warehouses found
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>

    </div>
</div>

</body>
</html>