<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Product Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        :root {
            --primary: #2563eb;
            --bg: #f8fafc;
            --glass: rgba(255, 255, 255, 0.75);
            --text: #0f172a;
            --muted: #64748b;
            --border: #e2e8f0;
            --danger: #dc2626;
            --success: #16a34a;
            --radius: 16px;
            --shadow: 0 20px 40px rgba(2, 6, 23, 0.08);
        }

        * {
            box-sizing: border-box;
            font-family: 'Inter', sans-serif;
        }

        body {
            min-height: 100vh;
            padding: 40px 20px;
            background: var(--bg);
        }

        .back-arrow {
            position: fixed;
            top: 20px;
            left: 20px;

            display: flex;
            align-items: center;
            justify-content: center;

            width: 42px;
            height: 42px;

            color: var(--text);
            font-size: 18px;
            text-decoration: none;

            background: var(--glass);
            border: 1px solid var(--border);
            border-radius: 50%;
            backdrop-filter: blur(10px);

            box-shadow: var(--shadow);

            transition:
                    transform 0.2s ease,
                    box-shadow 0.2s ease,
                    background 0.2s ease,
                    color 0.2s ease;
        }

        .back-arrow:hover {
            color: var(--primary);
            background: #eef2ff;
            transform: translateY(-2px) translateX(-2px);
            box-shadow: 0 12px 28px rgba(37, 99, 235, 0.25);
        }

        .container-box {
            max-width: 1200px;
            margin: 60px auto 0;
        }

        .card {
            overflow: hidden;
            background: var(--glass);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            backdrop-filter: blur(16px);
            box-shadow: var(--shadow);
        }

        .card-header {
            display: flex;
            align-items: center;
            justify-content: space-between;

            padding: 20px 24px;

            font-size: 18px;
            font-weight: 600;
        }

        .add-btn {
            padding: 8px 14px;

            color: #fff;
            font-size: 13px;
            font-weight: 600;
            text-decoration: none;

            background: var(--primary);
            border-radius: 10px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th,
        td {
            padding: 14px 16px;
            font-size: 13px;
            border-bottom: 1px solid var(--border);
            vertical-align: middle;
        }

        th {
            color: var(--muted);
            font-size: 11px;
            text-transform: uppercase;
            background: rgba(248, 250, 252, 0.9);
        }

        .toggle-switch {
            position: relative;

            width: 46px;
            height: 24px;

            cursor: pointer;
            background: #cbd5f5;
            border-radius: 999px;

            transition: 0.25s;
        }

        .toggle-switch::after {
            content: "";

            position: absolute;
            top: 3px;
            left: 4px;

            width: 18px;
            height: 18px;

            background: #fff;
            border-radius: 50%;

            transition: 0.25s;
        }

        .toggle-switch.active {
            background: var(--success);
        }

        .toggle-switch.active::after {
            transform: translateX(20px);
        }

        .actions {
            display: flex;
            gap: 8px;
        }

        .btn-action {
            padding: 6px 12px;

            font-size: 12px;
            font-weight: 600;
            text-decoration: none;

            border-radius: 8px;
        }

        .btn-edit {
            color: var(--primary);
            background: #eef2ff;
        }

        .btn-delete {
            color: var(--danger);
            background: #fee2e2;
        }

        .empty-state {
            padding: 40px;
            color: var(--muted);
            text-align: center;
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/" class="back-arrow">←</a>

<div class="container-box">
    <div class="card">

        <div class="card-header">
            <span>Product Management</span>

            <a href="${pageContext.request.contextPath}/product/add"
               class="add-btn">
                + Add Product
            </a>
        </div>

        <c:if test="${empty products}">
            <div class="empty-state">
                No products found
            </div>
        </c:if>

        <c:if test="${not empty products}">
            <table>

                <thead>
                <tr>
                    <th>Product</th>
                    <th>Category</th>
                    <th>Brand</th>
                    <th>Model</th>
                    <th>Unit</th>
                    <th>SKU</th>
                    <th>Status</th>
                    <th style="width: 180px;">Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="product" items="${products}">
                    <tr>

                        <td>
                            <strong>${product.productName}</strong>
                        </td>

                        <td>${product.category}</td>

                        <td>${product.brand}</td>

                        <td>${product.model}</td>

                        <td>${product.unit}</td>

                        <td>${product.identifier}</td>

                        <td>
                            <div class="toggle-switch ${product.status ? 'active' : ''}"
                                 onclick="toggleProductStatus('${product.identifier}', this)">
                            </div>
                        </td>

                        <td>
                            <div class="actions">

                                <a class="btn-action btn-edit"
                                   href="${pageContext.request.contextPath}/product/get/${product.identifier}">
                                    Edit
                                </a>

                                <a class="btn-action btn-delete"
                                   href="${pageContext.request.contextPath}/product/delete/${product.identifier}"
                                   onclick="return confirm('Delete this product?')">
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

<script>
    function toggleProductStatus(identifier, el) {

        el.classList.toggle("active");

        fetch('${pageContext.request.contextPath}/product/toggle-status', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'identifier=' + encodeURIComponent(identifier)
        });
    }
</script>

</body>
</html>