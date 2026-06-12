<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Product</title>

    <style>

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
        }

        .back-btn {
            position: fixed;
            top: 20px;
            left: 20px;
            padding: 8px 14px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
        }

        .back-btn:hover {
            background: #5a6268;
        }

        .card {
            width: 500px;
            margin: 70px auto;
            background: white;
            padding: 30px 35px;
            border-radius: 12px;
            border: 1px solid #ddd;
            box-shadow: 0 4px 10px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #333;
        }

        label {
            display: block;
            margin-top: 15px;
            font-size: 13px;
            font-weight: bold;
            color: #444;
        }

        input,
        select {
            width: 100%;
            margin-top: 6px;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 6px;
            box-sizing: border-box;
            font-size: 14px;
        }

        input:focus,
        select:focus {
            outline: none;
            border-color: #007bff;
        }

        input[readonly] {
            background-color: #f8f9fa;
            color: #666;
        }

        select[multiple] {
            height: 120px;
        }

        small {
            display: block;
            margin-top: 4px;
            color: #888;
            font-size: 11px;
        }

        .error-msg {
            margin-bottom: 15px;
            padding: 10px;
            text-align: center;
            border-radius: 6px;
            background-color: #fee2e2;
            color: #b91c1c;
            font-size: 13px;
        }

        .btn-submit {
            margin-top: 25px;
            width: 100%;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 15px;
            font-weight: bold;
            cursor: pointer;
        }

        .btn-submit:hover {
            background-color: #0056b3;
        }

    </style>
</head>

<body>

<a href="/product/list" class="back-btn">
    ← Back
</a>

<div class="card">

    <h2>Edit Product</h2>

    <!-- ERROR -->
    <c:if test="${not empty message}">
        <div class="error-msg">
            ${message}
        </div>
    </c:if>

    <form:form action="/product/update"
               method="post"
               modelAttribute="productDto">

        <!-- IDENTIFIER -->
        <label>Identifier</label>

        <form:input path="identifier"
                    readonly="true"/>

        <!-- NAME -->
        <label>Product Name</label>

        <form:input path="name"
                    required="true"/>

        <!-- CATEGORY -->
        <label>Categories</label>

        <form:select path="categories"
                     multiple="true">

            <c:forEach var="cat" items="${categories}">

                <form:option value="${cat.identifier}">
                    ${cat.identifier}
                </form:option>

            </c:forEach>

        </form:select>

        <small>
            Hold Ctrl (Windows) or Cmd (Mac) to select multiple
        </small>

        <!-- BRAND -->
        <label>Brand</label>

        <form:select path="brand">

            <form:option value="">
                -- Select Brand --
            </form:option>

            <c:forEach var="b" items="${brand}">

                <form:option value="${b.identifier}">
                    ${b.identifier}
                </form:option>

            </c:forEach>

        </form:select>

        <!-- MODEL -->
        <label>Model</label>

        <form:select path="model">

            <form:option value="">
                -- Select Model --
            </form:option>

            <c:forEach var="m" items="${model}">

                <form:option value="${m.identifier}">
                    ${m.identifier}
                </form:option>

            </c:forEach>

        </form:select>

        <!-- UNIT -->
        <label>Unit</label>

        <form:select path="unit">

            <form:option value="">
                -- Select Unit --
            </form:option>

            <c:forEach var="u" items="${unit}">

                <form:option value="${u.identifier}">
                    ${u.identifier}
                </form:option>

            </c:forEach>

        </form:select>

        <!-- STATUS -->
        <label>Status</label>

        <form:select path="status">

            <form:option value="true">
                Active
            </form:option>

            <form:option value="false">
                Inactive
            </form:option>

        </form:select>

        <!-- BUTTON -->
        <input type="submit"
               value="Update Product"
               class="btn-submit"/>

    </form:form>

</div>

</body>
</html>