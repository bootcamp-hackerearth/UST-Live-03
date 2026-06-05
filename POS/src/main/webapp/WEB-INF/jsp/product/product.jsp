<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Product</title>
    <style>
        body {
            margin: 0;
            font-family: 'Poppins', 'Segoe UI', Arial, sans-serif;
            background: #fff8f0;
            min-height: 100vh;
            padding: 40px;
        }

        .container {
            width: 75%;
            margin: auto;
            background: #efe3d9;
            padding: 36px;
            border-radius: 16px;
            box-shadow: 0 14px 35px rgba(0, 0, 0, 0.15);
        }

        h2 {
            text-align: center;
            margin-bottom: 28px;
            color: #4a2e2b;
            font-size: 24px;
            font-weight: 700;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 14px;
            font-weight: 600;
            color: #4a2e2b;
        }

        input,
        textarea,
        select {
            width: 100%;
            padding: 12px 14px;
            border-radius: 10px;
            border: 1px solid #d6c2b8;
            font-size: 14px;
            background: #fff8f0;
        }

        textarea {
            resize: vertical;
            min-height: 100px;
        }

        input[readonly] {
            background-color: #f6ede7;
            color: #6b4a46;
        }

        input:focus,
        textarea:focus,
        select:focus {
            outline: none;
            border-color: #6b4a46;
            box-shadow: 0 0 0 3px rgba(107, 74, 70, 0.2);
        }

        .btn-group {
            margin-top: 26px;
            display: flex;
            justify-content: center;
            gap: 14px;
        }

        .btn {
            padding: 12px 22px;
            border-radius: 12px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            color: #fff8f0;
            background-color: #6b4a46;
            text-decoration: none;
            transition: 0.2s ease;
        }

        .btn:hover {
            background-color: #543835;
        }

        .back-btn {
            background-color: #8b5e59;
            color: #fff8f0;
        }

        .back-btn:hover {
            background-color: #6f4844;
        }

        .message {
            color: #9a3d35;
            text-align: center;
            margin-bottom: 16px;
            font-weight: 600;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Update Product</h2>
    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>
    <form action="${pageContext.request.contextPath}/product/update" method="post">
        <div class="form-group">
            <label>ID</label>
            <input type="text" name="id" value="${product.id}" readonly />
        </div>
        <div class="form-group">
            <label>Identifier</label>
            <input type="text" name="identifier"
                   value="${product.identifier}" readonly />
        </div>
        <div class="form-group">
            <label>Name</label>
            <input type="text" name="name"
                   value="${product.name}" required />
        </div>
        <div class="form-group">
            <label>Unit</label>
            <input type="text" name="unit"
                   value="${product.unit}" required />
        </div>
        <div class="form-group">
            <label>Brand</label>
            <input type="text" name="brand"
                   value="${product.brand}" />
        </div>
        <div class="form-group">
            <label>Category</label>
            <select name="category" required>
                <option value="">-- Select Category --</option>
                <c:forEach items="${categories}" var="cat">
                    <option value="${cat.identifier}"
                        <c:if test="${cat.identifier eq product.category}">
                            selected
                        </c:if>>
                        ${cat.name}
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label>Description</label>
            <textarea name="description">${product.description}</textarea>
        </div>
        <div class="btn-group">
            <button type="submit" class="btn">Update Product</button>
            <a href="${pageContext.request.contextPath}/product/list"
               class="btn back-btn">Back
            </a>
        </div>
    </form>
</div>
</body>
</html>