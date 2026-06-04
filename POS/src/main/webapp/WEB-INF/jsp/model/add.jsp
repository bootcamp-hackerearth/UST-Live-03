<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Model</title>

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
            width: 520px;
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

        input:focus,
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
            background-color: #fee2e2;
            color: #991b1b;
            text-align: center;
            font-size: 13px;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Add Model</h2>

    <c:if test="${not empty message}">
        <div class="message">
            ${message}
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/model/add">
        <input
            type="hidden"
            name="id"
            value="${modelDto.id}"
        />

        <div class="form-group">
            <label>Identifier</label>
            <input
                type="text"
                name="identifier"
                value="${modelDto.identifier}"
                placeholder="Enter model identifier"
                required
            />
        </div>

        <div class="form-group">
            <label>Model Name</label>
            <input
                type="text"
                name="name"
                value="${modelDto.name}"
                placeholder="Enter model name"
                required
            />
        </div>

        <button type="submit" class="btn-submit">
            Save Model
        </button>

        <div class="back-center">
            <a href="${pageContext.request.contextPath}/model/list">
                Back
            </a>
        </div>

    </form>
</div>

</body>
</html>