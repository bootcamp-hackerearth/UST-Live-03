<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Model Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        body { background-color: #f7f9fc; }

        .page-header {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #fff;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
        }

        .table thead th {
            text-transform: uppercase;
            font-size: 13px;
            letter-spacing: 0.03em;
        }

        .action-btns a { margin-right: 6px; }

        .switch {
            position: relative;
            display: inline-block;
            width: 48px;
            height: 24px;
        }

        .switch input { display: none; }

        .slider {
            position: absolute;
            inset: 0;
            background-color: #dc3545;
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
            background-color: #198754;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }
    </style>
</head>

<body class="container py-4">

<div class="page-header d-flex justify-content-between align-items-center">
    <h4 class="mb-0">
        <i class="bi bi-diagram-3-fill me-2"></i> Model Management
    </h4>

    <div>
        <a href="${pageContext.request.contextPath}/"
           class="btn btn-light btn-sm me-2">
            <i class="bi bi-house-door-fill me-1"></i> Home
        </a>

        <a href="${pageContext.request.contextPath}/model/add"
           class="btn btn-light btn-sm">
            <i class="bi bi-plus-circle me-1"></i> Add Model
        </a>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body">

        <table class="table table-hover align-middle">
            <thead class="table-light">
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
                           style="text-decoration:none; display:inline-block">

                            <label class="switch" style="pointer-events:none">
                                <input type="checkbox" ${m.status ? "checked" : ""}>
                                <span class="slider"></span>
                            </label>

                        </a>
                    </td>

                    <td class="text-center action-btns">
                        <a href="${pageContext.request.contextPath}/model/get?identifier=${m.identifier}"
                           class="btn btn-sm btn-outline-primary"
                           title="Edit">
                            <i class="bi bi-pencil-square"></i>
                        </a>

                        <a href="${pageContext.request.contextPath}/model/delete?identifier=${m.identifier}"
                           class="btn btn-sm btn-outline-danger"
                           title="Delete"
                           onclick="return confirm('Are you sure you want to delete this model?');">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty models}">
                <tr>
                    <td colspan="4" class="text-center text-muted py-4">
                        <i class="bi bi-info-circle me-1"></i>
                        No models found
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>

    </div>
</div>

</body>
</html>