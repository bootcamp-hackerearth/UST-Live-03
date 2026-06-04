<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Product</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #ffffff;
        }

        .card {
            width: 360px;
            margin: 40px auto;
            background: #ffffff;
            padding: 22px;
            border-radius: 14px;
            position: relative;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
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
            border-radius: 16px;
            text-decoration: none;
            font-size: 12px;
            font-weight: 600;
            color: teal;
        }

        h2 {
            text-align: center;
            margin-bottom: 12px;
            font-size: 20px;
        }

        label {
            display: block;
            margin-top: 12px;
            font-weight: 600;
            font-size: 12px;
            color: #475569;
        }

        input, select {
            display: block;
            width: 100%;
            box-sizing: border-box;
            height: 34px;
            padding: 8px 10px;
            margin-top: 4px;
            border-radius: 18px;
            border: 1px solid #d1d5db;
            font-size: 13px;
        }

        select[multiple] {
            height: 90px;
            border-radius: 12px;
            padding: 6px;
        }

        input[readonly] {
            background: #f1f5f9;
        }

        button {
            margin-top: 16px;
            width: 100%;
            height: 34px;
            padding: 8px 10px;
            background: teal;
            color: white;
            border: none;
            border-radius: 18px;
            font-weight: 600;
            font-size: 13px;
        }

        .error {
            color: #dc2626;
            text-align: center;
            font-size: 12px;
            font-weight: 600;
        }

        .hint {
            font-size: 11px;
            color: #6b7280;
            margin-top: 3px;
        }
    </style>
</head>

<body>

<div class="card">

    <div class="app-title">POS Application</div>

    <a href="${pageContext.request.contextPath}/product/list" class="back-btn">Back</a>

    <h2>Edit Product</h2>

    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>


    <form:form action="${pageContext.request.contextPath}/product/update"
               method="post"
               modelAttribute="product">

        <form:hidden path="id"/>

        <!-- Product Sku -->
        <label>Product SKU</label>
        <form:input path="identifier" readonly="true"/>

        <!-- Category -->
        <label>Category</label>
        <form:select path="category" multiple="true">
            <c:forEach var="cat" items="${categories}">
                <form:option value="${cat.identifier}">
                    ${cat.identifier}
                </form:option>
            </c:forEach>
        </form:select>
        <div class="hint">Hold Ctrl / Cmd to select multiple</div>

        <!-- Brand -->
        <label>Brand</label>
        <form:select path="brand">
            <c:forEach var="bran" items="${brand}">
                <form:option value="${bran.identifier}">
                    ${bran.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <label>Unit</label>
        <form:select path="unit">
            <c:forEach var="uni" items="${unit}">
                <form:option value="${uni.identifier}">
                    ${uni.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <label>Model</label>
        <form:select path="model">
            <c:forEach var="mode" items="${model}">
                <form:option value="${mode.identifier}">
                    ${mode.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <label>Product Name</label>
        <form:input path="name"/>
        <button type="submit">Update Product</button>
    </form:form>
</div>

</body>
</html>