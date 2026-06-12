<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Management | Model Management</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: #F4F5F7;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            width: 95%;
            max-width: 1100px;
            background: #fff;
            border-radius: 16px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .brand-header {
            background: #0B3C5D;
            padding: 20px;
            color: white;
            text-align: center;
            font-size: 22px;
            font-weight: 600;
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
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
        }

        th {
            padding: 15px;
            background: #F9FAFB;
            color: #4B5563;
            font-weight: 700;
            text-transform: uppercase;
            font-size: 12px;
            text-align: left;
        }

        td {
            padding: 15px;
            border-bottom: 1px solid #F3F4F6;
            color: #1F2937;
            vertical-align: middle;
        }

        tbody tr:hover {
            background: #F9FAFB;
        }

        /* ===== Toggle Switch ===== */
        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 24px;
            margin-right: 8px;
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
            background-color: #E5E7EB;
            transition: 0.4s;
            border-radius: 20px;
            cursor: pointer;
        }

        .slider:before {
            content: "";
            position: absolute;
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: 0.4s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #16A34A;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }

        .status-label {
            font-size: 12px;
            font-weight: 600;
        }

        .btn-action {
            padding: 6px 14px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 700;
            text-decoration: none;
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

        .back-link {
            display: block;
            margin-top: 20px;
            text-align: center;
            font-size: 14px;
            color: #6B7280;
            font-weight: 600;
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="brand-header">POS Management</div>

    <div class="content-body">

        <div style="display:flex; justify-content:space-between; margin-bottom:25px;">
            <h2 style="margin:0;">Model Management</h2>
            <a href="${pageContext.request.contextPath}/models/add" class="btn-add">
                + Add New Model
            </a>
        </div>

        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Model Name</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="model" items="${models}">
                <tr>
                    <td>${model.id}</td>
                    <td><b>${model.identifier}</b></td>

                    <td>
                        <label class="switch">
                            <input type="checkbox"
                                   ${model.status ? "checked" : ""}
                                   onchange="toggleModelStatus('${model.identifier}', this)">
                            <span class="slider"></span>
                        </label>
                        <span class="status-label">
                            <c:choose>
                                <c:when test="${model.status}">Active</c:when>
                                <c:otherwise>Inactive</c:otherwise>
                            </c:choose>
                        </span>
                    </td>

                    <td>
                        <a href="${pageContext.request.contextPath}/models/get?identifier=${model.identifier}"
                           class="btn-action btn-view">View / Edit</a>

                        <a href="${pageContext.request.contextPath}/models/delete?identifier=${model.identifier}"
                           class="btn-action btn-delete"
                           onclick="return confirm('Delete this model?')">Delete</a>
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
function toggleModelStatus(identifier, checkbox) {
    const label = checkbox.closest('td').querySelector('.status-label');
    fetch('${pageContext.request.contextPath}/models/toggle', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'identifier=' + encodeURIComponent(identifier)
    })
    .then(() => {
        label.textContent = checkbox.checked ? 'Active' : 'Inactive';
    })
    .catch(() => {
        alert('Failed to update model status');
        checkbox.checked = !checkbox.checked;
        label.textContent = checkbox.checked ? 'Active' : 'Inactive';
    });
}
</script>

</body>
</html>