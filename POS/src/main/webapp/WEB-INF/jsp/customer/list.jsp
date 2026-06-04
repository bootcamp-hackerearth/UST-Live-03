<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
          rel="stylesheet">

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
            box-shadow: 0 6px 18px rgba(0,0,0,0.06);
        }

        .table thead th {
            background-color: #f1f3f5;
            font-weight: 600;
            white-space: nowrap;
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
        <h3>Customer Management</h3>

        <div>
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary me-2">
                <i class="bi bi-house-door"></i> Home
            </a>

            <a href="${pageContext.request.contextPath}/customer/add"
               class="btn add-btn">
                <i class="bi bi-plus-circle"></i> Add Customer
            </a>
        </div>
    </div>

    <div class="card shadow">
        <div class="card-body p-0">

            <table class="table table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Customer Name</th>
                    <th>Phone</th>
                    <th>Party Type</th>
                    <th>Credit Type</th>
                    <th>Credit</th>
                    <th>Credit Limit</th>
                    <th class="text-center" style="width:120px;">Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach items="${customers}" var="customer" varStatus="status">
                    <tr>
                        <td>${status.index + 1}</td>
                        <td class="fw-semibold">${customer.customerName}</td>
                        <td>${customer.phoneNo}</td>
                        <td>${customer.partyType}</td>
                        <td>${customer.creditType}</td>
                        <td>${customer.credit}</td>
                        <td>${customer.creditLimit}</td>

                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/customer/get?phoneNo=${customer.phoneNo}"
                               class="action-icon edit-icon"
                               title="Edit">
                                <i class="bi bi-pencil-square"></i>
                            </a>

                            <a href="${pageContext.request.contextPath}/customer/delete?phoneNo=${customer.phoneNo}"
                               class="action-icon delete-icon"
                               title="Delete"
                               onclick="return confirm('Delete this customer?');">
                                <i class="bi bi-trash"></i>
                            </a>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty customers}">
                    <tr>
                        <td colspan="8"
                            class="text-center text-muted py-4">
                            No customers found.
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