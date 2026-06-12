<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Management | Product Management</title>

    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: #F4F5F7;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .container {
            width: 95%;
            max-width: 1100px;
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .brand-header {
            background: #0B3C5D;
            padding: 20px;
            color: #FFFFFF;
            text-align: center;
        }

        .content-body {
            padding: 30px;
        }

        .btn-add {
            padding: 10px 20px;
            background: #0B3C5D;
            color: white;
            border-radius: 8px;
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
        }

        thead {
            background: #F9FAFB;
            border-bottom: 2px solid #E5E7EB;
        }

        th, td {
            padding: 15px;
            text-align: left;
        }

        th {
            color: #4B5563;
            font-size: 12px;
            text-transform: uppercase;
            font-weight: 700;
        }

        td {
            border-bottom: 1px solid #F3F4F6;
            color: #1F2937;
        }

        tbody tr:hover {
            background-color: #F9FAFB;
        }

        .btn-action {
            padding: 6px 14px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 700;
            text-decoration: none;
            display: inline-block;
        }

        .btn-view {
            background: #E5E7EB;
            color: #1F2937;
            margin-right: 8px;
        }

        .btn-delete {
            background: #FEE2E2;
            color: #DC2626;
            border: none;
            cursor: pointer;
        }

        .back-link {
            display: block;
            margin-top: 15px;
            font-size: 14px;
            color: #6B7280;
            text-decoration: none;
            font-weight: 600;
            text-align: center;
        }
    </style>
</head>

<body>
<div class="container">
    <div class="brand-header">
        <h1>POS Management</h1>
    </div>

    <div class="content-body">

        <div style="display:flex; justify-content:space-between; margin-bottom:25px;">
            <h2 style="font-size:18px;">Product Management</h2>
            <a href="${pageContext.request.contextPath}/product/add" class="btn-add">
                + Add New Product
            </a>
        </div>

        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>SKU Code</th>
                <th>Product Name</th>
                <th>Category</th>
                <th>Brand</th>
                <th>Model</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="product" items="${products}">
                <tr>
                    <td>${product.id}</td>
                    <td><strong>${product.identifier}</strong></td>
                    <td><strong>${product.name}</strong></td>
                    <td>
                        <c:forEach var="c" items="${product.categories}" varStatus="s">
                            ${c}<c:if test="${!s.last}">, </c:if>
                        </c:forEach>
                    </td>
                    <td>${product.brand}</td>
                    <td>${product.model}</td>

                    <td>
                        <a href="${pageContext.request.contextPath}/product/get?identifier=${product.identifier}"
                           class="btn-action btn-view">
                            View / Edit
                        </a>

                        <form action="${pageContext.request.contextPath}/product/delete"
                              method="get"
                              style="display:inline;"
                              onsubmit="return confirm('Delete this product?');">

                            <input type="hidden" name="identifier" value="${product.identifier}" />

                            <button type="submit" class="btn-action btn-delete">
                                Delete
                            </button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <a href="${pageContext.request.contextPath}/" class="back-link">
            ← Back to Homepage
        </a>
    </div>
</div>
</body>
</html>