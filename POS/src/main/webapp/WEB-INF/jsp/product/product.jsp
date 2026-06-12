<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Product</title>

    <style>
        body {
            background: #f6f7f9;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            margin: 0;
        }

        .card {
            width: 420px;
            margin: 60px auto;
            background: #ffffff;
            padding: 26px;
            border-radius: 12px;
            position: relative;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        }

        .back-btn {
            position: absolute;
            top: 18px;
            left: 18px;
            background: #eef0f3;
            padding: 6px 14px;
            border-radius: 6px;
            text-decoration: none;
            font-weight: 600;
            color: #374151;
        }

        h2 {
            text-align: center;
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-top: 14px;
            font-weight: 600;
        }

        /* Base styling for inputs & selects */
        input,
        select {
            width: 100%;
            height: 38px;
            padding: 9px 11px;
            margin-top: 6px;
            border-radius: 6px;
            border: 1px solid #d1d5db;
            font-size: 14px;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            box-sizing: border-box;
        }

        /*  multi-select must be taller */
        select[multiple] {
            height: 110px;
            padding: 6px;
        }

        input[readonly] {
            background: #f1f5f9;
        }

        button {
            margin-top: 22px;
            width: 100%;
            background: #2563eb;
            color: white;
            border: none;
            padding: 10px;
            border-radius: 20px;
            font-weight: 600;
            cursor: pointer;
        }

        .error {
            color: #dc2626;
            text-align: center;
            font-weight: 600;
        }

        .hint {
            font-size: 12px;
            color: #6b7280;
            margin-top: 4px;
        }
    </style>
</head>

<body>

<div class="card">

    <!-- Back Button -->
    <a href="${pageContext.request.contextPath}/product/list" class="back-btn">Back</a>

    <h2>Edit Product</h2>

    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <form:form
        action="${pageContext.request.contextPath}/product/update"
        method="post"
        modelAttribute="product">

        <form:hidden path="id"/>

        <!-- Sku Code -->
        <label>Sku Code</label>
        <form:input path="identifier" readonly="true"/>

        <!-- Category Multi-Select -->
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

        <!-- Product Name -->
        <label>Product Name</label>
        <form:input path="name" required="true"/>

        <button type="submit">Update Product</button>

    </form:form>

</div>

</body>
</html>