<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Category</title>
    <style>
        body {
            min-height: 100vh;
            background-color: #FFF8F0;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .container {
            background: #ffffff;
            width: 620px;
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

        input:focus,
        select:focus {
            border-color: #4B2E2B;
            outline: none;
        }

        .btn-submit {
            width: 100%;
            padding: 14px;
            margin-top: 20px;
            border-radius: 12px;
            border: none;
            background-color: #4B2E2B;
            color: #FFF8F0;
            font-weight: 600;
        }

        .btn-submit:hover {
            background-color: #3a2421;
        }

        .back-center {
            text-align: center;
            margin-top: 18px;
        }

        .back-center a {
            background-color: #e8dcd2;
            color: #4B2E2B;
            padding: 8px 20px;
            border-radius: 10px;
            text-decoration: none;
        }

        .back-center a:hover {
            background-color: #d8c6b8;
        }

        .message {
            margin-bottom: 16px;
            padding: 10px;
            border-radius: 10px;
            background-color: #f5e6dc;
            color: #4B2E2B;
            text-align: center;
            font-size: 13px;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Add Category</h2>
    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/category/add">
        <input type="hidden" name="id" value="${categoryDto.id}" />
        <div class="form-group">
            <label>Identifier</label>
            <input type="text"
                   name="identifier"
                   value="${categoryDto.identifier}"
                   placeholder="Enter identifier"
                   required/>
        </div>
        <div class="form-group">
            <label>Category Name</label>
            <input type="text"
                   name="name"
                   value="${categoryDto.name}"
                   placeholder="Enter category name"
                   required/>
        </div>
        <div class="form-group">
            <label>Super Category</label>
            <select name="superCategory">
                <option value="">-- None --</option>
                <c:forEach items="${categorys}" var="cat">
                    <option value="${cat.name}"
                        <c:if test="${cat.name eq categoryDto.superCategory}">
                            selected
                        </c:if>>
                        ${cat.name}
                    </option>
                </c:forEach>
            </select>
        </div>
        <button type="submit" class="btn-submit">Save Category</button>
        <div class="back-center">
            <a href="${pageContext.request.contextPath}/category/list">Back</a>
        </div>
    </form>
</div>
</body>
</html>