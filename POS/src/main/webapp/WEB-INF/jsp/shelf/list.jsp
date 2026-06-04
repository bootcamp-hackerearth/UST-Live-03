<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Shelf Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css"
          rel="stylesheet">

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
        <i class="bi bi-layout-text-window-reverse me-2"></i> Shelf Management
    </h4>

    <div class="d-flex gap-2">
        <a href="${pageContext.request.contextPath}/"
           class="btn btn-light btn-sm">
            <i class="bi bi-house-door-fill me-1"></i> Home
        </a>

        <a href="${pageContext.request.contextPath}/shelf/add"
           class="btn btn-light btn-sm">
            <i class="bi bi-plus-circle me-1"></i> Add Shelf
        </a>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body">

        <table class="table table-hover align-middle">
            <thead class="table-light">
            <tr>
                <th>ID</th>
                <th>Shelf Name</th>
                <th>Status</th>
                <th class="text-center">Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach items="${shelfs}" var="shelf">
                <tr>
                    <td class="fw-semibold">${shelf.id}</td>
                    <td>${shelf.identifier}</td>

                    <td>
                        <a href="${pageContext.request.contextPath}/shelf/toggle?identifier=${shelf.identifier}"
                           onclick="return confirm('Change shelf status?')"
                           style="display:inline-flex; align-items:center; gap:8px; text-decoration:none;">

                            <label class="switch" style="pointer-events:none;">
                                <input type="checkbox" ${shelf.status ? "checked" : ""}>
                                <span class="slider"></span>
                            </label>

                            <span class="fw-semibold">
                                <c:choose>
                                    <c:when test="${shelf.status}">ACTIVE</c:when>
                                    <c:otherwise>INACTIVE</c:otherwise>
                                </c:choose>
                            </span>
                        </a>
                    </td>

                    <td class="text-center action-btns">
                        <a href="${pageContext.request.contextPath}/shelf/get?identifier=${shelf.identifier}"
                           class="btn btn-sm btn-outline-primary"
                           title="Edit">
                            <i class="bi bi-pencil-square"></i>
                        </a>

                        <a href="${pageContext.request.contextPath}/shelf/delete?identifier=${shelf.identifier}"
                           class="btn btn-sm btn-outline-danger"
                           onclick="return confirm('Delete this shelf?')"
                           title="Delete">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty shelfs}">
                <tr>
                    <td colspan="4" class="text-center text-muted py-4">
                        <i class="bi bi-info-circle me-1"></i>
                        No shelves found
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>

    </div>
</div>

</body>
</html>