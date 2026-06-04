<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
    <title>Stock List</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #F6F7F9;
            padding: 30px;
        }

        h2 {
            text-align: center;
            color: #2B2B2B;
            margin-bottom: 20px;
        }

        .message {
            background: #E6F4EA;
            color: #2E7D32;
            padding: 10px 20px;
            border-radius: 6px;
            margin: 10px auto 20px auto;
            width: fit-content;
            text-align: center;
        }

        .action-bar {
            text-align: right;
            margin-bottom: 15px;
        }

        .add-btn {
            padding: 10px 16px;
            background: #2B2B2B;
            color: white;
            border-radius: 6px;
            font-weight: 600;
            text-decoration: none;
        }

        .add-btn:hover {
            background: #444;
        }

        table {
            width: 100%;
            background: #ffffff;
            border-collapse: collapse;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 6px 14px rgba(0,0,0,0.08);
        }

        th {
            background: #2B2B2B;
            color: white;
            padding: 12px;
            font-size: 14px;
        }

        td {
            padding: 10px;
            border-bottom: 1px solid #E5E7EB;
            text-align: center;
            font-size: 14px;
            color: #333;
        }

        tr:hover {
            background: #F3F4F6;
        }

        a {
            color: #2B2B2B;
            font-weight: 600;
            text-decoration: none;
            margin: 0 4px;
        }

        a:hover {
            text-decoration: underline;
        }

        .form-select {
            padding: 6px 8px;
            border-radius: 6px;
            border: 1px solid #D1D5DB;
            font-size: 13px;
        }

        .btn-secondary {
                    padding: 10px 16px;
                    background: #2B2B2B;
                    color: white;
                    border-radius: 6px;
                    font-weight: 600;
                    text-decoration: none;
                }

                .btn-secondary:hover {
                    background: #444;
                }
    </style>
</head>

<body>

<h2>Stock List</h2>

<c:if test="${not empty message}">
    <div class="message">${message}</div>
</c:if>

<div class="action-bar">
    <a href="/stock/add" class="add-btn">+ Add Stock</a>
    <a href="/" class="btn-secondary">Home</a>
</div>

<table>
    <tr>
        <th>ID</th>
        <th>Identifier</th>
        <th>Product</th>
        <th>Quantity</th>
        <th>Status</th>
        <th>Warehouse</th>
        <th>Actions</th>
    </tr>

    <c:forEach items="${stocks}" var="stock">
        <tr>
            <td>${stock.id}</td>
            <td>${stock.identifier}</td>
            <td>${stock.product}</td>
            <td>${stock.quantity}</td>
            <td>${stock.stockStatus}</td>
            <td>${stock.warehouse}</td>

            <td>
                <a href="/stock/get?identifier=${stock.identifier}">Edit</a> |
                <a href="/stock/delete?identifier=${stock.identifier}"
                   onclick="return confirm('Are you sure?')">Delete</a>
            </td>
        </tr>
    </c:forEach>
</table>

</body>
</html>