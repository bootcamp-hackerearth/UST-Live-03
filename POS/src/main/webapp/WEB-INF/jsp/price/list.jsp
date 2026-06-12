<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Price Management</title>


    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        .add-price-btn {
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

        .card-custom {
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            border-radius: 12px;
        }
    </style>
</head>

<body>

<div class="container mt-4">

    <div class="card card-custom p-4">
        <h2 class="text-center mb-4">Price Management</h2>


        <div class="text-center mb-4">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary back-btn">
                <i class="bi bi-arrow-left-circle"></i>
                Back to Home
            </a>

            <a href="${pageContext.request.contextPath}/price/add"
               class="btn btn-success add-price-btn">
                <i class="bi bi-plus-circle"></i>
                Add New Price
            </a>
        </div>


        <c:if test="${empty prices}">
            <div class="text-center text-muted p-5">
                No price available
            </div>
        </c:if>


        <c:if test="${not empty prices}">
            <table class="table table-hover table-bordered align-middle">
                <thead class="table-dark text-center">
                <tr>
                    <th>ID</th>
                    <th>Product Name</th>
                    <th>MRP</th>
                    <th>Selling Price</th>
                    <th>Cost Price</th>
                    <th>Effective From</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="price" items="${prices}">
                    <tr>
                        <td class="text-center">${price.id}</td>
                        <td>${price.identifier}</td>
                        <td class="text-center">${price.mrp}</td>
                        <td class="text-center">${price.sellingPrice}</td>
                        <td class="text-center">${price.costPrice}</td>
                        <td class="text-center">${price.effectiveFrom}</td>


                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/price/get?identifier=${price.identifier}"
                               class="btn btn-sm btn-warning">
                                <i class="bi bi-pencil-square"></i>
                                Edit
                            </a>

                            <a href="${pageContext.request.contextPath}/price/delete?identifier=${price.identifier}"
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('Are you sure you want to delete this price?')">
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