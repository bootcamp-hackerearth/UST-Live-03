<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Stock</title>
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
            margin-bottom: 26px;
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

        input:focus,
        select:focus {
            outline: none;
            border-color: #6b4a46;
            box-shadow: 0 0 0 3px rgba(107, 74, 70, 0.2);
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
            text-decoration: none;
            transition: 0.2s ease;
        }

        .btn:hover {
            background-color: #543835;
        }

        .back-btn {
            background-color: #8b5e59;
            color: #fff8f0;
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
    <h2>Update Stock</h2>
    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>
    <form action="${pageContext.request.contextPath}/stock/update" method="post">
        <div class="form-group">
            <label>ID</label>
            <input type="text" name="id" value="${stock.id}" readonly />
        </div>
        <div class="form-group">
            <label>Identifier</label>
            <input type="text" name="identifier" value="${stock.identifier}" readonly />
        </div>
        <div class="form-group">
            <label>Product</label>
            <select name="product" required>
                <option value="">-- Select Product --</option>
                <c:forEach items="${products}" var="prod">
                    <option value="${prod.identifier}"
                        <c:if test="${prod.identifier eq stock.product}">
                            selected
                        </c:if>>
                        ${prod.name}
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
                        <c:if test="${wh.identifier eq stock.warehouse}">
                            selected
                        </c:if>>
                        ${wh.identifier}
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label>Quantity</label>
            <input type="number" name="quantity" value="${stock.quantity}" required />
        </div>
        <div class="form-group">
            <label>Status</label>
            <select name="stockStatus" required>
                <option value="true"
                    <c:if test="${stock.stockStatus}">selected</c:if>>
                    Active
                </option>
                <option value="false"
                    <c:if test="${not stock.stockStatus}">selected</c:if>>
                    Not Active
                </option>
            </select>
        </div>
        <div class="form-group">
            <label>Expiry Date</label>
            <input type="datetime-local"
                name="expiryDate"
                value="${stock.expiryDate}"
                required
            />
        </div>
        <div class="btn-group">
            <button type="submit" class="btn">Update Stock</button>
            <a href="${pageContext.request.contextPath}/stock/list"
               class="btn back-btn">Back
            </a>
        </div>
    </form>
</div>
</body>
</html>