<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Stock</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            font-family: "Segoe UI", Tahoma, Arial, sans-serif;
            background: linear-gradient(120deg, #e0eafc, #cfdef3);
            padding: 30px;
            margin: 0;
        }

        .container {
            width: 420px;
            margin: auto;
            background: #ffffff;
            padding: 28px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.12);
        }

        h2 {
            text-align: center;
            margin-bottom: 22px;
            color: #2c3e50;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            display: block;
            font-weight: 600;
            margin-bottom: 6px;
            color: #333;
        }

        input, select {
            width: 100%;
            padding: 10px;
            border-radius: 6px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        .btn-group {
            margin-top: 24px;
            display: flex;
            justify-content: center;
            gap: 12px;
        }

        .btn {
            padding: 10px 18px;
            border-radius: 6px;
            border: none;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
        }

        .save-btn {
            background: #28a745;
            color: #ffffff;
        }

        .cancel-btn {
            background-color: #6c757d;
            color: #ffffff;
            text-decoration: none;
        }

        .error-msg {
            background-color: #fdecea;
            color: #b02a37;
            border: 1px solid #f5c2c7;
            padding: 10px;
            border-radius: 6px;
            text-align: center;
            margin-bottom: 18px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Add Stock</h2>

    <c:if test="${not empty message}">
        <div class="error-msg">${message}</div>
    </c:if>

    <form:form method="post"
               action="${pageContext.request.contextPath}/stock/add"
               modelAttribute="stockDto">

        <div class="form-group">
            <label for="identifier">Identifier</label>
            <form:input path="identifier" id="identifier" required="true"/>
        </div>

         <div class="form-group">
           <label for="productname">Product Name</label>
           <form:select path="productname" id="productname" required="true">
           <form:option value="" label="-- Select productname --"/>
            <c:forEach var="wh" items="${products}">
            <form:option value="${wh.name}">
             ${wh.name}
             </form:option>
             </c:forEach>
             </form:select>
           </div>

          <div class="form-group">
            <label for="warehouse">Warehouse</label>
            <form:select path="warehouse" id="warehouse" required="true">
                <form:option value="" label="-- Select Warehouse --"/>
                <c:forEach var="wh" items="${warehouses}">
                    <form:option value="${wh.identifier}">
                        ${wh.identifier}
                    </form:option>
                </c:forEach>
            </form:select>
        </div>

        <div class="form-group">
            <label for="quantity">Quantity</label>
            <form:input path="quantity" id="quantity"  type="number" min="1" required="true"/>
        </div>

        <div class="form-group">
            <label for="price">Price</label>
            <form:input path="price" id="price" type="number" step="0.01" min="0.01" required="true"/>
        </div>

    <div class="form-group">
    <label for="status">Status</label>
    <form:select path="status" id="status" required="true">
        <form:option value="" label="-- Select Status --"/>
        <form:option value="true">Available</form:option>
        <form:option value="false">Out of Stock</form:option>
    </form:select>
    </div>

     <div class="btn-group">
            <button type="submit" class="btn save-btn">Save</button>
            <a href="${pageContext.request.contextPath}/stock/list"
               class="btn cancel-btn">Cancel</a>
        </div>

    </form:form>
</div>

</body>
</html>