<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Price</title>

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
            --danger-bg: #fee2e2;
            --danger-border: #fca5a5;
            --radius: 16px;
            --shadow: 0 20px 40px rgba(2,6,23,0.08);
        }

        * {
            font-family: 'Inter', sans-serif;
            box-sizing: border-box;
        }

        body {
            background: var(--bg);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
            position: relative;
            color: var(--text);
        }

        .back-arrow {
            position: absolute;
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
            width: 100%;
            max-width: 520px;
        }

        .card {
            padding: 28px;
            border-radius: var(--radius);
            background: var(--glass);
            backdrop-filter: blur(16px);
            border: 1px solid var(--border);
            box-shadow: var(--shadow);
        }

        h2 {
            text-align: center;
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 20px;
        }

        label {
            font-size: 13px;
            color: var(--muted);
            margin-top: 12px;
            display: block;
        }

        .form-control {
            margin-top: 6px;
            border-radius: 10px;
            padding: 10px;
            border: 1px solid var(--border);
            font-size: 14px;
        }

        .form-control:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(37,99,235,0.15);
        }

        .btn-update {
            margin-top: 20px;
            width: 100%;
            padding: 11px;
            border-radius: 10px;
            border: none;
            background: var(--primary);
            color: white;
            font-weight: 600;
            transition: 0.2s;
        }

        .btn-update:hover {
            background: var(--primary-hover);
            transform: translateY(-1px);
        }

        .server-msg {
            text-align: center;
            padding: 10px;
            border-radius: 10px;
            margin-bottom: 14px;
            font-size: 13px;
            background: var(--danger-bg);
            color: var(--danger);
            border: 1px solid var(--danger-border);
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/price/list" class="back-arrow">←</a>

<div class="container-box">

    <div class="card">

        <h2>Edit Price</h2>

        <c:if test="${not empty message}">
            <div class="server-msg">${message}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/price/update" method="post">

            <input type="hidden" name="id" value="${price.id}" />

            <label>Product</label>

            <select name="productId" class="form-control" required>

                <c:forEach items="${products}" var="product">

                    <option value="${product.id}"
                        <c:if test="${product.id == price.productId}">
                            selected
                        </c:if>>

                        ${product.identifier} - ${product.productName}

                    </option>

                </c:forEach>

            </select>

            <label>MRP Price</label>

            <input type="number"
                   step="0.01"
                   min="0"
                   name="mrpPrice"
                   value="${price.mrpPrice}"
                   class="form-control"
                   required />

            <label>Selling Price</label>

            <input type="number"
                   step="0.01"
                   min="0"
                   name="sellingPrice"
                   value="${price.sellingPrice}"
                   class="form-control"
                   required />

            <label>Cost Price</label>

            <input type="number"
                   step="0.01"
                   min="0"
                   name="costPrice"
                   value="${price.costPrice}"
                   class="form-control"
                   required />

            <button type="submit" class="btn-update">
                Update Price
            </button>

        </form>

    </div>

</div>

</body>
</html>