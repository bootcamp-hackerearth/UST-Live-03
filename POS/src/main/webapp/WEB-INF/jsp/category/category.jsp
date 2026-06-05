<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Category</title>
    <style>
        body {
            min-height: 100vh;
            background-color: #FFF8F0;
            font-family: "Segoe UI", Arial, sans-serif;
            padding-top: 40px;
        }

        .container {
            width: 620px;
            margin: auto;
            background: #ffffff;
            padding: 40px 45px;
            border-radius: 20px;
            box-shadow: 0 18px 35px rgba(75, 46, 43, 0.25);
        }

        h2 {
            text-align: center;
            color: #4B2E2B;
            margin-bottom: 30px;
            font-weight: 600;
        }

        form {
            display: flex;
            flex-direction: column;
            gap: 16px;
        }

        .form-group {
            display: grid;
            grid-template-columns: 170px 1fr;
            align-items: center;
            gap: 12px;
        }

        label {
            text-align: right;
            font-weight: 600;
            color: #4B2E2B;
        }

        input,
        select {
            padding: 10px 14px;
            border-radius: 10px;
            border: 1px solid #ccb7b2;
            font-size: 14px;
            width: 100%;
        }

        input[readonly] {
            background-color: #f5ede8;
        }

        input:focus,
        select:focus {
            border-color: #4B2E2B;
            outline: none;
        }

        .btn-group {
            display: flex;
            justify-content: center;
            gap: 12px;
            margin-top: 20px;
        }

        .btn {
            padding: 12px 20px;
            border-radius: 12px;
            border: none;
            font-weight: 600;
            background-color: #4B2E2B;
            color: #FFF8F0;
            text-decoration: none;
        }

        .btn:hover {
            background-color: #3a2421;
        }

        .back-btn {
            background-color: #e8dcd2;
            color: #4B2E2B;
        }

        .back-btn:hover {
            background-color: #d8c6b8;
        }

        .message {
            color: #8d3c36;
            text-align: center;
            margin-bottom: 10px;
            font-weight: 600;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Update Category</h2>
    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>
    <form action="${pageContext.request.contextPath}/category/update" method="post">
        <div class="form-group">
            <label>ID</label>
            <input type="text" name="id" value="${category.id}" readonly />
        </div>
        <div class="form-group">
            <label>Identifier</label>
            <input type="text" name="identifier" value="${category.identifier}" readonly />
        </div>
        <div class="form-group">
            <label>Category Name</label>
            <input type="text" name="name" value="${category.name}" required />
        </div>
        <div class="form-group">
            <label>Super Category</label>
            <select name="superCategory">
                <option value="">-- None --</option>
                <c:forEach items="${categories}" var="cat">
                    <c:if test="${cat.identifier ne category.identifier}">
                        <option value="${cat.name}"
                            <c:if test="${cat.name eq category.superCategory}">
                                selected
                            </c:if>>
                            ${cat.name}
                        </option>
                    </c:if>
                </c:forEach>
            </select>
        </div>
        <div class="btn-group">
            <button type="submit" class="btn">Update Category</button>
            <a href="${pageContext.request.contextPath}/category/list"
                class="btn back-btn">Back
            </a>
        </div>
    </form>
</div>
</body>
</html>