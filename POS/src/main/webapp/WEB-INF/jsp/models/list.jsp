<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Model List</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
            padding: 40px;
            color: #333;
        }

        .container {
            max-width: 1000px;
            margin: 0 auto;
            background: white;
            padding: 35px;
            border-radius: 12px;
            box-shadow: 0 3px 12px rgba(0,0,0,0.1);
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }

        h2 { margin: 0; }

        table {
            width: 100%;
            border-collapse: collapse;
            table-layout: fixed;
        }

        th, td {
            padding: 14px;
            border-bottom: 1px solid #eee;
            text-align: center;
        }

        th {
            background-color: #f8f9fa;
        }

        tr:hover {
            background-color: #f5f7f9;
        }

        .btn {
            padding: 6px 12px;
            text-decoration: none;
            border-radius: 5px;
            font-size: 13px;
            color: white;
        }

        .btn-add { background: #28a745; }
        .btn-edit { background: #007bff; }
        .btn-delete { background: #dc3545; }
        .btn-back { background: #6c757d; }

        .actions {
            display: flex;
            justify-content: center;
            gap: 8px;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 50px;
            height: 26px;
        }

        .switch input { opacity: 0; }

        .slider {
            position: absolute;
            background-color: #dc3545;
            border-radius: 26px;
            top: 0; left: 0; right: 0; bottom: 0;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 20px;
            width: 20px;
            left: 3px;
            bottom: 3px;
            background: white;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #28a745;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }

        .toggle-container {
            display: flex;
            justify-content: center;
            cursor: pointer;
        }

    </style>
</head>

<body>

<div class="container">

    <div class="header">
        <h2>Model List</h2>
        <div>
            <a href="${pageContext.request.contextPath}/" class="btn btn-back">Home</a>
            <a href="${pageContext.request.contextPath}/models/add" class="btn btn-add">+ Add Model</a>
        </div>
    </div>

    <c:if test="${empty models}">
        <div style="text-align:center;">No model available</div>
    </c:if>

    <c:if test="${not empty models}">
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
            <c:forEach var="m" items="${models}">
                <tr>

                    <td>${m.id}</td>
                    <td><strong>${m.identifier}</strong></td>

                    <td>
                        <div class="toggle-container"
                             onclick="window.location.href='${pageContext.request.contextPath}/models/toggle?identifier=${m.identifier}'">

                            <label class="switch">
                                <input type="checkbox" ${m.status ? "checked" : ""} disabled>
                                <span class="slider"></span>
                            </label>

                        </div>
                    </td>

                    <td>
                        <div class="actions">
                            <a href="${pageContext.request.contextPath}/models/get?identifier=${m.identifier}"
                               class="btn btn-edit">Edit</a>

                            <a href="${pageContext.request.contextPath}/models/delete?identifier=${m.identifier}"
                               class="btn btn-delete"
                               onclick="return confirm('Delete this model?');">
                                Delete
                            </a>
                        </div>
                    </td>

                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

</div>

</body>
</html>