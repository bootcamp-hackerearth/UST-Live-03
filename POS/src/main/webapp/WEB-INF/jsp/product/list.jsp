<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Product List</title>

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
    </style>
</head>

<body>

<h2>Product List</h2>

<c:if test="${not empty message}">
    <div class="message">${message}</div>
</c:if>

<div class="action-bar">
    <a href="/product/add" class="add-btn">+ Add Product</a>
    <a href="/" class="add-btn">Home</a>
</div>

<table>
    <tr>
        <th>ID</th>
        <th>Identifier</th>
        <th>Name</th>
        <th>Unit</th>
        <th>Price</th>
        <th>Category</th>
        <th>Brand</th>
        <th>Model</th>
        <th>Actions</th>
    </tr>

    <c:forEach items="${products}" var="product">
        <tr>
            <td>${product.id}</td>
            <td>${product.identifier}</td>
            <td>${product.name}</td>

            <td>${product.unit}</td>
            <td>${product.price}</td>
            <td>${product.category}</td>
            <td>${product.brand}</td>
            <td>${product.model}</td>

            <td>
                <a href="/product/get?identifier=${product.identifier}">Edit</a> |
                <a href="/product/delete?identifier=${product.identifier}"
                   onclick="return confirm('Are you sure?')">Delete</a>
            </td>
        </tr>
    </c:forEach>
</table>

</body>
</html>