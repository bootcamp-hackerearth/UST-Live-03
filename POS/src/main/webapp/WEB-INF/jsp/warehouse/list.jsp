<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>POS Management | Warehouse Management</title>

<style>
    body {
        margin: 0;
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        background: #F4F5F7;
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .container {
        width: 95%;
        max-width: 1100px;
        background: #FFFFFF;
        border-radius: 16px;
        box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        overflow: hidden;
    }

    .brand-header {
        background: #0B3C5D;
        padding: 20px;
        color: #FFFFFF;
        text-align: center;
    }

    .content-body {
        padding: 30px;
    }

    .top-bar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 22px;
    }

    h2 {
        margin: 0;
        font-size: 18px;
        color: #1F2937;
    }

    .btn-add {
        padding: 10px 22px;
        background: #0B3C5D;
        color: white;
        border-radius: 8px;
        text-decoration: none;
        font-size: 14px;
        font-weight: 600;
    }

    table {
        width: 100%;
        border-collapse: collapse;
        font-size: 14px;
        margin-bottom: 20px;
    }

    thead {
        background: #F9FAFB;
        border-bottom: 2px solid #E5E7EB;
    }

    th, td {
        padding: 15px;
        border-bottom: 1px solid #F3F4F6;
        text-align: left;
        vertical-align: middle;
    }

    th {
        font-size: 12px;
        text-transform: uppercase;
        color: #4B5563;
        font-weight: 700;
    }

    tbody tr:hover {
        background: #F9FAFB;
    }

    /* ===== Toggle ===== */
    .switch {
        position: relative;
        display: inline-block;
        width: 42px;
        height: 22px;
    }

    .switch input {
        opacity: 0;
    }

    .slider {
        position: absolute;
        inset: 0;
        background: #E5E7EB;
        border-radius: 20px;
        cursor: pointer;
        transition: 0.3s;
    }

    .slider:before {
        content: "";
        position: absolute;
        height: 16px;
        width: 16px;
        left: 3px;
        bottom: 3px;
        background: #FFFFFF;
        border-radius: 50%;
        transition: 0.3s;
        box-shadow: 0 2px 4px rgba(0,0,0,0.25);
    }

    input:checked + .slider {
        background: #16A34A;
    }

    input:checked + .slider:before {
        transform: translateX(20px);
    }

    .status-label {
        font-size: 12px;
        font-weight: 600;
        color: #6B7280;
        margin-left: 8px;
    }

    .actions {
        display: flex;
        gap: 8px;
        align-items: center;
        justify-content: center;
    }

    .btn-action {
        padding: 6px 14px;
        border-radius: 6px;
        font-size: 12px;
        font-weight: 700;
        text-decoration: none;
        white-space: nowrap;
    }

    .btn-view {
        background: #E5E7EB;
        color: #1F2937;
    }

    .btn-delete {
        background: #FEE2E2;
        color: #DC2626;
    }

    .back-link {
        display: block;
        margin-top: 24px;
        font-size: 14px;
        color: #6B7280;
        text-decoration: none;
        font-weight: 600;
        text-align: center;
    }
</style>
</head>

<body>

<div class="container">

    <div class="brand-header">
        <h1>POS Management</h1>
    </div>

    <div class="content-body">

        <div class="top-bar">
            <h2>Warehouse Management</h2>
            <a href="${pageContext.request.contextPath}/warehouse/add" class="btn-add">
                + Add New Warehouse
            </a>
        </div>

        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Warehouse Name</th>
                <th>Location</th>
                <th>Manager</th>
                <th>Status</th>
                <th style="text-align:center;">Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="warehouse" items="${warehouses}">
                <tr>
                    <td style="font-weight:600; color:#6B7280;">
                        ${warehouse.id}
                    </td>

                    <td style="font-weight:600;">
                        ${warehouse.identifier}
                    </td>

                    <td>${warehouse.location}</td>
                    <td>${warehouse.manager}</td>

                    <!-- ✅ STATUS TOGGLE -->
                    <td>
                        <label class="switch">
                            <input type="checkbox"
                                   ${warehouse.status ? "checked" : ""}
                                   onchange="toggleWarehouseStatus('${warehouse.identifier}', this)">
                            <span class="slider"></span>
                        </label>
                        <span class="status-label">
                            <c:choose>
                                <c:when test="${warehouse.status}">Active</c:when>
                                <c:otherwise>Inactive</c:otherwise>
                            </c:choose>
                        </span>
                    </td>

                    <td style="text-align:center;">
                        <div class="actions">
                            <a href="${pageContext.request.contextPath}/warehouse/get?identifier=${warehouse.identifier}"
                               class="btn-action btn-view">
                                View / Edit
                            </a>

                            <a href="${pageContext.request.contextPath}/warehouse/delete?identifier=${warehouse.identifier}"
                               class="btn-action btn-delete"
                               onclick="return confirm('Are you sure you want to delete this warehouse?')">
                                Delete
                            </a>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <a href="${pageContext.request.contextPath}/" class="back-link">
            ← Back to Homepage
        </a>

    </div>
</div>

<script>
function toggleWarehouseStatus(identifier, checkbox) {
    fetch('${pageContext.request.contextPath}/warehouse/toggle', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: 'identifier=' + encodeURIComponent(identifier)
    }).then(() => {
        const label = checkbox.closest('td').querySelector('.status-label');
        label.textContent = checkbox.checked ? 'Active' : 'Inactive';
    }).catch(() => {
        alert('Failed to update warehouse status');
        checkbox.checked = !checkbox.checked;
    });
}
</script>

</body>
</html>
