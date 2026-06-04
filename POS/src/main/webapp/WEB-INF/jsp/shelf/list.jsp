<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Retail Management | Shelf Management</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            background: #F4F5F7;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
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
            font-size: 18px;
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
            vertical-align: middle;
            text-align: left;
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

        .status-wrap {
            display: flex;
            align-items: center;
        }

        .status-label {
            margin-left: 8px;
            font-size: 12px;
            font-weight: 600;
            color: #6B7280;
        }

        .actions {
            display: flex;
            gap: 8px;
            justify-content: center;
            align-items: center;
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
            color: #111827;
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
        <h1>POS Retail Management</h1>
    </div>

    <div class="content">

        <div class="top-bar">
            <h2>Shelf Management</h2>
            <a href="${pageContext.request.contextPath}/shelf/add" class="btn-add">
                + Add New Shelf
            </a>
        </div>

        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Shelf</th>
                <th>Status</th>
                <th style="text-align:center;">Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="shelf" items="${shelves}">
                <tr>
                    <td>#${shelf.id}</td>

                    <td>${shelf.identifier}</td>

                    <td>
                        <div class="status-wrap">
                            <label class="switch">
                                <input type="checkbox"
                                       ${shelf.status ? "checked" : ""}
                                       onchange="toggleShelfStatus('${shelf.identifier}', this)">
                                <span class="slider"></span>
                            </label>
                            <span class="status-label">
                                <c:choose>
                                    <c:when test="${shelf.status}">Active</c:when>
                                    <c:otherwise>Inactive</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </td>

                    <td style="text-align:center;">
                        <div class="actions">
                            <a href="${pageContext.request.contextPath}/shelf/get?identifier=${shelf.identifier}"
                               class="btn-action btn-view">
                                View / Edit
                            </a>

                            <a href="${pageContext.request.contextPath}/shelf/delete?identifier=${shelf.identifier}"
                               class="btn-action btn-delete"
                               onclick="return confirm('Delete this shelf?')">
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
function toggleShelfStatus(identifier, checkbox) {
    fetch('${pageContext.request.contextPath}/shelf/toggle', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'identifier=' + encodeURIComponent(identifier)
    }).catch(() => {
        checkbox.checked = !checkbox.checked;
        alert('Failed to update shelf status');
    });
}
</script>

</body>
</html>