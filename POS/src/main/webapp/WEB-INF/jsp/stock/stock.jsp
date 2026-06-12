<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Stock</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card-container {
            position: relative;
            width: 500px;
            background: #fff;
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0,0,0,.25);
        }

        /* ✅ Back Button */
        .back-icon {
            position: absolute;
            top: 16px;
            left: 16px;
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            color: #4b6cb7;
            text-decoration: none;
            font-weight: 600;
            background: rgba(75,108,183,0.08);
            border-radius: 50%;
            transition: all 0.25s ease;
        }

        .back-icon:hover {
            background: #4b6cb7;
            color: #ffffff;
            transform: translateX(-3px) scale(1.05);
        }

        h2 {
            text-align: center;
            color: #4b6cb7;
            margin-bottom: 20px;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-weight: 500;
        }

        input, select {
            width: 100%;
            padding: 10px;
            border-radius: 8px;
            border: 1px solid #ccc;
            font-family: 'Poppins', sans-serif;
            font-size: 14px;
        }

        .btn-group {
            margin-top: 20px;
            display: flex;
            justify-content: center;
            gap: 12px;
        }

        .btn {
            padding: 10px 18px;
            border-radius: 10px;
            border: none;
            cursor: pointer;
            font-weight: 600;
            font-size: 14px;
        }

        .btn-save {
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
        }

        .btn-save:hover {
            transform: scale(1.05);
        }

        .error-message {
            color: red;
            text-align: center;
            margin-bottom: 12px;
            font-size: 14px;
        }
    </style>
</head>

<body>

<div class="card-container">

    <!-- ✅ TOP LEFT BACK BUTTON -->
    <a href="${pageContext.request.contextPath}/stock/list" class="back-icon">←</a>

    <h2>Edit Stock</h2>

    <!-- ✅ Error Message -->
    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form method="post"
          action="${pageContext.request.contextPath}/stock/update">

        <!-- ✅ Hidden Identifier -->
        <input type="hidden" name="identifier" value="${stock.identifier}" />

        <!-- ✅ Stock Name -->
        <div class="form-group">
            <label>Stock</label>
            <input type="text" value="${stock.identifier}" readonly />
        </div>

        <!-- ✅ Product Name Dropdown -->
        <div class="form-group">
            <label>Product</label>
            <select name="productName" required>
                <option value="">-- Select Product --</option>
                <c:forEach items="${product}" var="product">
                    <option value="${product.identifier}"
                        ${product.identifier == stock.productName ? 'selected' : ''}>
                        ${product.identifier}
                    </option>
                </c:forEach>
            </select>
        </div>

        <!-- ✅ Warehouse Name Dropdown -->
        <div class="form-group">
            <label>Warehouse</label>
            <select name="warehouseName" required>
                <option value="">-- Select Warehouse --</option>
                <c:forEach items="${warehouse}" var="warehouse">
                    <option value="${warehouse.identifier}"
                        ${warehouse.identifier == stock.warehouseName ? 'selected' : ''}>
                        ${warehouse.identifier}
                    </option>
                </c:forEach>
            </select>
        </div>

        <!-- ✅ Quantity -->
        <div class="form-group">
            <label>Quantity</label>
            <input type="number"
                   name="noOfProducts"
                   value="${stock.noOfProducts}"
                   required />
        </div>

        <!-- ✅ Status -->
        <div class="form-group">
            <label>Status</label>
            <select name="status" required>
                <option value="IN_STOCK"
                    ${stock.status == 'IN_STOCK' ? 'selected' : ''}>
                    In Stock
                </option>
                <option value="OUT_OF_STOCK"
                    ${stock.status == 'OUT_OF_STOCK' ? 'selected' : ''}>
                    Out of Stock
                </option>
            </select>
        </div>

        <!-- ✅ Save Button -->
        <div class="btn-group">
            <button type="submit" class="btn btn-save">Update</button>
        </div>

    </form>

</div>

</body>
</html>
