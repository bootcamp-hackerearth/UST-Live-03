<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Category</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            padding: 30px;
        }

        .container {
            width: 60%;
            margin: auto;
            background: #ffffff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        h2 {
            text-align: center;
            margin-bottom: 24px;
            color: #1e293b;
            font-size: 22px;
            font-weight: 700;
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 13px;
            font-weight: 600;
            color: #334155;
        }

        input,
        select {
            width: 100%;
            padding: 12px;
            border-radius: 6px;
            border: 1px solid #cbd5e1;
            font-size: 14px;
        }

        input[readonly] {
            background-color: #f8fafc;
            color: #64748b;
        }

        input:focus,
        select:focus {
            outline: none;
            border-color: #1e293b;
            box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.15);
        }

        .btn {
            padding: 10px 18px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            color: #ffffff;
            background-color: #1e293b;
            text-decoration: none;
        }

        .btn:hover {
            background-color: #0f172a;
        }

        .back-btn {
            background-color: #e2e8f0;
            color: #1e293b;
            margin-left: 12px;
        }

        .btn-group {
            margin-top: 20px;
            text-align: center;
        }

        .message {
            color: #dc2626;
            text-align: center;
            margin-bottom: 12px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Update Category</h2>

    <c:if test="${not empty message}">
        <div class="message">
            ${message}
        </div>
    </c:if>

    <form
        action="${pageContext.request.contextPath}/category/update"
        method="post"
    >
        <div class="form-group">
            <label>ID</label>
            <input
                type="text"
                name="id"
                value="${category.id}"
                readonly
            />
        </div>

        <div class="form-group">
            <label>Identifier</label>
            <input
                type="text"
                name="identifier"
                value="${category.identifier}"
                readonly
            />
        </div>

        <div class="form-group">
            <label>Category Name</label>
            <input
                type="text"
                name="name"
                value="${category.name}"
                required
            />
        </div>

        <div class="form-group">
            <label>Super Category</label>
            <select name="superCategory">
                <option value="">-- None --</option>

                <c:forEach items="${categories}" var="cat">
                    <c:if test="${cat.identifier ne category.identifier}">
                        <option
                            value="${cat.name}"
                            <c:if test="${cat.name eq category.superCategory}">
                                selected
                            </c:if>
                        >
                            ${cat.name}
                        </option>
                    </c:if>
                </c:forEach>
            </select>
        </div>

        <div class="btn-group">
            <button type="submit" class="btn">
                Update Category
            </button>

            <a
                href="${pageContext.request.contextPath}/category/list"
                class="btn back-btn"
            >
                Back
            </a>
        </div>
    </form>
</div>

</body>
</html>