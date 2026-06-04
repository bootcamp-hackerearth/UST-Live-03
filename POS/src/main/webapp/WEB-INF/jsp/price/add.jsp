<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Price</title>


    <style>
        :root {
            --primary-color: #4f46e5;
            --primary-hover: #4338ca;
        }

        body {
            margin: 0;
            font-family: "Segoe UI", Arial, sans-serif;
            background: linear-gradient(135deg, #f5f7fb, #eef2ff);
        }


        .back-btn {
            position: fixed;
            top: 20px;
            left: 20px;
            padding: 8px 16px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 20px;
            font-size: 14px;
        }

        .back-btn:hover {
            background: #5a6268;
        }

        .card {
            width: 460px;
            margin: 90px auto;
            background: #ffffff;
            border-radius: 14px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .card-header {
            background: var(--primary-color);
            padding: 18px;
            color: white;
            text-align: center;
            font-size: 20px;
            font-weight: 600;
        }

        .card-body {
            padding: 28px 32px;
        }

        label {
            display: block;
            margin-top: 16px;
            font-size: 14px;
            font-weight: 600;
            color: #444;
        }

        input, select {
            width: 100%;
            margin-top: 6px;
            padding: 11px;
            border-radius: 8px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        input:focus, select:focus {
            outline: none;
            border-color: var(--primary-color);
        }

        .error-msg {
            margin-bottom: 15px;
            padding: 10px;
            border-radius: 8px;
            text-align: center;
            background: #fee2e2;
            color: #b91c1c;
            font-size: 14px;
        }

        .btn-submit {
            margin-top: 30px;
            width: 100%;
            padding: 12px;
            background: var(--primary-color);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
        }

        .btn-submit:hover {
            background: var(--primary-hover);
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/price/list" class="back-btn">
    ← Back
</a>

<div class="card">

    <div class="card-header">
        Add Price
    </div>

    <div class="card-body">

         <c:if test="${not empty successMessage}">
                    <div class="alert alert-success text-center">
                        ${successMessage}
                    </div>
                </c:if>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger text-center">
                        ${errorMessage}
                    </div>
                </c:if>

        <form:form action="add" method="post" modelAttribute="prices">

            <label>Product</label>
            <form:select path="identifier" required="true">
                <form:option value="">-- Select Product --</form:option>
                <form:options items="${products}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>
            <form:errors path="identifier" cssClass="error-msg"/>

            <label>MRP</label>
            <form:input path="mrp" type="number" step="0.01"/>
            <form:errors path="mrp" cssClass="error-msg"/>

            <label>Selling Price</label>
            <form:input path="sellingPrice" type="number" step="0.01"/>
            <form:errors path="sellingPrice" cssClass="error-msg"/>

            <label>Effective From</label>
            <form:input path="effectiveFrom" type="date"/>
            <form:errors path="effectiveFrom" cssClass="error-msg"/>

            <input type="submit" value="Add Price" class="btn-submit"/>

        </form:form>

    </div>
</div>

</body>
</html>