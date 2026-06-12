<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Price</title>
    <style>
        body {
            margin: 0;
            font-family: 'Poppins', 'Segoe UI', Arial, sans-serif;
            background: #fff8f0;
            min-height: 100vh;
            padding: 40px;
        }

        .container {
            width: 75%;
            margin: auto;
            background: #efe3d9;
            padding: 36px;
            border-radius: 16px;
            box-shadow: 0 14px 35px rgba(0, 0, 0, 0.15);
        }

        h2 {
            text-align: center;
            margin-bottom: 28px;
            color: #4a2e2b;
            font-size: 24px;
            font-weight: 700;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 14px;
            font-weight: 600;
            color: #4a2e2b;
        }

        input, select {
            width: 100%;
            padding: 12px 14px;
            border-radius: 10px;
            border: 1px solid #d6c2b8;
            font-size: 14px;
            background: #fff8f0;
        }

        input[readonly] {
            background-color: #f6ede7;
            color: #6b4a46;
        }

        .btn-group {
            margin-top: 26px;
            display: flex;
            justify-content: center;
            gap: 14px;
        }

        .btn {
            padding: 12px 22px;
            border-radius: 12px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            color: #fff8f0;
            background-color: #6b4a46;
        }

        .btn:hover {
            background-color: #543835;
        }

        .back-btn {
            background-color: #8b5e59;
        }

        .back-btn:hover {
            background-color: #6f4844;
        }

        .message {
            color: #9a3d35;
            text-align: center;
            margin-bottom: 16px;
            font-weight: 600;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Update Price</h2>
    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>
    <form action="${pageContext.request.contextPath}/price/update" method="post">
        <div class="form-group">
            <label>ID</label>
            <input type="text" name="id" value="${price.id}" readonly />
        </div>
        <div class="form-group">
            <label>Identifier</label>
            <input type="text" name="identifier" value="${price.identifier}" readonly />
        </div>
        <div class="form-group">
            <label>Product</label>
            <select name="product" required>
                <option value="">-- Select Product --</option>
                <c:forEach items="${products}" var="prod">
                    <option value="${prod.identifier}"
                        <c:if test="${prod.identifier eq price.product}">
                            selected
                        </c:if>>
                        ${prod.identifier}
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label>Amount</label>
            <input type="number"
                name="amount"
                value="${price.amount}"
                required
            />
        </div>
        <div class="form-group">
            <label>Type</label>
            <select name="type" required>
                <option value="">-- Select Price Type --</option>
                <option value="COST_PRICE" ${price.type == 'COST_PRICE' ? 'selected' : ''}>
                    Cost Price (CP)
                </option>
                <option value="SELLING_PRICE" ${price.type == 'SELLING_PRICE' ? 'selected' : ''}>
                    Selling Price (SP)
                </option>
                <option value="MRP" ${price.type == 'MRP' ? 'selected' : ''}>
                    MRP
                </option>
                <option value="DISCOUNT" ${price.type == 'DISCOUNT' ? 'selected' : ''}>
                    Discount
                </option>
            </select>
        </div>
        <div class="form-group">
            <label>Currency</label>
            <input type="text"
                name="currency"
                value="${price.currency}"
                required
            />
        </div>
        <div class="btn-group">
            <button type="submit" class="btn">Update Price</button>
            <a href="${pageContext.request.contextPath}/price/list"
                class="btn back-btn">Back
            </a>
        </div>
    </form>
</div>
</body>
</html>