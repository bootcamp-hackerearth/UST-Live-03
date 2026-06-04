<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">

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
        }
    </style>
</head>

<body class="container py-4">

<div class="page-header d-flex justify-content-between align-items-center">
    <h4 class="mb-0">
        <i class="bi bi-people-fill me-2"></i> Customer Management
    </h4>

    <div>
        <a href="${pageContext.request.contextPath}/"
           class="btn btn-light btn-sm me-2">
            <i class="bi bi-house-door-fill me-1"></i> Home
        </a>

        <a href="${pageContext.request.contextPath}/customer/add"
           class="btn btn-light btn-sm">
            <i class="bi bi-plus-circle me-1"></i> Add Customer
        </a>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body">

        <table class="table table-hover align-middle">
            <thead class="table-light">
            <tr>
                <th>#</th>
                <th>Customer Name</th>
                <th>Phone</th>
                <th>Party Type</th>
                <th>Credit Type</th>
                <th>Credit</th>
                <th>Credit Limit</th>
                <th class="text-center">Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach items="${customers}" var="customer" varStatus="status">
                <tr>
                    <td>${status.index + 1}</td>
                    <td>${customer.customerName}</td>
                    <td>${customer.phoneNo}</td>
                    <td>${customer.partyType}</td>
                    <td>${customer.creditType}</td>
                    <td>${customer.credit}</td>
                    <td>${customer.creditLimit}</td>

                    <td class="text-center">
                        <a href="${pageContext.request.contextPath}/customer/get?phoneNo=${customer.phoneNo}"
                           class="btn btn-sm btn-outline-primary">
                            <i class="bi bi-pencil"></i>
                        </a>

                        <a href="${pageContext.request.contextPath}/customer/delete?phoneNo=${customer.phoneNo}"
                           class="btn btn-sm btn-outline-danger"
                           onclick="return confirm('Delete this customer?');">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty customers}">
                <tr>
                    <td colspan="8" class="text-center text-muted">
                        No customers found
                    </td>
                </tr>
            </c:if>

            </tbody>
        </table>

    </div>
</div>

</body>
</html>