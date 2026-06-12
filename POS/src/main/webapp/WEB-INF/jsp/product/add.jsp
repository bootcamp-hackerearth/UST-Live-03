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
            font-family: Arial;
            background: #f4f7f6;
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

        .card {
            width: 480px;
            margin: 80px auto;
            background: white;
            padding: 30px 35px;
            border-radius: 12px;
            border: 1px solid #ddd;
            box-shadow: 0 4px 10px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-top: 15px;
            font-size: 13px;
            font-weight: bold;
        }

        input, select {
            width: 100%;
            margin-top: 6px;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 13px;
            box-sizing: border-box;
        }

        select[multiple] {
            height: 110px;
        }

        small {
            font-size: 11px;
            color: #999;
            margin-top: 3px;
            display: block;
        }

        .error-msg {
            margin-bottom: 12px;
            padding: 10px;
            text-align: center;
            border-radius: 6px;
            background: #fee2e2;
            color: #b91c1c;
        }

        .btn-submit {
            margin-top: 25px;
            width: 100%;
            padding: 12px;
            background: #28a745;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
        }

        .btn-submit:hover {
            background: #218838;
        }
    </style>
</head>

<body>

<a href="/product/list" class="back-btn">← Back</a>

<div class="card">

    <h2>Add Product</h2>

    <!-- ERROR -->
    <c:if test="${not empty message}">
        <div class="error-msg">${message}</div>
    </c:if>

    <form:form action="/product/add" method="post" modelAttribute="productDto">

        <!-- IDENTIFIER -->
        <label>Identifier</label>
        <form:input path="identifier" required="true"/>

        <!-- NAME -->
        <label>Name</label>
        <form:input path="name" required="true"/>

        <!-- CATEGORIES -->
        <label>Categories</label>
        <form:select path="categories" multiple="true">
            <c:forEach var="cat" items="${categories}">
                <form:option value="${cat.identifier}">
                    ${cat.identifier}
                    <c:if test="${not empty cat.superCategory}"> (${cat.superCategory})</c:if>
                </form:option>
            </c:forEach>
        </form:select>
        <small>Hold Ctrl (Windows) or Cmd (Mac) to select multiple</small>

        <!-- BRAND -->
        <label>Brand</label>
        <form:select path="brand">
            <form:option value="">-- Select Brand --</form:option>
            <c:forEach var="b" items="${brand}">
                <form:option value="${b.identifier}">${b.identifier}</form:option>
            </c:forEach>
        </form:select>

        <!-- MODEL -->
        <label>Model</label>
        <form:select path="model">
            <form:option value="">-- Select Model --</form:option>
            <c:forEach var="m" items="${model}">
                <form:option value="${m.identifier}">${m.identifier}</form:option>
            </c:forEach>
        </form:select>

        <!-- UNIT -->
        <label>Unit</label>
        <form:select path="unit">
            <form:option value="">-- Select Unit --</form:option>
            <c:forEach var="u" items="${unit}">
                <form:option value="${u.identifier}">${u.identifier}</form:option>
            </c:forEach>
        </form:select>
        <!-- ACTIVE -->
        <label>Status</label>
        <form:select path="status">
            <form:option value="true">Active</form:option>
            <form:option value="false">Inactive</form:option>
        </form:select>

        <!-- SUBMIT -->
        <input type="submit" value="Save Product" class="btn-submit"/>

    </form:form>

</div>

</body>
</html>