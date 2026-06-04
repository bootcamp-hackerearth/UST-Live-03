<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Node</title>

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
            width: 450px;
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

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 13px;
            font-weight: 600;
            color: #334155;
        }

        input, select {
            width: 100%;
            padding: 12px;
            border-radius: 6px;
            border: 1px solid #cbd5e1;
            font-size: 14px;
            margin-bottom: 18px;
        }

        input[readonly] {
            background-color: #f8fafc;
            color: #64748b;
        }

        select[multiple] {
            height: 120px;
        }

        .btn-group {
            display: flex;
            gap: 12px;
        }

        .btn {
            flex: 1;
            padding: 12px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            color: #ffffff;
            background-color: #1e293b;
            text-align: center;
            text-decoration: none;
        }

        .btn:hover {
            background-color: #0f172a;
        }

        .btn-cancel {
            background-color: #64748b;
        }

        .btn-cancel:hover {
            background-color: #475569;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Update Node</h2>

    <form action="/node/update" method="post">

        <label>Node ID</label>
        <input type="text" name="id" value="${node.id}" readonly />

        <label>Identifier</label>
        <input type="text" name="identifier" value="${node.identifier}" readonly />

        <label>Role Path</label>
        <input type="text" name="path" value="${node.path}" required />

        <label>Roles</label>
        <select name="roles" multiple>
            <c:forEach items="${roles}" var="role">
                <option value="${role.identifier}">
                    ${role.identifier}
                </option>
            </c:forEach>
        </select>

        <div class="btn-group">
            <button type="submit" class="btn">Update Node</button>
            <a href="/node/list" class="btn btn-cancel">Cancel</a>
        </div>

    </form>
</div>

</body>
</html>