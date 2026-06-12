<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer Management</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        .card-custom {
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            border-radius: 12px;
        }
        .add-btn {
            font-size: 18px;
            padding: 12px 30px;
            border-radius: 30px;
        }
        .back-btn {
            font-size: 18px;
            padding: 12px 30px;
            border-radius: 30px;
            margin-right: 15px;
        }
    </style>
</head>

<body>

<div class="container mt-4">
    <div class="card card-custom p-4">

        <h2 class="text-center mb-4">Customer Management</h2>

        <div class="text-center mb-4">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary back-btn">
                <i class="bi bi-arrow-left-circle"></i>
                Back to Home
            </a>

            <a href="${pageContext.request.contextPath}/customer/add"
               class="btn btn-success add-btn">
                <i class="bi bi-plus-circle"></i>
                Add New Customer
            </a>
        </div>

        <c:if test="${empty customers}">
            <div class="text-center text-muted p-5">
                No customers available
            </div>
        </c:if>

        <c:if test="${not empty customers}">
            <table class="table table-hover table-bordered align-middle">
                <thead class="table-dark text-center">
                <tr>
                    <th>Identifier</th>
                    <th>Customer Name</th>
                    <th>Phone Number</th>
                    <th>Party Type</th>
                    <th>Credit Type</th>
                    <th>Credit</th>
                    <th>Credit Limit</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach items="${customers}" var="customer" varStatus="status">
                    <tr>
                        <td class="text-center">${status.index + 1}</td>
                        <td class="fw-semibold">${customer.customerName}</td>
                        <td>${customer.phoneNo}</td>
                        <td>${customer.partyType}</td>
                        <td>${customer.creditType}</td>
                        <td>${customer.credit}</td>
                        <td>${customer.creditLimit}</td>

                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/customer/get?phoneNo=${customer.phoneNo}"
                               class="btn btn-sm btn-warning">
                                <i class="bi bi-pencil-square"></i>
                                Edit
                            </a>

                            <a href="${pageContext.request.contextPath}/customer/delete?phoneNo=${customer.phoneNo}"
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('Are you sure you want to delete this customer?');">
                                <i class="bi bi-trash"></i>
                                Delete
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>

    </div>
</div>

</body>
</html>