<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Category Management</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

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
        }

        .card {
            border-radius: 10px;
        }

        .action-btns a {
            margin-right: 8px;
        }

        .action-icon {
            font-size: 1.1rem;
        }

        .edit-icon {
            color: #0d6efd;
        }

        .delete-icon {
            color: #dc3545;
        }
    </style>
</head>

<body class="container py-4">

<div class="page-header d-flex justify-content-between align-items-center">
    <h4 class="mb-0">
        <i class="bi bi-tags-fill me-2"></i> Category Management
    </h4>

    <div>
        <a href="${pageContext.request.contextPath}/"
           class="btn btn-light btn-sm me-2">
            <i class="bi bi-house-door-fill me-1"></i> Home
        </a>

        <a href="${pageContext.request.contextPath}/category/add"
           class="btn btn-light btn-sm">
            <i class="bi bi-plus-circle me-1"></i> Add Category
        </a>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body p-0">

        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
            <tr>
                <th style="width: 80px;">ID</th>
                <th>Category Name</th>
                <th>Super Category</th>
                <th class="text-center" style="width:140px;">Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="category" items="${categories}">
                <tr>
                    <td class="fw-semibold">${category.id}</td>

                    <td>${category.identifier}</td>

                    <td>
                        <c:choose>
                            <c:when test="${empty category.superCategory}">
                                <span class="text-muted">—</span>
                            </c:when>
                            <c:otherwise>
                                ${category.superCategory}
                            </c:otherwise>
                        </c:choose>
                    </td>

                    <td class="text-center action-btns">
                        <a href="${pageContext.request.contextPath}/category/get?identifier=${category.identifier}"
                           class="action-icon edit-icon"
                           title="Edit">
                            <i class="bi bi-pencil-square"></i>
                        </a>

                        <a href="${pageContext.request.contextPath}/category/delete?identifier=${category.identifier}"
                           class="action-icon delete-icon"
                           title="Delete"
                           onclick="return confirm('Are you sure you want to delete this category?');">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty categories}">
                <tr>
                    <td colspan="4" class="text-center text-muted py-4">
                        <i class="bi bi-info-circle me-1"></i>
                        No categories available
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>

    </div>
</div>

</body>
</html>