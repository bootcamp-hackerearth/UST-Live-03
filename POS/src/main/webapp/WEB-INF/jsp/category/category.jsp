<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Category</title>

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
        }

        .card {
            width: 420px;
            margin: 100px auto;
            background: #fff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.08);
        }

        label {
            display: block;
            margin-top: 15px;
            font-weight: bold;
        }

        input, select {
            width: 100%;
            margin-top: 6px;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 6px;
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
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
        }
    </style>
</head>

<body>

<a href="/category/list" class="back-btn">← Back</a>

<div class="card">

    <h2>Edit Category</h2>

    <c:if test="${not empty message}">
        <div class="error-msg">${message}</div>
    </c:if>

    <form:form action="/category/update" method="post" modelAttribute="categoryDto">

        <form:hidden path="id"/>

        <label>Category Identifier</label>
        <form:input path="identifier" readonly="true"/>

        <label>Super Category</label>
        <form:select path="superCategory">
            <form:option value="">-- None --</form:option>

            <c:forEach var="cat" items="${categories}">
                <c:if test="${cat.identifier ne categoryDto.identifier}">
                    <form:option value="${cat.identifier}">
                        ${cat.identifier}
                    </form:option>
                </c:if>
            </c:forEach>

        </form:select>

        <label>Status</label>
        <form:select path="status">  <%-- keep as status --%>
            <form:option value="true">Active</form:option>
            <form:option value="false">Inactive</form:option>
        </form:select>

        <input type="submit" value="Update Category" class="btn-submit"/>

    </form:form>

</div>

</body>
</html>