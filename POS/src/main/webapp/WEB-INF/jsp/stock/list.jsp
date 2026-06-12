<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Stock List</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
            padding: 40px;
            color: #333;
        }

        .container {
            max-width: 1100px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            border-bottom: 2px solid #eee;
            padding-bottom: 15px;
        }

        .header-actions {
            display: flex;
            gap: 10px;
        }

        h2 {
            margin: 0;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background-color: #f8f9fa;
            padding: 12px;
            text-align: left;
            font-size: 14px;
            color: #555;
            border-bottom: 2px solid #dee2e6;
        }

        td {
            padding: 12px;
            border-bottom: 1px solid #eee;
            font-size: 14px;
        }

        tr:hover {
            background-color: #fafafa;
        }

        .btn {
            padding: 8px 14px;
            border-radius: 5px;
            text-decoration: none;
            color: white;
            font-size: 13px;
            font-weight: bold;
            display: inline-block;
        }

        .btn-back {
            background-color: #6c757d;
        }

        .btn-add {
            background-color: #28a745;
        }

        .btn-edit {
            background-color: #007bff;
            padding: 6px 12px;
            margin-right: 5px;
        }

        .btn-delete {
            background-color: #dc3545;
            padding: 6px 12px;
        }

        .status-active {
            color: green;
            font-weight: bold;
        }

        .status-inactive {
            color: red;
            font-weight: bold;
        }

        .empty-msg {
            text-align: center;
            padding: 40px;
            color: #999;
        }

    </style>
</head>

<body>

<div class="container">

    <!-- HEADER -->
    <div class="header">

        <h2>Stock List</h2>

        <div class="header-actions">

            <a href="/" class="btn btn-back">
                Home
            </a>

            <a href="/stock/add" class="btn btn-add">
                + Add Stock
            </a>

        </div>

    </div>

    <!-- EMPTY -->
    <c:if test="${empty stocks}">

        <div class="empty-msg">
            No Stock Available
        </div>

    </c:if>

    <!-- TABLE -->
    <c:if test="${not empty stocks}">

        <table>

            <thead>

            <tr>

                <th>ID</th>
                <th>Product </th>
                <th>Warehouse </th>
                <th>Quantity</th>
                <th>Minimum Stock</th>
                <th>Status</th>
                <th>Actions</th>

            </tr>

            </thead>

            <tbody>

            <c:forEach var="stock" items="${stocks}">

                <tr>

                    <td>${stock.id}</td>

                    <td>${stock.product}</td>

                    <td>${stock.warehouse}</td>

                    <td>${stock.quantity}</td>

                    <td>${stock.minimumStock}</td>

                    <!-- STATUS -->
                    <td>

                        <c:choose>

                            <c:when test="${stock.stockStatus == 'Available'}">
                                <span class="status-active">
                                    AVAILABLE
                                </span>
                            </c:when>

                            <c:when test="${stock.stockStatus == 'Low Stock'}">
                                <span style="color: orange; font-weight: bold;">
                                    LOW STOCK
                                </span>
                            </c:when>

                            <c:otherwise>
                                <span class="status-inactive">
                                    OUT OF STOCK
                                </span>
                            </c:otherwise>

                        </c:choose>

                    </td>
                    <!-- ACTIONS -->
                    <td>

                        <a href="/stock/get?identifier=${stock.identifier}"
                           class="btn btn-edit">
                            Edit
                        </a>

                        <a href="/stock/delete?identifier=${stock.identifier}"
                           class="btn btn-delete"
                           onclick="return confirm('Are you sure you want to delete this stock?');">
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