<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Price</title>

    <style>

body {
    margin: 0;
    font-family: "Inter", sans-serif;
    background-color: #3f3f3f;
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    padding: 40px 0;
    box-sizing: border-box;
}

        .price-card {
            background: #f3efe9;
            width: 470px;
            padding: 42px;
            box-sizing: border-box;
        }

        .back-btn {
            display: inline-block;
            margin-bottom: 22px;
            text-decoration: none;
            color: #2f2f2f;
            font-size: 13px;
            font-weight: 700;
            letter-spacing: 1px;
        }

        .back-btn:hover {
            opacity: 0.7;
        }

        h2 {
            margin: 0 0 34px;
            font-size: 26px;
            font-weight: 700;
            color: #2f2f2f;
        }

        .form-group {
            margin-bottom: 28px;
        }

        label {
            font-size: 12px;
            letter-spacing: 2px;
            color: #8a8a8a;
            display: block;
            margin-bottom: 10px;
        }

        input,
        select {
            width: 100%;
            box-sizing: border-box;
            padding: 10px 0;
            border: none;
            border-bottom: 3px solid #cfcfcf;
            background: transparent;
            font-size: 16px;
            outline: none;
            color: #2f2f2f;
            font-family: "Inter", sans-serif;
        }

        select {
            padding-bottom: 12px;
        }

        input:focus,
        select:focus {
            border-bottom: 3px solid #3f3f3f;
        }

        input:-webkit-autofill,
        input:-webkit-autofill:hover,
        input:-webkit-autofill:focus,
        select:-webkit-autofill {

            -webkit-box-shadow: 0 0 0px 1000px #f3efe9 inset !important;
            -webkit-text-fill-color: #2f2f2f !important;
            transition: background-color 5000s ease-in-out 0s;

        }

        .add-btn {
            width: 100%;
            padding: 16px;
            margin-top: 8px;
            background: #3f3f3f;
            color: #ffffff;
            border: 2px solid #3f3f3f;
            font-size: 15px;
            font-weight: 700;
            letter-spacing: 2px;
            cursor: pointer;
            transition: 0.3s;
        }

        .add-btn:hover {
            background: transparent;
            color: #3f3f3f;
        }

        .error-message {
            margin-top: 16px;
            padding: 12px;
            background: #ffe5e0;
            color: #b91c1c;
            font-size: 13px;
        }

    </style>

</head>

<body>

<div class="price-card">

    <a href="${pageContext.request.contextPath}/price/list"
       class="back-btn">

        ᐸ BACK

    </a>

    <h2>Add Price</h2>

    <c:if test="${not empty message}">

        <div class="error-message">

            ${message}

        </div>

    </c:if>

    <form:form
            action="${pageContext.request.contextPath}/price/add"
            method="post"
            modelAttribute="priceDto">

        <div class="form-group">

            <label>SKU CODE</label>

            <form:select path="identifier" required="true">

                <form:option value="">
                    -- Select SkuCode --
                </form:option>

                <c:forEach items="${products}" var="product">

                    <form:option value="${product.identifier}">
                        ${product.identifier}
                    </form:option>

                </c:forEach>

            </form:select>

        </div>

        <div class="form-group">

            <label>MRP</label>

            <form:input
                    path="mrp"
                    type="number"
                    step="0.01"
                    required="true"/>

        </div>

        <div class="form-group">

            <label>SELLING PRICE</label>

            <form:input
                    path="sellingPrice"
                    type="number"
                    step="0.01"
                    required="true"/>

        </div>

        <div class="form-group">

            <label>COST PRICE</label>

            <form:input
                    path="costPrice"
                    type="number"
                    step="0.01"
                    required="true"/>

        </div>

        <div class="form-group">

            <label>EFFECTIVE FROM</label>

            <form:input
                    path="effectiveFrom"
                    type="date"
                    required="true"/>

        </div>

        <button type="submit"
                class="add-btn">

            ADD PRICE

        </button>

    </form:form>

</div>

</body>
</html>