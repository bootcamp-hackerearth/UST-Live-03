<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Product</title>

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

        select[multiple] {
            height: 90px;
            border-radius: 12px;
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

        .hint {
            font-size: 11px;
            color: #64748b;
            margin-top: 5px;
        }

    </style>

</head>

<body>

<div class="card">

    <div class="app-title">POS Application</div>

    <a href="${pageContext.request.contextPath}/product/list" class="back-btn">
        Back
    </a>

    <h2>Add Product</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form:form
        action="${pageContext.request.contextPath}/product/add"
        method="post"
        modelAttribute="productDto">

        <!-- SKU CODE -->
        <label>SKU Code</label>
        <form:input path="identifier" required="true"/>

        <!-- Category -->
        <label>Category</label>
        <form:select path="category" multiple="true">
            <c:forEach var="cat" items="${categories}">
                <form:option value="${cat.identifier}">
                    ${cat.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <div class="hint">
            Hold <b>Ctrl</b> (Windows) or <b>Cmd</b> (Mac) to select multiple categories
        </div>

        <!-- Brand -->
        <label>Brand</label>

        <form:select path="brand" required="true">

            <form:option value="">
                -- Select Brand --
            </form:option>

            <c:forEach var="bran" items="${brand}">
                <form:option value="${bran.identifier}">
                    ${bran.identifier}
                </form:option>
            </c:forEach>

        </form:select>

        <!-- Unit -->
        <label>Unit</label>

        <form:select path="unit" required="true">

            <form:option value="">
                -- Select Unit --
            </form:option>

            <c:forEach var="uni" items="${unit}">
                <form:option value="${uni.identifier}">
                    ${uni.identifier}
                </form:option>
            </c:forEach>

        </form:select>

        <!-- Model -->
        <label>Model</label>

        <form:select path="model" required="true">

            <form:option value="">
                -- Select Model --
            </form:option>

            <c:forEach var="mode" items="${model}">
                <form:option value="${mode.identifier}">
                    ${mode.identifier}
                </form:option>
            </c:forEach>

        </form:select>

        <!-- Product Name -->
        <label>Product Name</label>

        <form:input path="name" type="text" required="true"/>

        <button type="submit">Add Product</button>

    </form:form>

</div>

</body>
</html>