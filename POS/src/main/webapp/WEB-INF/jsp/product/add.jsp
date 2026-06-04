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
            font-family: "Inter", sans-serif;
            background-color: #3f3f3f;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 30px 0;
        }

        .product-card {
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
        select,
        textarea {
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
            appearance: none;
            background: transparent;
        }

        select[multiple] {
            height: 120px;
        }

        input:focus,
        select:focus,
        textarea:focus {
            border-bottom: 3px solid #3f3f3f;
        }

        input:-webkit-autofill,
        input:-webkit-autofill:hover,
        input:-webkit-autofill:focus,
        textarea:-webkit-autofill,
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

        .hint {
            font-size: 11px;
            color: #8a8a8a;
            margin-top: 8px;
            line-height: 1.5;
        }

    </style>

</head>

<body>

<div class="product-card">

    <a href="${pageContext.request.contextPath}/product/list"
       class="back-btn">

        ᐸ BACK

    </a>

    <h2>Add Product</h2>

    <c:if test="${not empty message}">

        <div class="error-message">

            ${message}

        </div>

    </c:if>

    <form:form
            action="${pageContext.request.contextPath}/product/add"
            method="post"
            modelAttribute="productDto">

        <!-- SKU CODE -->
        <div class="form-group">

            <label>SKU CODE</label>

            <form:input
                    path="identifier"
                    required="true"/>

        </div>

        <!-- CATEGORY -->
        <div class="form-group">

            <label>CATEGORY</label>

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

        </div>

        <!-- BRAND -->
        <div class="form-group">

            <label>BRAND</label>

            <form:select
                    path="brand"
                    required="true">

                <form:option value="">
                    -- Select Brand --
                </form:option>

                <c:forEach var="bran" items="${brand}">

                    <form:option value="${bran.identifier}">

                        ${bran.identifier}

                    </form:option>

                </c:forEach>

            </form:select>

        </div>

        <!-- UNIT -->
        <div class="form-group">

            <label>UNIT</label>

            <form:select
                    path="unit"
                    required="true">

                <form:option value="">
                    -- Select Unit --
                </form:option>

                <c:forEach var="uni" items="${unit}">

                    <form:option value="${uni.identifier}">

                        ${uni.identifier}

                    </form:option>

                </c:forEach>

            </form:select>

        </div>

        <!-- MODEL -->
        <div class="form-group">

            <label>MODEL</label>

            <form:select
                    path="model"
                    required="true">

                <form:option value="">
                    -- Select Model --
                </form:option>

                <c:forEach var="mode" items="${model}">

                    <form:option value="${mode.identifier}">

                        ${mode.identifier}

                    </form:option>

                </c:forEach>

            </form:select>

        </div>

        <!-- PRODUCT NAME -->
        <div class="form-group">

            <label>PRODUCT NAME</label>

            <form:input
                    path="name"
                    type="text"
                    required="true"/>

        </div>

        <button type="submit"
                class="add-btn">

            ADD PRODUCT

        </button>

    </form:form>

</div>

</body>
</html>