
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
            font-family: "Segoe UI", Arial, sans-serif;
            background: linear-gradient(135deg, #f4f7f6, #eef2ff);
        }

        :root {
            --primary-color: #4f46e5;
            --primary-hover: #4338ca;
        }

        .card {
            width: 450px;
            margin: 80px auto;
            background: #ffffff;
            border-radius: 14px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.08);
            padding: 25px;
        }

        h2 {
            text-align: center;
        }

        label {
            display: block;
            margin-top: 14px;
            font-weight: 600;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-top: 6px;
            border-radius: 8px;
            border: 1px solid #ccc;
        }

        select[multiple] {
            height: 100px;
        }

        .hint {
            font-size: 12px;
            color: gray;
        }

        .btn-submit {
            margin-top: 20px;
            width: 100%;
            padding: 12px;
            background: var(--primary-color);
            color: white;
            border: none;
            border-radius: 10px;
        }

        .error {
            color: red;
            text-align: center;
        }

        .back-btn {
            display: inline-block;
            margin-bottom: 10px;
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="card">

    <a href="${pageContext.request.contextPath}/product/list" class="back-btn">
        ← Back
    </a>

    <h2>Add Product</h2>

    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <form:form 
        action="${pageContext.request.contextPath}/product/add"
        method="post"
        modelAttribute="products">

        <label>SKU Code</label>
        <form:input path="identifier" required="true"/>

        <label>Category</label>
        <form:select path="categories" multiple="true">
            <c:forEach var="cat" items="${categories}">
                <form:option value="${cat.identifier}">
                    ${cat.identifier}
                </form:option>
            </c:forEach>
        </form:select>
        <div class="hint">Hold Ctrl / Cmd to select multiple</div>

        <label>Brand</label>
        <form:select path="brand" required="true">
            <form:option value="">-- Select Brand --</form:option>
            <c:forEach var="bran" items="${brand}">
                <form:option value="${bran.identifier}">
                    ${bran.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <label>Unit</label>
        <form:select path="unit" required="true">
            <form:option value="">-- Select Unit --</form:option>
            <c:forEach var="uni" items="${unit}">
                <form:option value="${uni.identifier}">
                    ${uni.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <label>Model</label>
        <form:select path="model" required="true">
            <form:option value="">-- Select Model --</form:option>
            <c:forEach var="mode" items="${model}">
                <form:option value="${mode.identifier}">
                    ${mode.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <label>Product Name</label>
        <form:input path="name" required="true"/>

        <input type="submit" value="Add Product" class="btn-submit"/>

    </form:form>

</div>

</body>
</html>
