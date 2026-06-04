<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Retail Management | Brand Management</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", system-ui, -apple-system, BlinkMacSystemFont;
            background: #F5F7FB;
        }

        .container {
            max-width: 1250px;
            margin: 40px auto;
            background: #FFFFFF;
            border-radius: 18px;
            box-shadow: 0 12px 30px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .header {
            background: #0B3C5D;
            padding: 26px;
            color: white;
            text-align: center;
            font-size: 22px;
            font-weight: 700;
        }

        .content {
            padding: 32px 36px 40px;
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 28px;
        }

        .top-bar h2 {
            margin: 0;
            font-size: 22px;
            color: #111827;
        }

        .btn-add {
            background: #0B3C5D;
            color: #FFFFFF;
            padding: 12px 22px;
            border-radius: 10px;
            text-decoration: none;
            font-weight: 700;
            font-size: 14px;
        }

        table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            font-size: 14px;
        }

        thead th {
            background: #F9FAFB;
            color: #6B7280;
            font-size: 12px;
            font-weight: 700;
            text-transform: uppercase;
            padding: 14px 16px;
            text-align: left;
        }

        tbody td {
            padding: 22px 16px;
            border-bottom: 1px solid #E5E7EB;
            vertical-align: middle;
        }

        tbody tr:hover {
            background: #FAFBFF;
        }

        .brand-name {
            font-size: 15px;
            font-weight: 700;
            color: #111827;
            margin-bottom: 2px;
        }

        .brand-id {
            font-size: 12px;
            color: #6B7280;
        }

        .description {
            max-width: 420px;
            color: #374151;
            line-height: 1.5;
        }

        .icon-wrapper {
            width: 56px;
            height: 56px;
            border-radius: 12px;
            border: 1px solid #E5E7EB;
            background: #FFFFFF;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .brand-icon {
            max-width: 36px;
            max-height: 36px;
            object-fit: contain;
        }

        .status-box {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .switch {
            position: relative;
            width: 46px;
            height: 24px;
        }

        .switch input { opacity: 0; }

        .slider {
            position: absolute;
            inset: 0;
            background: #E5E7EB;
            border-radius: 999px;
            cursor: pointer;
            transition: 0.3s;
        }

        .slider:before {
            content: "";
            position: absolute;
            height: 18px;
            width: 18px;
            background: #FFFFFF;
            border-radius: 50%;
            left: 3px;
            bottom: 3px;
            transition: 0.3s;
        }

        input:checked + .slider {
            background: #16A34A;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }

        .status-text {
            font-size: 13px;
            font-weight: 700;
            color: #374151;
        }

        .actions {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .btn-action {
            padding: 8px 14px;
            border-radius: 8px;
            font-size: 12.5px;
            font-weight: 700;
            text-decoration: none;
            text-align: center;
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
            margin-top: 26px;
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #6B7280;
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="header">POS Management</div>

    <div class="content">

        <div class="top-bar">
            <h2>Brand Management</h2>
            <a href="${pageContext.request.contextPath}/brand/add" class="btn-add">
                + Add New Brand
            </a>
        </div>

        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Brand</th>
                <th>Description</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="brand" items="${brands}">
                <tr>
                    <td>${brand.id}</td>

                    <td>
                        <div class="brand-name">${brand.identifier}</div>
                    </td>

                    <td class="description">${brand.description}</td>

                    <td>
                        <div class="status-box">
                            <label class="switch">
                                <input type="checkbox"
                                       ${brand.status ? "checked" : ""}
                                       onchange="toggleBrandStatus('${brand.identifier}', this)">
                                <span class="slider"></span>
                            </label>
                            <span class="status-text">
                                <c:choose>
                                    <c:when test="${brand.status}">Active</c:when>
                                    <c:otherwise>Inactive</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </td>

                    <td>
                        <div class="actions">
                            <a href="${pageContext.request.contextPath}/brand/get?identifier=${brand.identifier}"
                               class="btn-action btn-view">View / Edit</a>
                            <a href="${pageContext.request.contextPath}/brand/delete?identifier=${brand.identifier}"
                               class="btn-action btn-delete"
                               onclick="return confirm('Delete this brand?')">Delete</a>
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
    function toggleBrandStatus(identifier, checkbox) {
        const statusText = checkbox
            .closest('td')
            .querySelector('.status-text');
        fetch('${pageContext.request.contextPath}/brand/toggle', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'identifier=' + encodeURIComponent(identifier)
        })
        .then(() => {
            statusText.textContent = checkbox.checked ? 'Active' : 'Inactive';
        })
        .catch(() => {
            checkbox.checked = !checkbox.checked;
            statusText.textContent = checkbox.checked ? 'Active' : 'Inactive';
            alert('Failed to update brand status');
        });
    }
</script>
</body>
</html>