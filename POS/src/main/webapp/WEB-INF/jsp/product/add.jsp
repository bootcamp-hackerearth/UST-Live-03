<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Product</title>

    <style>
        body {
            background: #f6f7f9;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            margin: 0;
        }

        /* ===== TOP BAR ===== */
        .topbar {
            height: 56px;
            background-color: #020617;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0 20px;
        }

        .top-title {
            color: #e5e7eb;
            font-weight: 600;
        }

        .logout-btn {
            background: #dc2626;
            color: white;
            border: none;
            padding: 7px 16px;
            border-radius: 6px;
            font-weight: 600;
            cursor: pointer;
        }

        /* ===== CARD ===== */
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

        /* multi-select must be taller */
        select[multiple] {
            height: 110px;
            padding: 6px;
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

<!-- TOP BAR -->
<div class="topbar">
    <div class="top-title">POS Application</div>
    <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<div class="card">

    <!-- BACK BUTTON -->
    <a href="${pageContext.request.contextPath}/product/list" class="back-btn">Back</a>

    <h2>Add Product</h2>

    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <form:form
        action="${pageContext.request.contextPath}/product/add"
        method="post"
        modelAttribute="productDto">

        <!-- SKU CODE -->
        <label>SKU Code</label>
        <form:input path="identifier" required="true"/>

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
        <form:input path="name" type="text" required="true"/>

        <button type="submit">Add Product</button>

    </form:form>

</div>

</body>
</html>