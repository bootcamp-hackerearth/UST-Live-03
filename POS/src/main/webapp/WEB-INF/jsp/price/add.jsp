<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Price</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            background: #ffffff;
            width: 480px;
            padding: 36px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        h2 {
            text-align: center;
            margin-bottom: 28px;
            color: #1e293b;
            font-size: 22px;
            font-weight: 700;
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 13px;
            font-weight: 600;
            color: #334155;
        }

        input,
        select {
            width: 100%;
            padding: 12px;
            border-radius: 6px;
            border: 1px solid #cbd5e1;
            font-size: 14px;
        }

        input:focus,
        select:focus {
            outline: none;
            border-color: #1e293b;
            box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.15);
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            margin-top: 16px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            background-color: #1e293b;
            color: #ffffff;
        }

        .btn-submit:hover {
            background-color: #0f172a;
        }

        .back-center {
            margin-top: 20px;
            text-align: center;
        }

        .back-center a {
            text-decoration: none;
            color: #1e293b;
            font-size: 14px;
            font-weight: 600;
            padding: 8px 18px;
            border-radius: 6px;
            background-color: #e2e8f0;
        }

        .message {
            margin-bottom: 16px;
            padding: 10px;
            border-radius: 6px;
            background-color: #e0f2fe;
            color: #0369a1;
            text-align: center;
            font-size: 13px;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Add Price</h2>

    <c:if test="${not empty message}">
        <div class="message">
            ${message}
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/price/add">

        <div class="form-group">
            <label>Product</label>
            <select name="product" required>
                <option value="">-- Select Product --</option>

                <c:forEach items="${products}" var="prod">
                    <option
                        value="${prod.name}"
                        <c:if test="${prod.name eq priceDto.product}">
                            selected
                        </c:if>
                    >
                        ${prod.name}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Type</label>
            <select name="type" required>
                <option value="">-- Select Price Type --</option>

                <option value="COST_PRICE"
                    ${priceDto.type == 'COST_PRICE' ? 'selected' : ''}>
                    Cost Price (CP)
                </option>

                <option value="SELLING_PRICE"
                    ${priceDto.type == 'SELLING_PRICE' ? 'selected' : ''}>
                    Selling Price (SP)
                </option>

                <option value="MRP"
                    ${priceDto.type == 'MRP' ? 'selected' : ''}>
                    MRP
                </option>

                <option value="DISCOUNT"
                    ${priceDto.type == 'DISCOUNT' ? 'selected' : ''}>
                    Discount
                </option>
            </select>
        </div>

        <div class="form-group">
            <label>Amount</label>
            <input
                type="number"
                name="amount"
                value="${priceDto.amount}"
                placeholder="Enter amount"
                required
            />
        </div>

        <div class="form-group">
            <label>Currency</label>
            <input
                type="text"
                name="currency"
                value="${priceDto.currency}"
                placeholder="e.g. INR, USD"
                required
            />
        </div>

        <button type="submit" class="btn-submit">
            Save Price
        </button>

        <div class="back-center">
            <a href="${pageContext.request.contextPath}/price/list">
                Back
            </a>
        </div>

    </form>
</div>

</body>
</html>