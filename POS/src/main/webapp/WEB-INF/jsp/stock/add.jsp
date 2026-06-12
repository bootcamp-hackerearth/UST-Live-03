<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>POS Management | Add Stock</title>

<style>
    body {
        margin: 0;
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        background: #F4F5F7;
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .container {
        width: 100%;
        max-width: 440px;
        background: #FFFFFF;
        border-radius: 16px;
        box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        overflow: hidden;
    }

    .brand-header {
        background: #0B3C5D;
        padding: 26px;
        color: white;
        text-align: center;
    }

    .brand-header h1 {
        margin: 0;
        font-size: 22px;
        font-weight: 600;
        letter-spacing: 0.6px;
    }

    .form-body {
        padding: 32px 38px;
    }

    h2 {
        text-align: center;
        color: #1F2937;
        margin-bottom: 26px;
        font-size: 18px;
        font-weight: 600;
    }

    .form-group {
        margin-bottom: 22px;
    }

    label {
        display: block;
        margin-bottom: 8px;
        font-weight: 600;
        color: #4B5563;
        font-size: 13px;
    }

    input, select {
        width: 100%;
        padding: 12px 14px;
        border-radius: 8px;
        border: 1px solid #E5E7EB;
        font-size: 14px;
        font-family: inherit;
        box-sizing: border-box;
        background: #FFFFFF;
        transition: border-color 0.2s, box-shadow 0.2s;
    }

    input:focus, select:focus {
        outline: none;
        border-color: #0B3C5D;
        box-shadow: 0 0 0 2px rgba(11,60,93,0.08);
    }

    select {
        appearance: none;
        background-image:
            linear-gradient(45deg, transparent 50%, #6B7280 50%),
            linear-gradient(135deg, #6B7280 50%, transparent 50%);
        background-position:
            calc(100% - 18px) center,
            calc(100% - 12px) center;
        background-size: 6px 6px;
        background-repeat: no-repeat;
        padding-right: 36px;
    }

    .btn-submit {
        width: 100%;
        padding: 14px;
        border-radius: 10px;
        border: none;
        background: #0B3C5D;
        color: white;
        cursor: pointer;
        font-weight: 700;
        font-size: 15px;
        margin-top: 10px;
        transition: transform 0.15s, box-shadow 0.15s;
    }

    .btn-submit:hover {
        transform: translateY(-1px);
        box-shadow: 0 6px 14px rgba(11,60,93,0.25);
    }

    .back-link {
        display: block;
        margin-top: 22px;
        text-align: center;
        color: #6B7280;
        text-decoration: none;
        font-size: 14px;
        font-weight: 600;
    }

    .back-link:hover {
        color: #0B3C5D;
        text-decoration: underline;
    }
</style>
</head>

<body>

<div class="container">
    <div class="brand-header">
        <h1>POS Management</h1>
    </div>

    <div class="form-body">
        <h2>Create New Stock</h2>

        <form action="${pageContext.request.contextPath}/stock/add" method="post">
            <div class="form-group">
                <label>Product Name</label>
                <select name="identifier" required>
                    <option value="">-- Select Product --</option>
                    <c:forEach var="product" items="${products}">
                        <option value="${product.identifier}">
                            ${product.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label>Available Stock</label>
                <input type="number"
                       name="availableStock"
                       placeholder="Enter available quantity"
                       min="0"
                       required />
            </div>

            <div class="form-group">
                <label>Outgoing Stock</label>
                <input type="number"
                       name="outgoingStock"
                       placeholder="Enter outgoing quantity"
                       min="0"
                       required />
            </div>

            <div class="form-group">
                <label>Warehouse</label>
                <select name="warehouse" required>
                    <option value="">-- Select Warehouse --</option>
                    <c:forEach var="wh" items="${warehouses}">
                        <option value="${wh.identifier}">
                            ${wh.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label>Product Status</label>
                <select name="productStatus" required>
                    <option value="">-- Select Status --</option>
                    <option value="AVAILABLE">AVAILABLE</option>
                    <option value="LOW_STOCK">LOW STOCK</option>
                    <option value="OUT_OF_STOCK">OUT OF STOCK</option>
                </select>
            </div>

            <button type="submit" class="btn-submit">Save Stock</button>

            <a href="${pageContext.request.contextPath}/stock/list"
               class="back-link">
                ← Cancel and Return
            </a>
        </form>
    </div>
</div>

</body>
</html>