<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Price</title>

    <style>
        body { font-family: Arial; margin: 30px; }
        .form-container { width: 400px; }
        label { display: block; margin-top: 15px; font-weight: bold; }
        input, select { width: 100%; padding: 8px; margin-top: 5px; }
        .btn { margin-top: 20px; padding: 8px 14px; border: none; color: white; border-radius: 4px; }
        .btn-save { background: green; }
        .btn-back { background: gray; margin-left: 10px; }
    </style>
</head>

<body>

<h2>Add Price</h2>

<!-- MESSAGE -->
<c:if test="${not empty message}">
    <div>${message}</div>
</c:if>

<div class="form-container">

    <form action="${pageContext.request.contextPath}/price/add" method="post">

        <!-- PRODUCT IDENTIFIER DROPDOWN -->
        <label>Product</label>
        <select name="identifier" required>
            <option value="">-- Select Product --</option>
            <c:forEach items="${product}" var="p">
                <option value="${p.identifier}">
                    ${p.identifier}
                </option>
            </c:forEach>
        </select>

        <!-- COST PRICE -->
        <label>Cost Price</label>
        <input type="number" step="0.10" name="costPrice" required />

        <!-- SELLING PRICE -->
        <label>Selling Price</label>
        <input type="number" step="0.10" name="sellingPrice" required />

         <!-- MRP -->
                <label>MRP</label>
                <input type="number" step="0.10" name="mrp" required />

        <button type="submit" class="btn btn-save">Save</button>

        <a href="${pageContext.request.contextPath}/price/list"
           class="btn btn-back">Back</a>
    </form>

</div>

</body>
</html>
