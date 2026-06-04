<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS | Rack Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        :root {
            --primary-blue: #0d6efd;
            --bg-light: #f4f7f6;
            --text-dark: #2c3e50;
            --border-color: #e2e8f0;
            --shadow-soft: 0 4px 14px rgba(0,0,0,0.06);
            --shadow-deep: 0 12px 30px rgba(0,0,0,0.08);
        }

        body {
            background-color: var(--bg-light);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            color: var(--text-dark);
            min-height: 100vh;
            padding: 40px 0;
        }

        .page-header {
            background: #ffffff;
            padding: 22px 26px;
            border-radius: 14px;
            box-shadow: var(--shadow-soft);
            margin-bottom: 28px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .table-container {
            background: #ffffff;
            border-radius: 14px;
            padding: 25px;
            box-shadow: var(--shadow-deep);
            border: none;
        }

        .table thead th {
            background-color: var(--primary-blue) !important;
            color: #ffffff !important;
            padding: 16px;
            font-weight: 600;
            border: none;
        }

        .table tbody td {
            padding: 16px;
            vertical-align: middle;
            border-bottom: 1px solid #f1f4f8;
        }

        .table-hover tbody tr:hover {
            background-color: #f8fafc;
        }

        .identifier-badge {
            font-size: 0.85rem;
            font-weight: 600;
            color: var(--primary-blue);
            background: #eef3ff;
            padding: 4px 10px;
            border-radius: 6px;
            display: inline-block;
            border: 1px dashed rgba(13, 110, 253, 0.3);
        }

        .shelf-badge {
            background-color: #f1f5f9;
            color: #475569;
            border: 1px solid var(--border-color);
            padding: 5px 12px;
            border-radius: 8px;
            font-weight: 600;
            font-size: 0.85rem;
        }

        .status-label {
            font-size: 0.82rem;
            font-weight: 700;
            letter-spacing: 0.5px;
        }

        .form-check-input {
            width: 2.8em !important;
            height: 1.4em !important;
            cursor: pointer;
        }

        .form-check-input:checked {
            background-color: #10b981 !important;
            border-color: #10b981 !important;
        }

        .btn-action {
            border-radius: 8px;
            padding: 6px 14px;
            font-weight: 600;
            font-size: 0.85rem;
            transition: all 0.2s ease;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }

        .btn-edit {
            background: #fef3c7;
            color: #92400e;
            border: 1px solid #fde68a;
        }
        .btn-edit:hover {
            background: #fde68a;
            color: #78350f;
        }

        .btn-delete {
            background: #fee2e2;
            color: #991b1b;
            border: 1px solid #fecaca;
        }
        .btn-delete:hover {
            background: #fecaca;
            color: #7f1d1d;
        }

        .btn-add {
            background-color: var(--primary-blue);
            color: white;
            font-weight: 600;
            border-radius: 10px;
            padding: 8px 20px;
            box-shadow: 0 4px 10px rgba(13, 110, 253, 0.2);
        }
        .btn-add:hover { color: white; background-color: #0b5ed7; }
    </style>
</head>
<body>

<div class="container-fluid px-5">

    <div class="page-header">
        <h3 class="mb-0 fw-bold text-primary">
            <i class="bi bi-grid-3x3-gap me-2"></i>Rack Management
        </h3>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary btn-sm px-3">
                <i class="bi bi-house-door"></i> Home
            </a>
            <a href="${pageContext.request.contextPath}/rack/add" class="btn btn-add btn-sm">
                <i class="bi bi-plus-circle me-1"></i> Add New Rack
            </a>
        </div>
    </div>

    <c:if test="${not empty message}">
        <div class="alert ${success ? 'alert-success' : 'alert-danger'} alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi ${success ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill'} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="table-container">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead>
                    <tr>
                        <th class="text-center" style="width: 100px;">ID</th>
                        <th>Rack Identifier</th>
                        <th class="text-center">Shelf</th>
                        <th class="text-center">Status</th>
                        <th class="text-center">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="r" items="${racks}">
                        <tr>
                            <td class="text-center text-muted fw-bold">#${r.id}</td>

                            <td>
                                <span class="identifier-badge">
                                    <i class="bi bi-tag-fill me-1"></i> ${r.identifier}
                                </span>
                            </td>

                            <td class="text-center">
                                <span class="shelf-badge">
                                    <i class="bi bi-layers-fill text-info me-1"></i> ${r.shelfs}
                                </span>
                            </td>

                            <td class="text-center">
                                <form method="post" action="${pageContext.request.contextPath}/rack/update" class="d-inline">
                                    <input type="hidden" name="id" value="${r.id}">
                                    <input type="hidden" name="identifier" value="${r.identifier}">
                                    <input type="hidden" name="shelfs" value="${r.shelfs}">
                                    <input type="hidden" name="status" value="${!r.status}">

                                    <div class="form-check form-switch d-flex justify-content-center align-items-center gap-2">
                                        <input class="form-check-input" type="checkbox" role="switch"
                                               ${r.status ? "checked" : ""}
                                               onchange="this.form.submit()">
                                        <span class="status-label ${r.status ? 'text-success' : 'text-danger'}">
                                            ${r.status ? 'ACTIVE' : 'DEACTIVE'}
                                        </span>
                                    </div>
                                </form>
                            </td>

                            <td class="text-center">
                                <div class="d-flex justify-content-center gap-2">
                                    <!-- Edit Button -->
                                    <a href="${pageContext.request.contextPath}/rack/get?identifier=${r.identifier}"
                                       class="btn-action btn-edit">
                                        <i class="bi bi-pencil-square"></i> Edit
                                    </a>
                                    <!-- Proper Delete Button -->
                                    <a href="${pageContext.request.contextPath}/rack/delete?identifier=${r.identifier}"
                                       class="btn-action btn-delete"
                                       onclick="return confirm('Confirm deletion of Rack: ${r.identifier}?')">
                                        <i class="bi bi-trash3"></i> Delete
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty racks}">
                        <tr>
                            <td colspan="5" class="text-center py-5">
                                <div class="opacity-25 mb-3">
                                    <i class="bi bi-inbox" style="font-size: 4rem;"></i>
                                </div>
                                <p class="text-muted fw-bold">No racks found in the inventory.</p>
                                <a href="${pageContext.request.contextPath}/rack/add" class="btn btn-primary btn-sm">
                                    Create First Rack
                                </a>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>