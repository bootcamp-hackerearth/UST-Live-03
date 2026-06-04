<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Price Management | List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            background-color: #f4f7f6;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .container { margin-top: 50px; }

        .page-header {
            background: #ffffff;
            padding: 20px;
            border-radius: 15px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            margin-bottom: 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .table-container {
            background: #ffffff;
            border-radius: 15px;
            padding: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }

        .table thead {
            background-color: #0d6efd;
            color: white;
        }

        .badge-mrp { background-color: #6c757d; }
        .badge-selling { background-color: #198754; }

        .status-label {
            font-size: 0.85rem;
            font-weight: 600;
        }

        .btn-action {
            padding: 5px 10px;
            border-radius: 8px;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="page-header">
        <h2 class="mb-0 text-primary">Price List</h2>

        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary">
                Home
            </a>

            <a href="${pageContext.request.contextPath}/price/add" class="btn btn-primary">
                Add New Price
            </a>
        </div>
    </div>

    <div class="table-container">
        <table class="table table-hover align-middle">
            <thead>
            <tr>
                <th>ID</th>
                <th>Identifier</th>
                <th>MRP</th>
                <th>Selling Price</th>
                <th>Effective From</th>
                <th>Status</th>
                <th class="text-center">Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="price" items="${price}">
                <tr>
                    <td class="fw-bold">#${price.id}</td>
                    <td class="text-muted">${price.identifier}</td>

                    <td>
                        <span class="badge badge-mrp">
                            <fmt:formatNumber value="${price.mrp}" type="currency" currencySymbol="₹"/>
                        </span>
                    </td>

                    <td>
                        <span class="badge badge-selling">
                            <fmt:formatNumber value="${price.sellingPrice}" type="currency" currencySymbol="₹"/>
                        </span>
                    </td>

                    <td>
                        <fmt:parseDate value="${price.effectiveFrom}"
                                       pattern="yyyy-MM-dd'T'HH:mm"
                                       var="parsedDate"/>
                        <fmt:formatDate value="${parsedDate}" pattern="dd MMM yyyy, hh:mm a"/>
                    </td>

                    <td class="text-center">
                        <form method="get"
                              action="${pageContext.request.contextPath}/price/toggle"
                              class="d-inline">

                            <input type="hidden" name="identifier" value="${price.identifier}"/>
                            <input type="hidden" name="status" value="${!price.status}"/>

                            <div class="form-check form-switch d-flex justify-content-center align-items-center gap-2">
                                <input class="form-check-input"
                                       type="checkbox"
                                       ${price.status ? "checked" : ""}
                                       onchange="this.form.submit()"/>

                                <span class="status-label ${price.status ? 'text-success' : 'text-danger'}">
                                    ${price.status ? 'Active' : 'Inactive'}
                                </span>
                            </div>
                        </form>
                    </td>

                    <td class="text-center">
                        <a href="${pageContext.request.contextPath}/price/get?identifier=${price.identifier}"
                           class="btn btn-outline-primary btn-sm btn-action me-1">
                            Edit
                        </a>

                        <a href="${pageContext.request.contextPath}/price/delete?identifier=${price.identifier}"
                           class="btn btn-outline-danger btn-sm btn-action"
                           onclick="return confirm('Are you sure you want to delete this price entry?')">
                            Delete
                        </a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty price}">
                <tr>
                    <td colspan="7" class="text-center py-5 text-muted">
                        No price records found.
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>