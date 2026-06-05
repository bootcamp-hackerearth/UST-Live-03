<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Stock</title>
    <style>
        body {
            margin: 0;
            font-family: 'Poppins', 'Segoe UI', Arial, sans-serif;
            background: #fff8f0;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            background: #efe3d9;
            width: 440px;
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

        input,
        select {
            width: 100%;
            height: 46px;
            padding: 10px 14px;
            border-radius: 10px;
            border: 1px solid #d6c2b8;
            font-size: 14px;
            background: #fff8f0;
        }

        input:focus,
        select:focus {
            outline: none;
            border-color: #6b4a46;
            box-shadow: 0 0 0 3px rgba(107, 74, 70, 0.2);
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            margin-top: 18px;
            border-radius: 12px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            background-color: #6b4a46;
            color: #fff8f0;
            transition: 0.2s ease;
        }

        .btn-submit:hover {
            background-color: #543835;
        }

        .back-center {
            margin-top: 22px;
            text-align: center;
        }

        .back-center a {
            text-decoration: none;
            color: #fff8f0;
            font-size: 14px;
            font-weight: 600;
            padding: 10px 22px;
            border-radius: 12px;
            background-color: #8b5e59;
            transition: 0.2s ease;
        }

        .back-center a:hover {
            background-color: #6f4844;
        }

        .message {
            margin-bottom: 18px;
            padding: 12px;
            border-radius: 12px;
            background-color: #f6ede7;
            color: #9a3d35;
            text-align: center;
            font-size: 14px;
            font-weight: 600;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Add Stock</h2>
    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>
    <form method="post">
        <input type="hidden" name="id" value="${stockDto.id}" />
        <div class="form-group">
            <label>Product</label>
            <select name="product" required>
                <option value="">-- Select Product --</option>
                <c:forEach items="${products}" var="prod">
                    <option value="${prod.identifier}"
                        <c:if test="${prod.identifier eq stockDto.product}">
                            selected
                        </c:if>>
                        ${prod.identifier}
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label>Warehouse</label>
            <select name="warehouse" required>
                <option value="">-- Select Warehouse --</option>
                <c:forEach items="${warehouses}" var="wh">
                    <option value="${wh.identifier}"
                        <c:if test="${wh.identifier eq stockDto.warehouse}">
                            selected
                        </c:if>>
                        ${wh.identifier}
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label>Quantity</label>
            <input type="number" name="quantity"
                   value="${stockDto.quantity}"
                   placeholder="Enter quantity" required/>
        </div>
        <div class="form-group">
            <label>Status</label>
            <select name="stockStatus" required>
                <option value="">-- Select Status --</option>
                <option value="true"
                    <c:if test="${stockDto.stockStatus}">selected</c:if>>
                    Active
                </option>
                <option value="false"
                    <c:if test="${not stockDto.stockStatus}">selected</c:if>>
                    Not Active
                </option>
            </select>
        </div>
        <div class="form-group">
            <label>Expiry Date</label>
            <input type="datetime-local" name="expiryDate"
                   value="${stockDto.expiryDate}" required/>
        </div>
        <button type="submit" class="btn-submit">Submit</button>
        <div class="back-center">
            <a href="${pageContext.request.contextPath}/stock/list">Back</a>
        </div>
    </form>
</div>
</body>
</html>