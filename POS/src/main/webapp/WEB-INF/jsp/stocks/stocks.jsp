<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Stock</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #ffffff;
        }

        /* ===== CARD ===== */
        .card {
            width: 360px;
            background: #ffffff;
            margin: 40px auto;
            padding: 22px;
            border-radius: 14px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            position: relative;
        }

        .app-title {
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #14b8a6;
            margin-bottom: 4px;
        }

        .back-btn {
            position: absolute;
            top: 12px;
            left: 12px;
            padding: 5px 12px;
            background: #ffffff;
            border: 1px solid teal;
            color: teal;
            text-decoration: none;
            border-radius: 16px;
            font-size: 12px;
            font-weight: 600;
        }

        h2 {
            text-align: center;
            margin-bottom: 12px;
            font-size: 20px;
        }

        /* ===== FORM ===== */
        label {
            display: block;
            margin-top: 10px;
            font-size: 12px;
            font-weight: 600;
            color: #475569;
        }

        input,
        select {
            display: block;
            width: 100%;
            box-sizing: border-box;
            padding: 8px 10px;
            margin-top: 4px;
            border: 1px solid #d1d5db;
            border-radius: 18px;
            font-size: 13px;
            height: 34px;
        }

        input[readonly] {
            background: #f1f5f9;
        }

        /* REMOVE DROPDOWN ARROW */
        select:disabled {
            background: #f1f5f9;
            appearance: none;
            -webkit-appearance: none;
            -moz-appearance: none;
        }

        button {
            margin-top: 16px;
            width: 100%;
            height: 34px;
            background: teal;
            color: white;
            border-radius: 18px;
            border: none;
            font-weight: 600;
            font-size: 13px;
            cursor: pointer;
        }

        .error-message {
            text-align: center;
            color: #ef4444;
            font-size: 12px;
            font-weight: 600;
            margin-bottom: 10px;
        }
    </style>
</head>

<body>

<div class="card">

    <div class="app-title">POS Application</div>

    <a href="${pageContext.request.contextPath}/stocks/list" class="back-btn">
        Back
    </a>

    <h2>Edit Stock</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/stocks/update"
               method="post"
               modelAttribute="stocks">

        <form:hidden path="id"/>

        <!-- SKU CODE -->
        <label>Sku Code</label>

        <form:select path="identifier" disabled="true">

            <c:forEach var="product" items="${products}">
                <form:option value="${product.identifier}">
                    ${product.identifier}
                </form:option>
            </c:forEach>

        </form:select>

        <form:hidden path="identifier"/>

        <!-- AVAILABLE STOCK -->
        <label>Available Stock</label>

        <form:input
            path="availableStock"
            type="number"
            min="0"
            required="true"/>

        <!-- INCOMING STOCK -->
        <label>Incoming Stock</label>

        <form:input
            path="incomingStock"
            type="number"
            min="0"/>

        <!-- OUTGOING STOCK -->
        <label>Outgoing Stock</label>

        <form:input
            path="outgoingStock"
            type="number"
            min="0"/>

        <!-- PRODUCT STATUS -->
        <label>Product Status</label>

        <form:select path="productStatus" required="true">

            <form:option value="">
                -- Select Status --
            </form:option>

            <form:option value="AVAILABLE">Available</form:option>
            <form:option value="OUT_OF_STOCK">Out of Stock</form:option>
            <form:option value="LOW_STOCK">Low Stock</form:option>
            <form:option value="INCOMING">Incoming</form:option>
            <form:option value="BLOCKED">Blocked</form:option>
            <form:option value="DAMAGED">Damaged</form:option>

        </form:select>

        <!-- WAREHOUSE -->
        <label>Warehouse</label>

        <form:select path="wareHouse" required="true">

            <form:option value="">
                -- Select Warehouse --
            </form:option>

            <c:forEach var="wh" items="${warehouse}">
                <form:option value="${wh.identifier}">
                    ${wh.identifier}
                </form:option>
            </c:forEach>

        </form:select>

        <button type="submit">Update Stock</button>

    </form:form>

</div>

</body>
</html>