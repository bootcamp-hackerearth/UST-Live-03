<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Model Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Arial;
        }

        .page-header {
            background-color: #000;
            color: #fff;
            padding: 14px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }

        .card {
            border-radius: 10px;
            border: 1px solid #e0e0e0;
        }

        .table thead th {
            background-color: #f1f3f5;
            font-weight: 600;
        }

        .btn-sm {
            border-radius: 6px;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 48px;
            height: 24px;
        }

        .switch input {
            display: none;
        }

        .slider {
            position: absolute;
            inset: 0;
            background-color: grey;
            border-radius: 24px;
            transition: .3s;
        }

        .slider:before {
            content: "";
            position: absolute;
            width: 18px;
            height: 18px;
            background-color: #fff;
            border-radius: 50%;
            bottom: 3px;
            left: 3px;
            transition: .3s;
        }

        input:checked + .slider {
            background-color: blue;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }
    </style>
</head>

<body>

<div class="container mt-4">

    <div class="page-header d-flex justify-content-between align-items-center">
        <h3>Model Management</h3>

        <div>
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary btn-sm me-2">
                <i class="bi bi-house-door"></i> Home
            </a>

            <a href="${pageContext.request.contextPath}/model/add"
               class="btn btn-success btn-sm">
                <i class="bi bi-plus-circle"></i> Add Model
            </a>
        </div>
    </div>

    <div class="card shadow">
        <div class="card-body p-0">

            <table class="table table-hover align-middle mb-0">

                <thead>
                <tr>
                    <th>Sl No</th>
                    <th>Model Name</th>
                    <th>Status</th>
                    <th class="text-center">Actions</th>
                </tr>
                </thead>

                <tbody>

                <c:forEach items="${models}" var="m" varStatus="loop">
                    <tr>

                        <td class="fw-semibold">${loop.index + 1}</td>
                        <td>${m.identifier}</td>

                        <td>
                            <a href="${pageContext.request.contextPath}/model/toggle?identifier=${m.identifier}"
                               onclick="return confirm('Change model status?')"
                               style="text-decoration:none">

                                <label class="switch" style="pointer-events:none">
                                    <input type="checkbox" ${m.status ? "checked" : ""}>
                                    <span class="slider"></span>
                                </label>

                            </a>
                        </td>

                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/model/get?identifier=${m.identifier}"
                               class="btn btn-sm btn-outline-primary me-1">
                                <i class="bi bi-pencil-square"></i>
                            </a>

                            <a href="${pageContext.request.contextPath}/model/delete?identifier=${m.identifier}"
                               class="btn btn-sm btn-outline-danger"
                               onclick="return confirm('Are you sure you want to delete this model?');">
                                <i class="bi bi-trash"></i>
                            </a>
                        </td>

                    </tr>
                </c:forEach>

                <c:if test="${empty models}">
                    <tr>
                        <td colspan="4" class="text-center text-muted py-4">
                            No models found
                        </td>
                    </tr>
                </c:if>

                </tbody>

            </table>

        </div>
    </div>

</div>

</body>
</html>