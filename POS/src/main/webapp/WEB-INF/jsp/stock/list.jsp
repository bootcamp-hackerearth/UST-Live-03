<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS | Stock Management</title>

    <!-- Bootstrap 5 & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        /* ========= Modern Variables ========= */
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

        /* ========= Header Section ========= */
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

        /* ========= Table Card ========= */
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
            white-space: nowrap;
        }

        .table tbody td {
            padding: 14px 16px;
            vertical-align: middle;
            border-bottom: 1px solid #f1f4f8;
        }

        /* ========= Badges & Highlights ========= */
        .identifier-badge {
            font-size: 0.8rem;
            font-weight: 600;
            color: var(--primary-blue);
            background: #eef3ff;
            padding: 4px 8px;
            border-radius: 6px;
            border: 1px dashed rgba(13, 110, 253, 0.3);
        }

        .warehouse-badge {
            background-color: #f1f5f9;
            color: #475569;
            border: 1px solid var(--border-color);
            padding: 4px 10px;
            border-radius: 6px;
            font-weight: 600;
            font-size: 0.8rem;
        }

        .unit-badge {
            background-color: #fff4e6;
            color: #d9480f;
            border: 1px solid #ffe8cc;
            padding: 4px 10px;
            border-radius: 6px;
            font-weight: 700;
            font-size: 0.75rem;
            text-transform: uppercase;
        }

        .qty-text {
            font-weight: 700;
            font-size: 1rem;
            color: var(--text-dark);
        }

        /* ========= Action Buttons ========= */
        .btn-action {
            padding: 7px 14px;
            font-weight: 700;
            font-size: 0.75rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            border-radius: 8px;
            transition: all 0.2s ease;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            border: 1px solid transparent;
        }

        .btn-edit { background-color: #fff9db; color: #856404; border-color: #ffeeba; }
        .btn-edit:hover { background-color: #ffe066; transform: translateY(-2px); }

        .btn-delete { background-color: #fff5f5; color: #c92a2a; border-color: #ffa8a8; }
        .btn-delete:hover { background-color: #ffc9c9; transform: translateY(-2px); }

        .btn-add {
            background-color: var(--primary-blue);
            color: white;
            font-weight: 600;
            border-radius: 10px;
            padding: 8px 20px;
        }

        .form-check-input:checked { background-color: #10b981 !important; border-color: #10b981 !important; }
    </style>
</head>
<body>

<div class="container-fluid px-5">

    <!-- ✅ HEADER SECTION -->
    <div class="page-header">
        <h3 class="mb-0 fw-bold text-primary">
            <i class="bi bi-boxes me-2"></i>Stock Management
        </h3>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary btn-sm px-3">
                <i class="bi bi-house-door"></i> Home
            </a>
            <a href="${pageContext.request.contextPath}/stock/add" class="btn btn-add btn-sm text-white">
                <i class="bi bi-plus-circle me-1"></i> Add New Stock
            </a>
        </div>
    </div>

    <!-- ✅ TABLE SECTION -->
    <div class="table-container shadow-sm">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead>
                    <tr>
                        <th class="text-center" style="width: 80px;">ID</th>
                        <th>Identifier</th>
                        <th>Warehouse</th>
                        <th class="text-center">Quantity</th>
                        <th class="text-center">Unit</th> <!-- ✅ NEW COLUMN -->
                        <th class="text-center">Status</th>
                        <th class="text-center">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="stock" items="${stocks}">
                        <tr>
                            <td class="text-center text-muted fw-bold">#${stock.id}</td>

                            <td>
                                <span class="identifier-badge">
                                    <i class="bi bi-hash"></i>${stock.identifier}
                                </span>
                            </td>

                            <td>
                                <span class="warehouse-badge">
                                    <i class="bi bi-building me-1"></i>${stock.warehouse}
                                </span>
                            </td>

                            <td class="text-center">
                                <span class="qty-text">${stock.quantity}</span>
                            </td>

                            <!-- ✅ UNIT COLUMN -->
                            <td class="text-center">
                                <span class="unit-badge">
                                    ${stock.unit != null ? stock.unit : 'PCS'}
                                </span>
                            </td>

                            <td class="text-center">
                                <form method="get" action="${pageContext.request.contextPath}/stock/toggle" class="d-inline">
                                    <input type="hidden" name="identifier" value="${stock.identifier}">
                                    <input type="hidden" name="status" value="${!stock.status}">

                                    <div class="form-check form-switch d-flex justify-content-center align-items-center gap-2">
                                        <input class="form-check-input" type="checkbox" role="switch"
                                               ${stock.status ? "checked" : ""}
                                               onchange="this.form.submit()">
                                        <span class="fw-bold small ${stock.status ? 'text-success' : 'text-danger'}">
                                            ${stock.status ? 'ACTIVE' : 'INACTIVE'}
                                        </span>
                                    </div>
                                </form>
                            </td>

                            <td class="text-center">
                                <div class="d-flex justify-content-center gap-2">
                                    <a href="${pageContext.request.contextPath}/stock/get?identifier=${stock.identifier}"
                                       class="btn-action btn-edit">
                                        <i class="bi bi-pencil-square"></i> Edit
                                    </a>
                                    <a href="${pageContext.request.contextPath}/stock/delete?identifier=${stock.identifier}"
                                       class="btn-action btn-delete"
                                       onclick="return confirm('Delete stock record ${stock.identifier}?');">
                                        <i class="bi bi-trash3"></i> Delete
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty stocks}">
                        <tr>
                            <td colspan="7" class="text-center py-5 text-muted fw-bold">
                                <i class="bi bi-exclamation-circle me-2"></i>No stock records found.
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