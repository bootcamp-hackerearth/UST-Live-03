<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Management | Category Management</title>

    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: #F4F5F7;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .container {
            width: 95%;
            max-width: 950px;
            background: #ffffff;
            padding: 0;
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

        .brand-header h1 {
            margin: 0;
            font-size: 22px;
            font-weight: 600;
            letter-spacing: 0.8px;
        }

        .content-body {
            padding: 30px;
        }

        .btn-add {
            padding: 10px 20px;
            background: #0B3C5D;
            color: white;
            border-radius: 8px;
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
            transition: opacity 0.2s;
        }

        .btn-add:hover {
            opacity: 0.9;
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

        th {
            padding: 15px;
            text-align: left;
            color: #4B5563;
            font-size: 12px;
            text-transform: uppercase;
            font-weight: 700;
        }

        td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #F3F4F6;
            color: #1F2937;
        }

        tbody tr:hover {
            background-color: #F9FAFB;
        }

        .btn-action {
            padding: 6px 14px;
            border-radius: 6px;
            text-decoration: none;
            font-size: 12px;
            font-weight: 700;
            transition: opacity 0.2s;
            display: inline-block;
        }

        .btn-view {
            background: #E5E7EB;
            color: #1F2937;
            margin-right: 8px;
        }

        .btn-delete {
            background: #FEE2E2;
            color: #DC2626;
        }

        .btn-action:hover {
            opacity: 0.8;
        }

        .back-link {
            display: block;
            margin-top: 15px;
            font-size: 14px;
            color: #6B7280;
            text-decoration: none;
            font-weight: 600;
            text-align: center;
        }

        .back-link:hover {
            color: #0B3C5D;
            text-decoration: underline;
        }

        /* ===== Toggle Switch ===== */
        .switch {
            position: relative;
            display: inline-block;
            width: 42px;
            height: 22px;
            vertical-align: middle;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            inset: 0;
            cursor: pointer;
            background: #E5E7EB;
            border-radius: 20px;
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

        .status-label {
            font-size: 12px;
            font-weight: 600;
            color: #6B7280;
            margin-left: 8px;
        }

    </style>
</head>

<body>
    <div class="container">
        <div class="brand-header">
            <h1>POS Management</h1>
        </div>

        <div class="content-body">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:25px;">
                <h2 style="margin:0; font-size:18px; color:#1F2937;">
                    Category Management
                </h2>
                <a href="${pageContext.request.contextPath}/category/add" class="btn-add">
                    + Add New Category
                </a>
            </div>

            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Category</th>
                        <th>Super Category</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="category" items="${categories}">
                        <tr>
                            <td style="font-weight:600; color:#6B7280;">
                                ${category.id}
                            </td>
                            <td style="font-weight:600;">
                                ${category.identifier}
                            </td>
                            <td style="color:#6B7280;">
                                ${category.superCategory}
                            </td>
                            <td>
                                <label class="switch">
                                    <input type="checkbox"
                                           ${category.status ? "checked" : ""}
                                           onchange="toggleCategoryStatus('${category.identifier}', this)">
                                    <span class="slider"></span>
                                </label>

                                <span class="status-label">
                                    <c:choose>
                                        <c:when test="${category.status}">Active</c:when>
                                        <c:otherwise>Inactive</c:otherwise>
                                    </c:choose>
                                </span>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/category/get?identifier=${category.identifier}"
                                   class="btn-action btn-view">
                                    View / Edit
                                </a>

                                <a href="${pageContext.request.contextPath}/category/delete?identifier=${category.identifier}"
                                   class="btn-action btn-delete"
                                   onclick="return confirm('Are you sure you want to delete this category?')">
                                    Delete
                                </a>
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
        function toggleCategoryStatus(identifier, checkbox) {
            fetch('${pageContext.request.contextPath}/category/toggle', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
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
