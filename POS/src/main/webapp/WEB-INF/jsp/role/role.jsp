<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Update Role</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            padding: 30px;
        }

        .container {
            width: 75%;
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
            letter-spacing: 0.4px;
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

        input {
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

        input:focus {
            outline: none;
            border-color: #1e293b;
            box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.15);
        }

        .btn {
            padding: 10px 16px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            color: #ffffff;
            background-color: #1e293b;
            transition: background 0.2s ease, box-shadow 0.2s ease;
        }

        .btn:hover {
            background-color: #0f172a;
            box-shadow: 0 6px 18px rgba(15, 23, 42, 0.35);
        }

        .btn-group {
            margin-top: 20px;
            text-align: center;
        }

        .back-btn {
            margin-left: 12px;
            text-decoration: none;
            display: inline-block;
            background-color: #e2e8f0;
            color: #1e293b;
        }

        .back-btn:hover {
            background-color: #cbd5e1;
            color: #0f172a;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Update Role</h2>

    <form action="${pageContext.request.contextPath}/role/update" method="post">

        <div class="form-group">
            <label>ID</label>
            <input
                type="text"
                name="id"
                value="${role.id}"
                readonly
            />
        </div>

        <div class="form-group">
            <label>Identifier</label>
            <input
                type="text"
                name="identifier"
                value="${role.identifier}"
                readonly
            />
        </div>

        <div class="form-group">
            <label>Description</label>
            <input
                type="text"
                name="description"
                value="${role.description}"
                required
            />
        </div>

        <div class="btn-group">
            <button type="submit" class="btn">
                Update Role
            </button>

            <a href="${pageContext.request.contextPath}/role/list"
               class="btn back-btn">
                Back
            </a>
        </div>

    </form>
</div>

</body>
</html>