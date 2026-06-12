<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Category</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background-color: #ffffff;
        }

        /* ===== CARD ===== */
        .card {
            width: 360px;
            margin: 40px auto;
            background: #ffffff;
            padding: 22px;
            border-radius: 14px;
            position: relative;
            box-shadow: 0 4px 12px rgba(0,0,0,0.25);
        }


        .app-title {
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #14b8a6; /* light teal */
            margin-bottom: 4px;
        }

        .back-btn {
            position: absolute;
            top: 12px;
            left: 12px;
            background: #ffffff;
            border: 1px solid teal;
            padding: 5px 12px;
            border-radius: 16px;
            text-decoration: none;
            font-size: 12px;
            font-weight: 600;
            color: teal;
        }

        h2 {
            text-align: center;
            margin-bottom: 12px;
            font-size: 20px;
        }

        /* ===== FORM ===== */
        label {
            margin-top: 10px;
            display: block;
            font-size: 12px;
            font-weight: 600;
            color: #475569;
        }

        input, select {
            display: block;
            width: 100%;
            box-sizing: border-box;
            padding: 8px 10px;
            margin-top: 4px;
            border-radius: 18px;
            border: 1px solid #d1d5db;
            font-size: 13px;
        }

        button {
            margin-top: 16px;
            width: 100%;
            background: teal;
            color: white;
            border: none;
            padding: 8px 10px;
            border-radius: 18px;
            font-weight: 600;
            font-size: 13px;
        }

        .error {
            color: #ef4444;
            text-align: center;
            font-size: 12px;
            font-weight: 600;
            margin-bottom: 10px;
        }
    </style>
</head>

<body>

<div class="card">


    <div class="app-title">POS Application</div>
    <a href="${pageContext.request.contextPath}/category/list" class="back-btn">Back</a>
    <h2>Add Category</h2>
    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/category/add"
               method="post"
               modelAttribute="categoryDto">

        <label>Category Name</label>
        <form:input path="identifier" required="true"/>

        <label>Super Category</label>
        <form:select path="superCategory">
            <form:option value="">-- Select Super Category --</form:option>
            <c:forEach var="cat" items="${categories}">
                <form:option value="${cat.identifier}">
                    ${cat.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <button type="submit">Add Category</button>

    </form:form>

</div>

</body>
</html>