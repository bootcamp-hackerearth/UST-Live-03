<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Price Management</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            color: #2e2e2e;
        }

        .page-header {
            background-color: #000;
            padding: 14px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            color: #fff;
        }

        .page-header h3 {
            margin: 0;
            font-weight: 600;
        }

        .card {
            border-radius: 10px;
            border: 1px solid #e0e0e0;
        }

        .card.shadow {
            box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
        }

        .table thead th {
            background-color: #f1f3f5;
            font-weight: 600;
        }

        .badge {
            padding: 6px 10px;
            font-weight: 600;
            font-size: 12px;
        }

        .action-icon {
            font-size: 1.1rem;
            margin: 0 8px;
            transition: transform 0.15s ease;
        }

        .action-icon:hover {
            transform: scale(1.15);
        }

        .edit-icon {
            color: #0d6efd;
        }

        .delete-icon {
            color: #dc3545;
        }

        .add-btn {
            background-color: #198754;
            border-color: #198754;
            color: #fff;
        }
    </style>
</head>

<body>

<div class="container mt-4">

    <div class="page-header d-flex justify-content-between align-items-center">
        <h3>Price Management</h3>

        <div>
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-dark me-2">
                <i class="bi bi-house-door"></i> Home
            </a>

            <a href="${pageContext.request.contextPath}/price/add"
               class="btn add-btn">
                <i class="bi bi-plus-circle"></i> Add Price
            </a>
        </div>
    </div>

    <div class="card shadow">
        <div class="card-body p-0">

            <table class="table table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Identifier</th>
                    <th>Product</th>
                    <th>PriceAmount</th>
                    <th>Price Type</th>
                    <th class="text-center" style="width:120px;">Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach items="${prices}" var="p">
                    <tr>
                        <td>${p.id}</td>

                        <td class="fw-semibold">
                            ${p.identifier}  </td>
                        <td>${p.product}</td>
                        <td>₹ ${p.priceAmount}</td>

                        <td>
                            <span class="badge bg-info text-dark">
                                ${p.type}
                            </span>
                        </td>

                        <td class="text-center">

                            <a href="${pageContext.request.contextPath}/price/get?identifier=${p.identifier}"
                               class="action-icon edit-icon"
                               title="Edit">
                                <i class="bi bi-pencil-square"></i>
                            </a>

                            <a href="${pageContext.request.contextPath}/price/delete?identifier=${p.identifier}"
                               class="action-icon delete-icon"
                               title="Delete"
                               onclick="return confirm('Are you sure you want to delete this price?');">
                                <i class="bi bi-trash"></i>
                            </a>

                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty prices}">
                    <tr>
                        <td colspan="6" class="text-center text-muted py-4">
                            No prices found.
                        </td>
                    </tr>
                </c:if>

                </tbody>
            </table>

        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
