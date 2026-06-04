<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Create Node</title>

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
            width: 420px;
            padding: 36px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        h2 {
            text-align: center;
            margin-bottom: 26px;
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
            background-color: #ffffff;
        }

        select {
            min-height: 110px;
        }

        input:focus,
        select:focus {
            outline: none;
            border-color: #1e293b;
            box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.15);
        }

        .btn-group {
            display: flex;
            gap: 12px;
            margin-top: 26px;
        }

        .btn {
            flex: 1;
            padding: 12px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            text-align: center;
        }

        .btn-save {
            background-color: #1e293b;
            color: #ffffff;
        }

        .btn-save:hover {
            background-color: #0f172a;
        }

        .btn-back {
            background-color: #e2e8f0;
            color: #1e293b;
            text-decoration: none;
        }

        .btn-back:hover {
            background-color: #cbd5e1;
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
    <h2>Create Node</h2>

    <c:if test="${not empty message}">
                    <div class="message">
                        ${message}
                    </div>
                </c:if>

    <form method="post">

        <div class="form-group">
            <label>Identifier</label>
            <input type="text"
                   name="identifier"
                   placeholder="Enter identifier"
                   required>
        </div>

        <div class="form-group">
            <label>Path</label>
            <input type="text"
                   name="path"
                   placeholder="/admin/dashboard"
                   required>
        </div>

        <div class="form-group">
            <label>Roles</label>
            <select name="roles" multiple required>
                <c:forEach items="${roles}" var="role">
                    <option value="${role.identifier}">
                        ${role.identifier}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="btn-group">
            <button type="submit" class="btn btn-save">
                Save Node
            </button>

            <a href="${pageContext.request.contextPath}/node/list"
               class="btn btn-back">
                Back
            </a>
        </div>

    </form>
</div>

</body>
</html>