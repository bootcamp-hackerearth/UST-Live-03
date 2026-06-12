<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Price Management</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #ffffff;
        }

        /* ===== CONTAINER ===== */
        .container {
            width: 95%;
            max-width: 1100px;
            margin: 40px auto;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            padding: 18px;
        }

        .app-title {
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #14b8a6;
            margin-bottom: 4px;
        }

        h2 {
            text-align: center;
            font-size: 22px;
            margin-bottom: 12px;
        }

        /* ===== ACTIONS ===== */
        .list-actions {
            display: flex;
            justify-content: flex-end;
            gap: 8px;
            margin-bottom: 12px;
        }

        .home-btn {
            padding: 7px 16px;
            background: #ffffff;
            color: teal;
            text-decoration: none;
            border-radius: 18px;
            font-size: 13px;
            font-weight: 600;
            border: 1px solid teal;
        }

        .add-btn {
            padding: 7px 16px;
            background: teal;
            color: #ffffff;
            text-decoration: none;
            border-radius: 18px;
            font-size: 13px;
            font-weight: 600;
        }

        /* ===== TABLE ===== */
        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 12px;
            text-align: center;
        }

        th {
            background-color: #f1f5f9;
            font-size: 13px;
            font-weight: 700;
            border-bottom: 1px solid #e5e7eb;
        }

        td {
            border-bottom: 1px solid #e5e7eb;
            font-size: 13px;
        }

        tr:hover {
            background: #f8fafc;
        }

        /* ===== ACTION BUTTONS ===== */
        .action-link {
            padding: 6px 12px;
            border-radius: 18px;
            font-size: 12px;
            font-weight: 600;
            color: #ffffff;
            text-decoration: none;
        }

        .edit {
            background-color: teal;
        }

        .delete {
            background-color: #ef4444;
            margin-left: 6px;
        }

        .empty {
            text-align: center;
            padding: 18px;
            font-size: 14px;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="app-title">POS Application</div>

    <h2>Price Management</h2>

    <div class="list-actions">

        <a href="${pageContext.request.contextPath}/" class="home-btn">
            Home
        </a>

        <a href="${pageContext.request.contextPath}/price/add" class="add-btn">
            Add Price
        </a>

    </div>

    <c:if test="${empty prices}">
        <div class="empty">No price records found</div>
    </c:if>

    <c:if test="${not empty prices}">

        <table>

            <thead>
            <tr>
                <th>Sku Code</th>
                <th>MRP</th>
                <th>Selling Price</th>
                <th>Cost Price</th>
                <th>Effective From</th>
                <th>Action</th>
            </tr>
            </thead>

            <tbody>

            <c:forEach var="price" items="${prices}">
                <tr>

                    <td>${price.identifier}</td>
                    <td>${price.mrp}</td>
                    <td>${price.sellingPrice}</td>
                    <td>${price.costPrice}</td>
                    <td>${price.effectiveFrom}</td>

                    <td>

                        <a href="${pageContext.request.contextPath}/price/get?identifier=${price.identifier}"
                           class="action-link edit">
                            Edit
                        </a>

                        <a href="${pageContext.request.contextPath}/price/delete?identifier=${price.identifier}"
                           class="action-link delete"
                           onclick="return confirm('Delete price for this product?');">
                            Delete
                        </a>

                    </td>

                </tr>
            </c:forEach>

            </tbody>

        </table>

    </c:if>

</div>

</body>
</html>