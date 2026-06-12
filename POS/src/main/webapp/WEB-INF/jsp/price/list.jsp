<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Price List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #2f2f2f;
            min-height: 100vh;
            font-family: "Segoe UI", sans-serif;
            padding: 40px;
        }

        .container {
            max-width: 900px;
        }

        .card {
            border: none;
            border-radius: 18px;
            background-color: #ffffff;
            box-shadow: 0 15px 40px rgba(0, 0, 0, 0.25);
        }

        .card-header {
            background: transparent;
            border-bottom: none;
            padding: 20px;
        }

        .card-header h2 {
            font-size: 22px;
            font-weight: 600;
            color: #333;
        }

        table th {
            font-weight: 600;
            font-size: 0.9rem;
            color: #444;
        }

        table td {
            font-size: 0.9rem;
        }

        .cost-price {
            color: #dc3545;
            font-weight: 600;
        }

        .selling-price {
            color: #198754;
            font-weight: 600;
        }

        .btn-add {
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            color: #fff;
            border-radius: 20px;
            font-weight: 600;
        }

        .btn-home {
            background: #6c757d;
            color: #fff;
            border-radius: 20px;
            font-weight: 600;
        }

        .btn-home:hover {
            background: #5a6268;
            color: #fff;
        }

        .card-footer {
            text-align: center;
            font-size: 0.85rem;
            color: #555;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="card">

        <div class="card-header d-flex justify-content-between align-items-center">
            <h2>Price List</h2>
            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/"
                   class="btn btn-home btn-sm">
                     Home
                </a>
                <a href="${pageContext.request.contextPath}/price/add"
                   class="btn btn-add btn-sm">
                    + Add Price
                </a>
            </div>
        </div>

        <div class="card-body">

            <c:if test="${empty prices}">
                <div class="alert alert-info text-center">
                    No price details available
                </div>
            </c:if>

            <c:if test="${not empty prices}">
                <table class="table table-bordered table-hover">
                    <thead class="table-light">
                    <tr>
                        <th>Identifier</th>
                        <th>Cost Price</th>
                        <th>Selling Price</th>
                        <th>MRP Price</th>
                        <th style="width: 160px;">Actions</th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:forEach var="price" items="${prices}">
                        <tr>
                            <td>${price.identifier}</td>
                            <td class="cost-price">₹ ${price.costprice}</td>
                            <td class="selling-price">₹ ${price.sellingprice}</td>
                             <td class="mrp-price">₹ ${price.mrpprice}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/price/get?identifier=${price.identifier}"
                                   class="btn btn-sm btn-warning">
                                    Edit
                                </a>
                                <a href="${pageContext.request.contextPath}/price/delete?identifier=${price.identifier}"
                                   class="btn btn-sm btn-danger"
                                   onclick="return confirm('Are you sure?');">
                                    Delete
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>

        </div>

        <div class="card-footer">
            POS Management System
        </div>

    </div>
</div>

</body>
</html>
