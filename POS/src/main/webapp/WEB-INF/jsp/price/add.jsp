<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Price</title>

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

        .error {
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

    <a href="${pageContext.request.contextPath}/price/list" class="back-btn">
        Back
    </a>

    <h2>Add Price</h2>

    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/price/add"
               method="post"
               modelAttribute="priceDto">

        <!-- SKU CODE -->
        <label>Sku Code</label>

        <form:select path="identifier" required="true">

            <form:option value="">
                -- Select SkuCode --
            </form:option>

            <c:forEach items="${products}" var="product">

                <form:option value="${product.identifier}">
                    ${product.identifier}
                </form:option>

            </c:forEach>

        </form:select>

        <!-- MRP -->
        <label>MRP</label>

        <form:input
            path="mrp"
            type="number"
            step="0.01"
            required="true"/>

        <!-- SELLING PRICE -->
        <label>Selling Price</label>

        <form:input
            path="sellingPrice"
            type="number"
            step="0.01"
            required="true"/>

        <!-- COST PRICE -->
        <label>Cost Price</label>

        <form:input
            path="costPrice"
            type="number"
            step="0.01"
            required="true"/>

        <!-- EFFECTIVE FROM -->
        <label>Effective From</label>

        <form:input
            path="effectiveFrom"
            type="date"
            required="true"/>

        <button type="submit">Add Price</button>

    </form:form>

</div>

</body>
</html>