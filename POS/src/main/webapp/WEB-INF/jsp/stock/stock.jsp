<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Stock</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

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
            width: 520px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

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
            background: rgba(75, 108, 183, 0.08);
            border-radius: 50%;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
            transition: all 0.25s ease;
        }

        .back-icon:hover {
            background: #4b6cb7;
            color: #ffffff;
            transform: translateX(-4px) scale(1.05);
        }

        label {
            font-size: 14px;
            font-weight: 500;
            margin-bottom: 6px;
            color: #333;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 10px 12px;
            border-radius: 10px;
            border: 1px solid #ccc;
            font-size: 14px;
            transition: 0.2s ease;
        }

        input[readonly] {
            background: #f1f3f5;
            cursor: not-allowed;
        }

        input:focus, select:focus {
            border-color: #4b6cb7;
            box-shadow: 0 0 0 2px rgba(75,108,183,0.15);
            outline: none;
        }

        .form-group {
            margin-bottom: 16px;
        }

        .alert-error {
            background: #ffe5e5;
            color: #b91c1c;
            padding: 10px;
            border-radius: 10px;
            text-align: center;
            margin-bottom: 18px;
            font-size: 14px;
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            margin-top: 10px;
            border-radius: 12px;
            border: none;
            font-size: 15px;
            font-weight: 600;
            color: white;
            cursor: pointer;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            transition: 0.25s ease;
        }

        .btn-submit:hover {
            transform: scale(1.04);
        }
    </style>
</head>

<body>

<div class="card-container">

    <a href="/stock/list" class="back-icon">←</a>

    <h2>Update Stock</h2>

    <!-- ❌ Error Message -->
    <c:if test="${not empty message}">
        <div class="alert-error">
            ${message}
        </div>
    </c:if>

    <form:form method="post" action="/stock/update" modelAttribute="stock">
         <label class="form-label">Warehouse Name</label>
                        <form:select path="warehouseName" cssClass="form-control">
                                    <form:option value="" label="-- Select Product --" />
                                    <c:forEach items="${warehouses}" var="warehouse">
                                            <form:option value="${warehouse.identifier}">
                                                ${warehouse.identifier}
                                            </form:option>
                                    </c:forEach>
                                </form:select>

        <div class="form-group">
            <label>Identifier</label>
            <form:input path="identifier" readonly="true"/>
        </div>

        <div class="form-group">
            <label>Status</label>
            <form:select path="stockStatus">
                <form:option value="AVAILABLE">AVAILABLE</form:option>
                <form:option value="OUT_OF_STOCK">OUT OF STOCK</form:option>
                <form:option value="DISCONTINUED">DISCONTINUED</form:option>
            </form:select>
        </div>

        <div class="form-group">
            <label>Quantity</label>
            <form:input path="quantity" type="number" min="0" required="true"/>
        </div>

        <form:hidden path="id"/>

        <button type="submit" class="btn-submit">
            Update Stock
        </button>

    </form:form>

</div>

</body>
</html>