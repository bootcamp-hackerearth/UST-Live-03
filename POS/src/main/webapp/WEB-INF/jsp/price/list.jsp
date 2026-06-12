<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Price Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #f7f9fc;
        }

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

        .badge {
            font-size: 12px;
        }

        .action-btns a {
            margin-right: 6px;
        }
    </style>
</head>

<body class="container py-4">

<div class="page-header d-flex justify-content-between align-items-center">
    <h4 class="mb-0">
        <i class="bi bi-currency-rupee me-2"></i> Price Management
    </h4>

    <div>
        <a href="${pageContext.request.contextPath}/"
           class="btn btn-light btn-sm me-2">
            <i class="bi bi-house-door-fill me-1"></i> Home
        </a>

        <a href="${pageContext.request.contextPath}/price/add"
           class="btn btn-light btn-sm">
            <i class="bi bi-plus-circle me-1"></i> Add Price
        </a>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body">

        <table class="table table-hover align-middle">
            <thead class="table-light">
            <tr>
                <th>ID</th>
                <th>Identifier</th>
                <th>Cost Price</th>
                <th>Price Type</th>
                <th class="text-center">Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach items="${prices}" var="p">
                <tr>
                    <td class="fw-semibold">${p.id}</td>
                    <td>${p.identifier}</td>
                    <td>₹ ${p.costPrice}</td>

                    <td>
                        <span class="badge bg-info text-dark">
                            ${p.type}
                        </span>
                    </td>

                    <td class="text-center action-btns">
                        <a href="${pageContext.request.contextPath}/price/get?identifier=${p.identifier}"
                           class="btn btn-sm btn-outline-primary"
                           title="Edit">
                            <i class="bi bi-pencil-square"></i>
                        </a>

                        <a href="${pageContext.request.contextPath}/price/delete?identifier=${p.identifier}"
                           class="btn btn-sm btn-outline-danger"
                           title="Delete"
                           onclick="return confirm('Are you sure you want to delete this price?');">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty prices}">
                <tr>
                    <td colspan="5" class="text-center text-muted py-4">
                        <i class="bi bi-info-circle me-1"></i>
                        No prices found
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>

    </div>
</div>

</body>
</html>