<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Price</title>

    <style>
        body { margin:0; font-family:"Segoe UI", Arial; background:#f6f7f9; }

        .topbar { height:56px; background:#020617; display:flex;
            justify-content:space-between; align-items:center; padding:0 20px; }

        .top-title { color:#e5e7eb; font-weight:600; }

        .logout-btn { background:#dc2626; color:white; border:none;
            padding:7px 16px; border-radius:6px; }

        .card {
            width:420px; margin:60px auto; background:#fff;
            padding:26px; border-radius:12px;
            box-shadow:0 8px 20px rgba(0,0,0,0.08); position:relative;
        }

        .back-btn {
            position:absolute; top:18px; left:18px;
            background:#eef0f3; padding:6px 14px;
            text-decoration:none; border-radius:6px;
        }

        label { margin-top:14px; display:block; font-weight:600; }
       input, select {
           width:100%;
           padding:9px;
           margin-top:6px;
           border-radius:6px;
           border: 1px solid #ccc;
           box-sizing: border-box;
       }

        button {
            margin-top:22px; width:100%; padding:10px;
            background:#2563eb; color:white; border:none;
            border-radius:20px; font-weight:600;
        }

        .error { color:#dc2626; text-align:center; font-weight:600; }
    </style>
</head>

<body>

<div class="topbar">
    <div class="top-title">POS Application</div>
    <form action="${pageContext.request.contextPath}/logout" method="post">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<div class="card">

    <a href="${pageContext.request.contextPath}/price/list" class="back-btn">Back</a>

    <h2 style="text-align:center;">Add Price</h2>

    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/price/add"
               method="post"
               modelAttribute="priceDto">

        <label>Product Name</label>
        <form:select path="identifier" required="true">
            <form:option value="">-- Select Product --</form:option>

            <c:forEach items="${products}" var="product">
                <form:option value="${product.identifier}">
                    ${product.identifier}
                </form:option>
            </c:forEach>

        </form:select>

        <label>MRP</label>
        <form:input path="mrp" type="number" step="0.01" required="true"/>

        <label>Selling Price</label>
        <form:input path="sellingPrice" type="number" step="0.01" required="true"/>

        <label>Effective From</label>
        <form:input path="effectiveFrom" type="date" required="true"/>

        <button type="submit">Add Price</button>
    </form:form>

</div>

</body>
</html>