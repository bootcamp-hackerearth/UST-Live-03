<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>POS Retail Management | Customer Management</title>

<style>
    body {
        margin: 0;
        min-height: 100vh;
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        background: #F4F5F7;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .container {
        width: 95%;
        max-width: 1250px;
        background: #FFFFFF;
        border-radius: 16px;
        box-shadow: 0 10px 26px rgba(0,0,0,0.08);
        overflow: hidden;
    }

    .brand-header {
        background: #0B3C5D;
        padding: 22px;
        color: white;
        text-align: center;
    }

    .content {
        padding: 30px 34px;
    }

    .top-bar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 22px;
    }

    h2 {
        margin: 0;
        color: #1F2937;
    }

    .btn-add {
        padding: 10px 22px;
        background: #0B3C5D;
        color: white;
        border-radius: 8px;
        text-decoration: none;
        font-weight: 700;
        font-size: 14px;
    }

    table {
        width: 100%;
        border-collapse: collapse;
        font-size: 13.5px;
    }

    th, td {
        padding: 16px 14px;
        border-bottom: 1px solid #E5E7EB;
        text-align: left;
        vertical-align: middle;
    }

    th {
        background: #F9FAFB;
        text-transform: uppercase;
        font-size: 11.5px;
        color: #6B7280;
        font-weight: 700;
    }

    tbody tr:hover {
        background: #F9FAFB;
    }

    .cust-name {
        font-weight: 700;
        color: #111827;
        font-size: 14px;
    }

    .cust-phone {
        font-size: 12px;
        color: #6B7280;
    }

    .status-label {
        font-size: 12px;
        font-weight: 700;
        color: #6B7280;
        margin-left: 6px;
    }

    .switch {
        position: relative;
        display: inline-block;
        width: 42px;
        height: 22px;
    }

    .switch input { opacity: 0; }

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
        background: white;
        border-radius: 50%;
        transition: 0.3s;
        box-shadow: 0 2px 5px rgba(0,0,0,0.25);
    }

    input:checked + .slider {
        background: #16A34A;
    }

    input:checked + .slider:before {
        transform: translateX(20px);
    }

    .actions {
        display: flex;
        gap: 8px;
    }

    .btn-action {
        padding: 6px 14px;
        border-radius: 6px;
        font-size: 12px;
        font-weight: 700;
        text-decoration: none;
        white-space: nowrap;
    }

    .btn-view { background: #E5E7EB; color: #111827; }
    .btn-delete { background: #FEE2E2; color: #DC2626; }
</style>
</head>

<body>

<div class="container">
    <div class="brand-header">
        <h1>POS Retail Management</h1>
    </div>

    <div class="content">

        <div class="top-bar">
            <h2>Customer Management</h2>
            <a href="${pageContext.request.contextPath}/customer/add" class="btn-add">
                + Add Customer
            </a>
        </div>

        <table>
            <thead>
                <tr>
                    <th>Customer</th>
                    <th>Phone No</th>
                    <th>Party Type</th>
                    <th>Balance</th>
                    <th>Credit</th>
                    <th>Status</th>
                    <th style="text-align:center;">Actions</th>
                </tr>
            </thead>

            <tbody>
                <c:forEach var="c" items="${customers}">
                    <tr>
                        <td>
                            <div class="cust-name">${c.customerName}</div>
                        </td>
                        <td>${c.identifier}</td>
                        <td>${c.partyType}</td>
                        <td>${c.balance} (${c.balanceType})</td>
                        <td>${c.creditLimit}</td>

                        <td>
                            <label class="switch">
                                <input type="checkbox"
                                       ${c.status ? "checked" : ""}
                                       onchange="toggleCustomerStatus('${c.identifier}', this)">
                                <span class="slider"></span>
                            </label>
                            <span class="status-label">
                                <c:choose>
                                    <c:when test="${c.status}">Active</c:when>
                                    <c:otherwise>Inactive</c:otherwise>
                                </c:choose>
                            </span>
                        </td>

                        <td style="text-align:center;">
                            <div class="actions">
                                <a class="btn-action btn-view"
                                   href="${pageContext.request.contextPath}/customer/get?identifier=${c.identifier}">
                                   View / Edit
                                </a>
                                <a class="btn-action btn-delete"
                                   href="${pageContext.request.contextPath}/customer/delete?identifier=${c.identifier}"
                                   onclick="return confirm('Delete this customer?')">
                                   Delete
                                </a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <div style="margin-top:20px;">
            <a href="${pageContext.request.contextPath}/"
               style="font-size:13px; color:#6B7280; text-decoration:none; font-weight:600;">
                ← Back to Home
            </a>
        </div>
    </div>
</div>

<script>
    function toggleCustomerStatus(identifier, checkbox) {
        fetch('${pageContext.request.contextPath}/customer/toggle', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'identifier=' + encodeURIComponent(identifier)
        }).then(() => {
            const label = checkbox.closest('td').querySelector('.status-label');
            label.textContent = checkbox.checked ? 'Active' : 'Inactive';
        }).catch(() => {
            alert('Failed to update status');
            checkbox.checked = !checkbox.checked;
        });
    }
</script>

</body>
</html>
