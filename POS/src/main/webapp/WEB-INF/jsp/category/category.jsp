<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Category</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        body {
            font-family: "Poppins", sans-serif;
            background-color: #f4f6fb;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .card {
            width: 520px;
            background: #ffffff;
            border-radius: 16px;
            padding: 30px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
        }

        h3 {
            text-align: center;
            color: #4e73df;
            margin-bottom: 25px;
        }

        .form-group {
            display: grid;
            grid-template-columns: 150px 1fr;
            margin-bottom: 15px;
            align-items: center;
        }

        label {
            font-weight: 600;
            color: #444;
        }

        input, select {
            padding: 8px;
            border-radius: 6px;
            border: 1px solid #ccc;
        }

        input[readonly] {
            background-color: #e9ecef;
            cursor: not-allowed;
        }

        button {
            width: 60%;
            margin: 20px auto 0;
            display: block;
            padding: 10px;
            background: linear-gradient(135deg, #4e73df, #2e59d9);
            color: #fff;
            border: none;
            border-radius: 6px;
            cursor: pointer;
        }

        .back-btn {
            display: block;
            text-align: center;
            margin-top: 10px;
            color: #4e73df;
            text-decoration: none;
        }

        .message {
            color: red;
            text-align: center;
            margin-bottom: 10px;
        }
    </style>
</head>

<body>
<div class="card">
    <h3>Update Category</h3>
    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>
    <form:form method="post"
               action="${pageContext.request.contextPath}/category/update"
               modelAttribute="category">
        <div class="form-group">
            <label>Identifier</label>
            <form:input path="identifier" readonly="true"/>
        </div>
        <div class="form-group">
            <label>Category Name</label>
            <form:input path="name"/>
        </div>
        <div class="form-group">
            <label>Super Category</label>
            <form:select path="superCategory">
                <form:option value="">-- Select Category --</form:option>
                <c:forEach var="cat" items="${categories}">
                    <form:option value="${cat.name}">
                        ${cat.name}
                    </form:option>
                </c:forEach>
            </form:select>
        </div>
        <button type="submit">Update Category</button>
    </form:form>
    <a href="${pageContext.request.contextPath}/category/list" class="back-btn">
        ← Back to List
    </a>
</div>
</body>
</html>
