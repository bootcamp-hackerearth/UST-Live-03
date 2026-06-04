<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Product List</title>

    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet"/>

    <style>
        body {
            min-height: 100vh;
            font-family: "Segoe UI", sans-serif;
            padding: 40px;
            background: radial-gradient(circle at 20% 30%, rgba(255,122,0,0.15), transparent 40%),
                        radial-gradient(circle at 80% 70%, rgba(255,72,0,0.15), transparent 40%),
                        linear-gradient(135deg, #1f1f1f, #3a2e2a);
        }

        .container {
            max-width: 1000px;
            margin: auto;
        }

        .card {
            background: rgba(255,255,255,0.12);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
            border: 1px solid rgba(255,255,255,0.15);
        }

        h4 {
            text-align: center;
            margin-bottom: 20px;
            color: #fff;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background: rgba(255,255,255,0.18);
            color: #fff;
            padding: 12px 14px;
            font-size: 14px;
            text-align: left;
            backdrop-filter: blur(10px);
        }

        td {
            padding: 12px 14px;
            border-bottom: 1px solid rgba(255,255,255,0.15);
            text-align: left;
            color: #f1f1f1;
            font-size: 14px;
        }

        tr:hover {
            background: rgba(255,255,255,0.08);
        }

        .actions {
            display: flex;
            gap: 18px;
            align-items: center;
        }

        .icon-btn {
            border: none;
            background: transparent;
            cursor: pointer;
            font-size: 15px;
        }

        .edit-icon {
            color: #ff7a00;
        }

        .delete-icon {
            color: #ff4d4d;
        }

        .icon-btn:hover {
            transform: scale(1.1);
        }

        .btn-secondary {
            background: rgba(255,255,255,0.2);
            color: #fff;
            padding: 8px 16px;
            border-radius: 20px;
            text-decoration: none;
        }

        .btn-success {
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: white;
            padding: 8px 16px;
            border-radius: 20px;
            text-decoration: none;
        }

        .empty {
            text-align: center;
            padding: 20px;
            color: #ddd;
        }

        .footer {
            margin-top: 20px;
            text-align: center;
        }
        td {
            word-wrap: break-word;
            word-break: break-word;
            white-space: normal;
            max-width: 200px;
        }

        table {
            table-layout: fixed;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="card">

        <h4>List of Products</h4>

        <c:if test="${empty products}">
            <div class="empty">
                No products found
            </div>
        </c:if>

        <c:if test="${not empty products}">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Product Id</th>
                    <th>Product</th>
                    <th>Unit</th>
                    <th>Price</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="product" items="${products}">
                    <tr>
                        <td>${product.id}</td>
                        <td>${product.identifier}</td>
                        <td>${product.name}</td>
                        <td>${product.unit}</td>
                        <td>${product.price}</td>
                        <td>
                            <div class="actions">
                                <a href="/product/get?identifier=${product.identifier}" class="icon-btn edit-icon">
                                    <i class="fas fa-pen"></i>
                                </a>

                                <a href="/product/delete?identifier=${product.identifier}"
                                   class="icon-btn delete-icon"
                                   onclick="return confirm('Delete this product?');">
                                    <i class="fas fa-trash"></i>
                                </a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>

    </div>

    <div class="footer">
        <a href="/" class="btn-secondary">Home</a>
        <a href="/product/add" class="btn-success">+ Add New Product</a>

        <div style="margin-top:10px; font-size:12px; color:#ddd;">
            POS Management System
        </div>
    </div>

</div>

</body>
</html>