<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Price Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        :root {
            --primary: #2563eb;
            --primary-hover: #1e40af;
            --bg: #f8fafc;
            --glass: rgba(255,255,255,0.75);
            --text: #0f172a;
            --muted: #64748b;
            --border: #e2e8f0;
            --danger: #dc2626;
            --danger-hover: #b91c1c;
            --radius: 16px;
            --shadow: 0 20px 40px rgba(2,6,23,0.08);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Inter', sans-serif;
        }

        body {
            background: var(--bg);
            min-height: 100vh;
            padding: 40px 20px;
            color: var(--text);
        }

        .back-arrow {
            position: fixed;
            top: 20px;
            left: 20px;
            width: 42px;
            height: 42px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            background: var(--glass);
            backdrop-filter: blur(10px);
            border: 1px solid var(--border);
            color: var(--text);
            text-decoration: none;
            font-size: 18px;
            box-shadow: var(--shadow);
            transition: 0.2s;
        }

        .back-arrow:hover {
            background: #eef2ff;
            color: var(--primary);
        }

        .container-box {
            max-width: 1300px;
            margin: 60px auto 0;
        }

        .card {
            background: var(--glass);
            backdrop-filter: blur(16px);
            border-radius: var(--radius);
            border: 1px solid var(--border);
            box-shadow: var(--shadow);
            overflow: hidden;
        }

        .card-header {
            padding: 20px 24px;
            font-size: 18px;
            font-weight: 600;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .add-btn {
            padding: 8px 14px;
            border-radius: 10px;
            background: var(--primary);
            color: #fff;
            text-decoration: none;
            font-size: 13px;
            font-weight: 600;
            transition: 0.2s;
        }

        .add-btn:hover {
            background: var(--primary-hover);
            transform: translateY(-1px);
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 14px 16px;
            font-size: 13px;
            text-align: left;
            border-bottom: 1px solid var(--border);
        }

        th {
            font-size: 11px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            color: var(--muted);
            background: rgba(248,250,252,0.8);
        }

        tr:hover {
            background: rgba(241,245,249,0.6);
        }

        .actions {
            display: flex;
            gap: 8px;
        }

        .btn {
            padding: 6px 12px;
            border-radius: 8px;
            font-size: 12px;
            font-weight: 600;
            text-decoration: none;
            transition: 0.2s;
        }

        .btn-edit {
            background: #eef2ff;
            color: var(--primary);
        }

        .btn-edit:hover {
            background: #e0e7ff;
        }

        .btn-delete {
            background: #fee2e2;
            color: var(--danger);
        }

        .btn-delete:hover {
            background: #fecaca;
        }

        .empty-state {
            text-align: center;
            padding: 40px;
            color: var(--muted);
            font-size: 14px;
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/" class="back-arrow">←</a>

<div class="container-box">

    <div class="card">

        <div class="card-header">
            <span>Price Management</span>

            <a href="${pageContext.request.contextPath}/price/add" class="add-btn">
                + Add Price
            </a>
        </div>

        <c:if test="${empty prices}">
            <div class="empty-state">
                No price records found
            </div>
        </c:if>

        <c:if test="${not empty prices}">
            <table>

                <thead>
                <tr>
                    <th>ID</th>
                    <th>SKU Code</th>
                    <th>Product Name</th>
                    <th>MRP Price</th>
                    <th>Selling Price</th>
                    <th>Cost Price</th>
                    <th style="width:160px;">Actions</th>
                </tr>
                </thead>

                <tbody>

                <c:forEach var="price" items="${prices}">

                    <tr>

                        <td>${price.id}</td>

                        <td>${price.identifier}</td>

                        <td>${price.productName}</td>

                        <td>₹ ${price.mrpPrice}</td>

                        <td>₹ ${price.sellingPrice}</td>

                        <td>₹ ${price.costPrice}</td>

                        <td>

                            <div class="actions">

                                <a class="btn btn-edit"
                                   href="${pageContext.request.contextPath}/price/get?id=${price.id}">
                                    Edit
                                </a>

                                <a class="btn btn-delete"
                                   href="${pageContext.request.contextPath}/price/delete?id=${price.id}">
                                    Delete
                                </a>

                            </div>

                        </td>

                    </tr>

                </c:forEach>

                </tbody>

            </table>
        </c:if>

    </div>

</div>

</body>
</html>