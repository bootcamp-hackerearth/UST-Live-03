<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>POS | Customer Management</title>

    <!-- Bootstrap 5 & Icons -->
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
            font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
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

        .customer-name {
            font-weight: 700;
            color: var(--text-dark);
        }

        .identifier-badge {
            font-size: 0.85rem;
            font-weight: 600;
            color: var(--primary-blue);
            background: #eef3ff;
            padding: 4px 10px;
            border-radius: 6px;
            display: inline-block;
        }

        .balance-cr {
            color: #10b981;
            font-weight: 700;
            background: rgba(16, 185, 129, 0.1);
            padding: 4px 12px;
            border-radius: 20px;
        }

        .balance-dr {
            color: #ef4444;
            font-weight: 700;
            background: rgba(239, 68, 68, 0.1);
            padding: 4px 12px;
            border-radius: 20px;
        }

        .status-label {
            font-size: 0.85rem;
            font-weight: 700;
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
            padding: 8px 14px;
            font-weight: 600;
            font-size: 0.85rem;
            transition: all 0.2s ease;
            text-decoration: none;
            border: none;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }

        .btn-edit { background: #fef3c7; color: #92400e; }
        .btn-edit:hover { background: #fde68a; color: #78350f; }

        .btn-delete { background: #fee2e2; color: #991b1b; }
        .btn-delete:hover { background: #fecaca; color: #7f1d1d; }

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
        <div>
            <h3 class="mb-0 fw-bold text-primary">
                <i class="bi bi-people-fill me-2"></i>Customer Management
            </h3>
            <p class="text-muted small mb-0 mt-1">Manage your business partners and credit limits</p>
        </div>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary btn-sm px-3">
                <i class="bi bi-house-door"></i> Home
            </a>
            <a href="${pageContext.request.contextPath}/customer/add" class="btn btn-add btn-sm">
                <i class="bi bi-person-plus-fill me-1"></i> Add New Customer
            </a>
        </div>
    </div>

    <c:if test="${not empty message}">
        <div class="alert ${success ? 'alert-success' : 'alert-danger'} alert-dismissible fade show shadow-sm border-0" role="alert">
            <div class="d-flex align-items-center">
                <i class="bi ${success ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill'} me-2 fs-5"></i>
                <div>${message}</div>
            </div>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="table-container">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead>
                    <tr>
                        <th class="text-center" style="width: 80px;">ID</th>
                        <th>Customer Details</th>
                        <th class="text-center">Party Type</th>
                        <th class="text-end">Credit Limit</th>
                        <th class="text-center">Current Balance</th>
                        <th class="text-center">Status</th>
                        <th class="text-center">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${customers}">
                        <tr>
                            <td class="text-center text-muted fw-semibold">#${p.id}</td>

                            <td>
                                <div class="customer-name">${p.customerName}</div>
                                <small class="text-muted">ID: ${p.identifier}</small>
                            </td>

                            <td class="text-center">
                                <span class="identifier-badge">${p.partyType}</span>
                            </td>

                            <td class="text-end fw-semibold">
                                <span class="text-secondary">$<fmt:formatNumber value="${p.creditLimit}" pattern="#,##0.00"/></span>
                            </td>

                            <td class="text-center">
                                <span class="${p.balanceType == 'Credit' ? 'balance-cr' : 'balance-dr'}">
                                    $<fmt:formatNumber value="${p.balance}" pattern="#,##0.00"/>
                                    <small class="ms-1 fw-normal opacity-75">${p.balanceType}</small>
                                </span>
                            </td>

                            <td class="text-center">
                                <!-- Status Toggle Form -->
                                <form method="POST" action="${pageContext.request.contextPath}/customer/update-status" class="d-inline">
                                    <input type="hidden" name="identifier" value="${p.identifier}">
                                    <input type="hidden" name="status" value="${!p.status}">

                                    <div class="form-check form-switch d-flex justify-content-center align-items-center gap-2">
                                        <input class="form-check-input" type="checkbox" role="switch"
                                               ${p.status ? "checked" : ""}
                                               onchange="this.form.submit()">
                                        <span class="status-label ${p.status ? 'text-success' : 'text-danger'}">
                                            ${p.status ? 'ACTIVE' : 'INACTIVE'}
                                        </span>
                                    </div>
                                </form>
                            </td>

                            <td class="text-center">
                                <div class="d-flex justify-content-center gap-2">
                                    <!-- EDIT: Standard Link -->
                                    <a href="${pageContext.request.contextPath}/customer/get?identifier=${p.identifier}"
                                       class="btn-action btn-edit">
                                        <i class="bi bi-pencil-square"></i> Edit
                                    </a>

                                    <!-- DELETE: Proper POST Form -->
                                    <form action="${pageContext.request.contextPath}/customer/delete?identifier=${p.identifier}" method="GET"
                                          onsubmit="return confirm('Delete customer ${p.customerName}? This action cannot be undone.');">
                                        <input type="hidden" name="identifier" value="${p.identifier}">
                                        <button type="submit" class="btn-action btn-delete">
                                            <i class="bi bi-trash3"></i> Delete
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty customers}">
                        <tr>
                            <td colspan="7" class="text-center py-5">
                                <div class="opacity-25 mb-3">
                                    <i class="bi bi-people" style="font-size: 4rem;"></i>
                                </div>
                                <p class="text-muted fw-bold">No customer records found.</p>
                                <a href="${pageContext.request.contextPath}/customer/add" class="btn btn-primary btn-sm">
                                    Create First Customer
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