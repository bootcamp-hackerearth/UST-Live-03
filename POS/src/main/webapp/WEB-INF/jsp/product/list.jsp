<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <title>Product List</title>
    <style>
        body {
            margin: 0;
            font-family: 'Poppins', 'Segoe UI', Arial, sans-serif;
            background: #fff8f0;
            min-height: 100vh;
            padding: 30px;
        }

        .container {
            width: 90%;
            margin: auto;
            background: #efe3d9;
            padding: 30px;
            border-radius: 16px;
            box-shadow: 0 14px 35px rgba(0, 0, 0, 0.15);
        }

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }

        .page-header h2 {
            margin: 0;
            color: #4a2e2b;
            font-size: 24px;
            font-weight: 700;
        }

        .header-actions {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            min-height: 42px;
            min-width: 140px;
            padding: 10px 20px;
            border-radius: 10px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            color: #fff8f0;
            background-color: #4B2E2B;
            transition: 0.2s ease;
            box-sizing: border-box;
        }

        .btn:hover {
            background-color: #3a2421;
        }

        .btn-edit,
        .btn-delete {
            min-width: 42px;
            min-height: 42px;
            padding: 10px;
        }

        table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            border-radius: 14px;
            overflow: hidden;
        }

        th {
            background-color: #4a2e2b;
            color: #fff8f0;
            padding: 16px;
            font-size: 14px;
            font-weight: 600;
            text-align: center;
        }

        tr {
            height: 70px;
        }

        tbody tr {
            background: #fff8f0;
        }

        tbody tr:nth-child(even) {
            background: #eadfd6;
        }

        tbody tr:hover {
            background: #e2cec1;
        }

        td {
            padding: 14px 16px;
            font-size: 14px;
            color: #000000;
            text-align: center;
        }

        td:nth-child(7) {
            max-width: 260px;
            word-wrap: break-word;
        }

        .action-cell {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 12px;
            white-space: nowrap;
        }

        .action-cell i {
            font-size: 14px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="page-header">
        <h2>Product List</h2>
        <div class="header-actions">
            <a href="${pageContext.request.contextPath}/product/add" class="btn">
                <i class="fa-solid fa-plus"></i>
                Add Product
            </a>
            <a href="${pageContext.request.contextPath}/" class="btn">
                Home
            </a>
        </div>
    </div>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Identifier</th>
            <th>Name</th>
            <th>Unit</th>
            <th>Brand</th>
            <th>Category</th>
            <th>Description</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="product" items="${products}">
            <tr>
                <td>${product.id}</td>
                <td>${product.identifier}</td>
                <td>${product.name}</td>
                <td>${product.unit}</td>
                <td>${product.brand}</td>
                <td>${product.category}</td>
                <td>${product.description}</td>
                <td class="action-cell">
                    <a href="${pageContext.request.contextPath}/product/get?identifier=${product.identifier}"
                       class="btn btn-edit"
                       title="Edit Product">
                       <i class="fa-solid fa-pen"></i>
                    </a>
                    <a href="${pageContext.request.contextPath}/product/delete?identifier=${product.identifier}"
                       class="btn btn-delete"
                       onclick="return confirm('Are you sure you want to delete this product?');"
                       title="Delete Product">
                       <i class="fa-solid fa-trash"></i>
                    </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>