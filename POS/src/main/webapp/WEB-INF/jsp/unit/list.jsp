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
            background-color: #f5f6f7;
            font-family: "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            color: #2e2e2e;
        }

        .page-header {
            background-color: #000;
            padding: 14px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            color: #fff;
        }

        .page-header h3 {
            margin: 0;
            font-weight: 600;
        }

        .card {
            border-radius: 10px;
            border: 1px solid #e0e0e0;
        }

        .card.shadow {
            box-shadow: 0 6px 18px rgba(0,0,0,0.06);
        }

        .table thead th {
            background-color: #f1f3f5;
            font-weight: 600;
            vertical-align: middle;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 44px;
            height: 22px;
        }

        .switch input {
            display: none;
        }

        .slider {
            position: absolute;
            inset: 0;
            background-color: grey;
            border-radius: 22px;
            transition: 0.3s;
        }

        .slider:before {
            content: "";
            position: absolute;
            height: 16px;
            width: 16px;
            left: 3px;
            bottom: 3px;
            background-color: #ffffff;
            border-radius: 50%;
            transition: 0.3s;
        }

        input:checked + .slider {
            background-color: blue;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }

        .action-icon {
            font-size: 1.1rem;
            margin: 0 8px;
            transition: transform 0.15s ease;
        }

        .action-icon:hover {
            transform: scale(1.15);
        }

        .edit-icon {
            color: #0d6efd;
        }

        .delete-icon {
            color: #dc3545;
        }

        .add-btn {
            background-color: #198754;
            border-color: #198754;
            color: #fff;
        }
    </style>
</head>

<body>

<div class="container mt-4">

    <div class="page-header d-flex justify-content-between align-items-center">
        <h3>Unit Management</h3>

        <div>
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary me-2">
                <i class="bi bi-house-door"></i> Home
            </a>

            <a href="${pageContext.request.contextPath}/unit/add"
               class="btn add-btn">
                <i class="bi bi-plus-circle"></i> Add Unit
            </a>
        </div>
    </div>

    <div class="card shadow">
        <div class="card-body p-0">

            <table class="table table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th>Sl No</th>
                    <th>Unit Name</th>
                    <th>Status</th>
                    <th class="text-center" style="width:120px;">Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach items="${units}" var="u" varStatus="loop">
                    <tr>
                        <td>${loop.index + 1}</td>
                        <td class="fw-semibold">${u.identifier}</td>

                        <td>
                            <a href="${pageContext.request.contextPath}/unit/toggle?identifier=${u.identifier}"
                               onclick="return confirm('Change unit status?')"
                               style="display:inline-flex; align-items:center; gap:10px; text-decoration:none;">

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

                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/unit/get?identifier=${u.identifier}"
                               class="action-icon edit-icon"
                               title="Edit">
                                <i class="bi bi-pencil-square"></i>
                            </a>

                            <a href="${pageContext.request.contextPath}/unit/delete?identifier=${u.identifier}"
                               class="action-icon delete-icon"
                               title="Delete"
                               onclick="return confirm('Are you sure you want to delete this unit?');">
                                <i class="bi bi-trash"></i>
                            </a>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty units}">
                    <tr>
                        <td colspan="4"
                            class="text-center text-muted py-4">
                            No units found.
                        </td>
                    </tr>
                </c:if>

                </tbody>
            </table>

        </div>
    </div>

</div>

</body>
</html>
