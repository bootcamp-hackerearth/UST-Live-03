<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Unit Management</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            background-color: #f7f9fc;
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
            background-color: #dc3545; /* INACTIVE */
            border-radius: 24px;
            transition: 0.3s;
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
            transition: 0.3s;
        }

        input:checked + .slider {
            background-color: #198754; /* ACTIVE */
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }

        .action-btns a {
            margin-right: 6px;
        }
    </style>
</head>

<body class="container py-4">

<div class="page-header d-flex justify-content-between align-items-center">
    <h4 class="mb-0">
        <i class="bi bi-rulers me-2"></i> Unit Management
    </h4>

    <div>
        <a href="${pageContext.request.contextPath}/"
           class="btn btn-light btn-sm me-2">
            <i class="bi bi-house-door-fill me-1"></i> Home
        </a>

        <a href="${pageContext.request.contextPath}/unit/add"
           class="btn btn-light btn-sm">
            <i class="bi bi-plus-circle me-1"></i> Add Unit
        </a>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body">

        <table class="table table-hover align-middle">
            <thead class="table-light">
            <tr>
                <th>Sl No</th>
                <th>Unit Name</th>
                <th>Status</th>
                <th class="text-center">Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach items="${units}" var="u" varStatus="loop">
                <tr>
                    <td class="fw-semibold">${loop.index + 1}</td>
                    <td>${u.identifier}</td>

                    <td>
                        <a href="${pageContext.request.contextPath}/unit/toggle?identifier=${u.identifier}"
                           onclick="return confirm('Change unit status?')"
                           style="display:inline-flex; align-items:center; gap:8px; text-decoration:none;">

                            <label class="switch" style="pointer-events:none;">
                                <input type="checkbox" ${u.status ? "checked" : ""}>
                                <span class="slider"></span>
                            </label>

                            <span class="fw-semibold">
                                <c:choose>
                                    <c:when test="${u.status}">ACTIVE</c:when>
                                    <c:otherwise>INACTIVE</c:otherwise>
                                </c:choose>
                            </span>
                        </a>
                    </td>

                    <td class="text-center action-btns">
                        <a href="${pageContext.request.contextPath}/unit/get?identifier=${u.identifier}"
                           class="btn btn-sm btn-outline-primary"
                           title="Edit">
                            <i class="bi bi-pencil-square"></i>
                        </a>

                        <a href="${pageContext.request.contextPath}/unit/delete?identifier=${u.identifier}"
                           class="btn btn-sm btn-outline-danger"
                           title="Delete"
                           onclick="return confirm('Are you sure you want to delete this unit?');">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty units}">
                <tr>
                    <td colspan="4" class="text-center text-muted py-4">
                        <i class="bi bi-info-circle me-1"></i>
                        No units found
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>

    </div>
</div>

</body>
</html>