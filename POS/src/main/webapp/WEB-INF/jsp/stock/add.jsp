<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Stock</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        :root {
            --primary: #2563eb;
            --primary-hover: #1e40af;

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
            position: relative;

            display: flex;
            align-items: center;
            justify-content: center;

            min-height: 100vh;
            padding: 20px;

            background: var(--bg);
        }

        .back-arrow {
            position: absolute;
            top: 20px;
            left: 20px;

            display: flex;
            align-items: center;
            justify-content: center;

            width: 42px;
            height: 42px;

            font-size: 18px;
            color: var(--text);
            text-decoration: none;

            background: var(--glass);
            border: 1px solid var(--border);
            border-radius: 50%;
            backdrop-filter: blur(10px);

            box-shadow: var(--shadow);
            transition: 0.2s;
        }

        .back-arrow:hover {
            color: var(--primary);
            background: #eef2ff;
        }

        .card {
            width: 460px;
            padding: 26px;

            background: var(--glass);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
        }

        h5 {
            margin-bottom: 18px;

            font-weight: 600;
            text-align: center;
            color: var(--text);
        }

        label {
            margin-top: 12px;

            font-size: 13px;
            color: var(--muted);
        }

        .form-control,
        .form-select {
            margin-top: 6px;
            padding: 10px;

            border: 1px solid var(--border);
            border-radius: 10px;
        }

        .form-control:focus,
        .form-select:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(40, 167, 69, 0.15);
        }

        .btn-save {
            width: 100%;
            margin-top: 22px;
            padding: 11px;

            font-weight: 600;
            color: #fff;

            background: var(--primary);
            border: none;
            border-radius: 10px;

            transition: 0.2s;
        }

        .btn-save:hover {
            background: var(--primary-hover);
        }

        .server-msg {
            margin-bottom: 14px;
            padding: 10px;

            font-size: 13px;
            text-align: center;

            border-radius: 10px;
        }

        .server-msg.error {
            color: var(--danger);
            background: var(--danger-bg);
            border: 1px solid var(--danger-border);
        }

        .server-msg.success {
            color: var(--success);
            background: var(--success-bg);
            border: 1px solid var(--success-border);
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/stock/list"
   class="back-arrow">
    ←
</a>

<div class="card">

    <h5>Add Stock</h5>

    <c:if test="${not empty message}">
        <div class="server-msg ${messageType}">
            ${message}
        </div>
    </c:if>

    <form:form modelAttribute="stockDto"
               method="post"
               action="${pageContext.request.contextPath}/stock/add">

        <label>Product</label>
        <form:select path="productId"
                     cssClass="form-select"
                     required="true">

            <form:option value=""
                         label="-- Select Product --" />

            <c:forEach items="${products}" var="product">
                <form:option value="${product.id}">
                    ${product.productName}
                </form:option>
            </c:forEach>

        </form:select>

        <label>Warehouse</label>
        <form:select path="warehouseId"
                     cssClass="form-select"
                     required="true">

            <form:option value=""
                         label="-- Select Warehouse --" />

            <c:forEach items="${warehouses}" var="warehouse">
                <form:option value="${warehouse.id}">
                    ${warehouse.name}
                </form:option>
            </c:forEach>

        </form:select>

        <label>Quantity</label>
        <form:input path="quantity"
                    type="number"
                    min="0"
                    cssClass="form-control"
                    required="true" />

        <button type="submit"
                class="btn-save">
            Save Stock
        </button>

    </form:form>

</div>

</body>
</html>