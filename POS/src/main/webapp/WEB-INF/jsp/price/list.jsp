<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Retail Management | Price Management</title>

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

        .brand-header h1 {
            margin: 0;
            font-size: 22px;
            font-weight: 600;
            letter-spacing: 0.8px;
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
            margin-bottom: 20px;
        }

        thead {
            background: #F9FAFB;
            border-bottom: 2px solid #E5E7EB;
        }

        th {
            padding: 15px;
            text-align: left;
            color: #4B5563;
            font-size: 12px;
            text-transform: uppercase;
            font-weight: 700;
        }

        td {
            padding: 15px;
            border-bottom: 1px solid #F3F4F6;
            color: #1F2937;
        }

        tbody tr:hover {
            background-color: #F9FAFB;
        }

        .btn-action {
            padding: 6px 14px;
            border-radius: 6px;
            text-decoration: none;
            font-size: 12px;
            font-weight: 700;
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
        <h1>POS Retail Management</h1>
    </div>

    <div class="content-body">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:25px;">
            <h2 style="margin:0; font-size:18px; color:#1F2937;">Price Management</h2>
            <a href="${pageContext.request.contextPath}/price/add" class="btn-add">
                + Add New Price
            </a>
        </div>

        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Product Name</th>
                <th>MRP</th>
                <th>Selling Price</th>
                <th>Effective From</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="price" items="${prices}">
                <tr>
                    <td style="font-weight:600; color:#6B7280;">${price.id}</td>
                    <td style="font-weight:600;">${price.identifier}</td>
                    <td>₹ ${price.mrp}</td>
                    <td>₹ ${price.sellingPrice}</td>
                    <td>${price.effectiveFrom}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/price/get?identifier=${price.identifier}&effectiveFrom=${price.effectiveFrom}"
                           class="btn-action btn-view">View / Edit</a>

                        <a href="${pageContext.request.contextPath}/price/delete?identifier=${price.identifier}&effectiveFrom=${price.effectiveFrom}"
                           class="btn-action btn-delete"
                           onclick="return confirm('Delete this price record?')">
                            Delete
                        </a>
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