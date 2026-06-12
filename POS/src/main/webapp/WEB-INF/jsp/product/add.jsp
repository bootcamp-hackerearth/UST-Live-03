<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Product</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            background: #ffffff;
            width: 480px;
            padding: 36px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        h2 {
            text-align: center;
            margin-bottom: 28px;
            color: #1e293b;
            font-size: 22px;
            font-weight: 700;
        }

        .form-group {
            margin-bottom: 16px;
        }

        .checkbox-group {
            margin-top: 8px;
        }

        .checkbox-item {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 8px;
        }

        .checkbox-item input[type="checkbox"] {
            width: 16px;
            height: 16px;
            cursor: pointer;
        }

        .checkbox-item label {
            margin: 0;
            font-size: 14px;
            cursor: pointer;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 13px;
            font-weight: 600;
            color: #334155;
        }

        input,
        textarea,
        select {
            width: 100%;
            padding: 12px;
            border-radius: 6px;
            border: 1px solid #cbd5e1;
            font-size: 14px;
        }

        textarea {
            resize: vertical;
            min-height: 80px;
        }

        input:focus,
        textarea:focus,
        select:focus {
            outline: none;
            border-color: #1e293b;
            box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.15);
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            margin-top: 16px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            background-color: #1e293b;
            color: #ffffff;
        }

        .btn-submit:hover {
            background-color: #0f172a;
        }

        .back-center {
            margin-top: 20px;
            text-align: center;
        }

        .back-center a {
            text-decoration: none;
            color: #1e293b;
            font-size: 14px;
            font-weight: 600;
            padding: 8px 18px;
            border-radius: 6px;
            background-color: #e2e8f0;
        }

        .message {
            margin-bottom: 16px;
            padding: 10px;
            border-radius: 6px;
            background-color: #e0f2fe;
            color: #0369a1;
            text-align: center;
            font-size: 13px;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Add Product</h2>

    <c:if test="${not empty message}">
        <div class="message">
            ${message}
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/product/add">

        <div class="form-group">
            <label>Identifier</label>
            <input
                type="text"
                name="identifier"
                placeholder="Enter product identifier"
                required
            />
        </div>

        <div class="form-group">
            <label>Name</label>
            <input
                type="text"
                name="name"
                placeholder="Enter product name"
                required
            />
        </div>

        <div class="form-group">
            <label>Unit</label>
            <select name="unit" id="unit" required>
                <option value="">-- Select Unit --</option>

                <c:forEach items="${units}" var="u">
                    <option value="${u.name}">
                        ${u.name}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Brand</label>
            <select name="brand" id="brand" required>
                <option value="">-- Select Brand --</option>

                <c:forEach items="${brands}" var="b">
                    <option value="${b.name}">
                        ${b.name}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Category</label>
            <select name="category" id="category" required>
                <option value="">-- Select Category --</option>

                <c:forEach items="${categories}" var="cat">
                    <option value="${cat.name}">
                        ${cat.name}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Description</label>
            <textarea
                name="description"
                placeholder="Enter product description"
            ></textarea>
        </div>

        <button type="submit" class="btn-submit">
            Save Product
        </button>

        <div class="back-center">
            <a href="${pageContext.request.contextPath}/product/list">
                Back
            </a>
        </div>

    </form>
</div>

</body>
</html>