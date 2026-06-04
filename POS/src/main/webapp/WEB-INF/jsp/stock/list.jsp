<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Stock Management</title>


    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">


    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        .add-stock-btn {
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

        .status-low { color: orange; font-weight: bold; }
        .status-out { color: red; font-weight: bold; }
        .status-ok  { color: green; font-weight: bold; }
    </style>
</head>

<body>

<div class="container mt-4">

    <div class="card card-custom p-4">
        <h2 class="text-center mb-4">Stock Management</h2>


        <div class="text-center mb-4">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary back-btn">
                <i class="bi bi-arrow-left-circle"></i> Back
            </a>

            <a href="${pageContext.request.contextPath}/stock/add"
               class="btn btn-success add-stock-btn">
                <i class="bi bi-plus-circle"></i> Add Stock
            </a>
        </div>


        <c:if test="${empty stocks}">
            <div class="text-center text-muted p-5">
                No stock available
            </div>
        </c:if>


        <c:if test="${not empty stocks}">
            <table class="table table-bordered table-hover align-middle">

                <thead class="table-dark text-center">
                <tr>
                    <th>ID</th>
                    <th>Product</th>
                    <th>Warehouse</th>
                    <th>Qty</th>
                    <th>Min Qty</th>
                    <th>Stock Level</th>
                    <th>Active</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="stock" items="${stocks}">
                    <tr>
                        <td class="text-center">${stock.id}</td>
                        <td>${stock.productIdentifier}</td>
                        <td>${stock.warehouseIdentifier}</td>
                        <td class="text-center">${stock.quantity}</td>
                        <td class="text-center">${stock.minimumStock}</td>


                        <td class="text-center">
                            <c:choose>
                                <c:when test="${stock.quantity == 0}">
                                    <span class="status-out">OUT</span>
                                </c:when>
                                <c:when test="${stock.quantity < stock.minimumStock}">
                                    <span class="status-low">LOW</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-ok">OK</span>
                                </c:otherwise>
                            </c:choose>
                        </td>


                        <td class="text-center">
                            <form method="post"
                                  action="${pageContext.request.contextPath}/stock/toggle">

                                <input type="hidden"
                                       name="identifier"
                                       value="${stock.identifier}" />

                                <div class="form-check form-switch d-flex justify-content-center align-items-center">
                                    <input class="form-check-input me-2"
                                           type="checkbox"
                                           ${stock.status ? "checked" : ""}
                                           onchange="this.form.submit()" />

                                    <span class="${stock.status ? 'text-success' : 'text-danger'} fw-semibold">
                                        ${stock.status ? "Active" : "Inactive"}
                                    </span>
                                </div>
                            </form>
                        </td>


                        <td class="text-center">
                            <a href="${pageContext.request.contextPath}/stock/get?identifier=${stock.identifier}"
                               class="btn btn-sm btn-warning">
                                <i class="bi bi-pencil-square"></i> Edit
                            </a>

                            <a href="${pageContext.request.contextPath}/stock/delete?identifier=${stock.identifier}"
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('Delete this stock?');">
                                <i class="bi bi-trash"></i> Delete
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