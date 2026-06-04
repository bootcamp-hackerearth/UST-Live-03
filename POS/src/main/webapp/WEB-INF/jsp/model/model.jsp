<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Model</title>

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

        .back-btn:hover {
            background-color: #cbd5e1;
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
    <h2>Update Model</h2>

    <c:if test="${not empty message}">
        <div class="message">
            ${message}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/model/update" method="post">

        <div class="form-group">
            <label>ID</label>
            <input
                type="text"
                name="id"
                value="${model.id}"
                readonly
            />
        </div>

        <div class="form-group">
            <label>Identifier</label>
            <input
                type="text"
                name="identifier"
                value="${model.identifier}"
                readonly
            />
        </div>

        <div class="form-group">
            <label>Model Name</label>
            <input
                type="text"
                name="name"
                value="${model.name}"
            />
        </div>

        <input
            type="hidden"
            name="status"
            value="${model.status}"
        />

        <div class="btn-group">
            <button type="submit" class="btn">
                Update Model
            </button>

            <a
                href="${pageContext.request.contextPath}/model/list"
                class="btn back-btn"
            >
                Back
            </a>
        </div>

    </form>
</div>

</body>
</html>