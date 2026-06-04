<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Stock</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background-color: #f6f7f9;
        }

        .topbar {
            height: 56px;
            background-color: #020617;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 20px;
            border-bottom: 1px solid #1e293b;
        }

        .top-title {
            font-size: 16px;
            font-weight: 600;
            color: #e5e7eb;
        }

        .logout-btn {
            background: #dc2626;
            border: none;
            color: white;
            padding: 7px 16px;
            border-radius: 6px;
            font-size: 13px;
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
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            position: relative;
        }

        .back-btn {
            position: absolute;
            top: 18px;
            left: 18px;
            padding: 6px 14px;
            background: #eef0f3;
            color: #374151;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
            border: 1px solid #d1d5db;
        }

        h2 {
            text-align: center;
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-top: 14px;
            font-size: 12px;
            font-weight: 600;
        }

        input, select {
            width: 100%;
            padding: 9px;
            margin-top: 6px;
            border-radius: 6px;
            border: 1px solid #d1d5db;
        }

        button {
            margin-top: 22px;
            width: 100%;
            padding: 10px;
            background: #2563eb;
            color: white;
            border: none;
            border-radius: 20px;
            font-weight: 600;
            cursor: pointer;
        }

        .error-message {
            text-align: center;
            color: #dc2626;
            font-size: 13px;
            font-weight: 600;
            margin-bottom: 14px;
        }
    </style>
</head>

<body>

<div class="topbar">
    <div class="top-title">POS Application</div>

    <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
</div>

<div class="card">

    <a href="${pageContext.request.contextPath}/stocks/list" class="back-btn">
        Back
    </a>

    <h2>Add Stock</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form:form
        action="${pageContext.request.contextPath}/stocks/add"
        method="post"
        modelAttribute="stocksDto">

       <label>Product Name</label>
       <form:select path="identifier" required="true">
           <form:option value="">-- Select Product --</form:option>

           <c:forEach var="product" items="${products}">
               <form:option value="${product.identifier}">
                   ${product.identifier}
               </form:option>
           </c:forEach>
       </form:select>

        <label>Available Stock</label>
        <form:input
            path="availableStock"
            type="number"
            required="true"
            min="0"
            title="Available stock must be zero or greater"
        />

        <label>Incoming Stock</label>
        <form:input
            path="incomingStock"
            type="number"
            min="0"
            title="Incoming stock cannot be negative"
        />

        <label>Outgoing Stock</label>
        <form:input
            path="outgoingStock"
            type="number"
            min="0"
            title="Outgoing stock cannot be negative"
        />

        <label>Product Status</label>
        <form:select path="productStatus" required="true">
            <form:option value="">-- Select Status --</form:option>
            <form:option value="AVAILABLE">Available</form:option>
            <form:option value="OUT_OF_STOCK">Out of Stock</form:option>
            <form:option value="LOW_STOCK">Low Stock</form:option>
            <form:option value="INCOMING">Incoming</form:option>
            <form:option value="BLOCKED">Blocked</form:option>
            <form:option value="DAMAGED">Damaged</form:option>
        </form:select>

       <label>Warehouse</label>
       <form:select path="wareHouse" required="true">
           <form:option value="">-- Select Warehouse --</form:option>

           <c:forEach var="wh" items="${warehouse}">
               <form:option value="${wh.identifier}">
                   ${wh.identifier}
               </form:option>
           </c:forEach>
       </form:select>

        <button type="submit">Add Stock</button>

    </form:form>

</div>

</body>
</html>