<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Category | POS</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            background: radial-gradient(circle at 20% 30%, rgba(255,122,0,0.15), transparent 40%),
                        radial-gradient(circle at 80% 70%, rgba(255,72,0,0.15), transparent 40%),
                        linear-gradient(135deg, #1f1f1f, #3a2e2a);
        }

        .card {
            width: 420px;
            background: rgba(255,255,255,0.12);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
            border: 1px solid rgba(255,255,255,0.15);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #fff;
            font-weight: 600;
        }

        .message {
            text-align: center;
            color: #ffb3b3;
            font-size: 13px;
            margin-bottom: 10px;
        }

        label {
            font-size: 13px;
            color: #ddd;
            margin-bottom: 6px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: 1px solid rgba(255,255,255,0.2);
            font-size: 14px;
            background: rgba(255,255,255,0.1);
            color: #fff;
            margin-bottom: 12px;
        }

        input::placeholder {
            color: #ccc;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #ff7a00;
            box-shadow: 0 0 6px rgba(255,122,0,0.3);
        }

        .btn-primary {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: none;
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: white;
            font-size: 14px;
            margin-top: 10px;
        }

        .btn-primary:hover {
            opacity: 0.9;
        }

        .back {
            display: block;
            text-align: center;
            margin-top: 12px;
            font-size: 13px;
            color: #fff;
            text-decoration: none;
            background: rgba(255,255,255,0.2);
            padding: 10px;
            border-radius: 20px;
        }

        .back:hover {
            background: rgba(255,255,255,0.3);
        }
    </style>
</head>

<body>

<div class="card">

    <h2>Create Category</h2>

    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>

    <form:form method="post"
               action="${pageContext.request.contextPath}/category/add"
               modelAttribute="categoryDto">

        <label>Category Name</label>
        <form:input path="identifier" placeholder="Enter category name"/>

        <label>Super Category</label>
        <form:select path="superCategory">
            <form:option value="" label="Select Super Category"/>
            <c:forEach var="cat" items="${categories}">
                <form:option value="${cat.identifier}">
                    ${cat.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <button type="submit" class="btn-primary">
            Save Category
        </button>

    </form:form>

    <a href="${pageContext.request.contextPath}/category/list" class="back">
        Back to Category List
    </a>

</div>

</body>
</html>