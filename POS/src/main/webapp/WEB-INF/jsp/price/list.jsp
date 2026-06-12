<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Price List</title>

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

        <h2>Price List</h2>

        <div class="header-actions">

            <a href="/" class="btn btn-back">
                Home
            </a>

            <a href="/price/add" class="btn btn-add">
                + Add Price
            </a>

        </div>

    </div>

    <!-- EMPTY -->
    <c:if test="${empty price}">

        <div class="empty-msg">
            No Price Available
        </div>

    </c:if>

    <!-- TABLE -->
    <c:if test="${not empty price}">

        <table>

            <thead>

            <tr>

                <th>ID</th>
                <th>Identifier</th>
                <th>Product</th>
                <th>Price</th>
                <th>Price Type</th>
                <th>Actions</th>

            </tr>

            </thead>

            <tbody>

            <c:forEach var="itemPrice" items="${price}">

                <tr>

                    <td>${itemPrice.id}</td>

                    <td>${itemPrice.identifier}</td>

                    <td>${itemPrice.product}</td>

                    <td>${itemPrice.priceAmount}</td>

                    <td>${itemPrice.priceType}</td>

                    <!-- ACTIONS -->
                    <td>

                        <a href="/price/get?identifier=${itemPrice.identifier}"
                           class="btn btn-edit">
                            Edit
                        </a>

                        <a href="/price/delete?identifier=${itemPrice.identifier}"
                           class="btn btn-delete"
                           onclick="return confirm('Are you sure you want to delete this price?');">
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