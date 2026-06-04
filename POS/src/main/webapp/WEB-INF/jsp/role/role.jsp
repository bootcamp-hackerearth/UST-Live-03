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
            background-color: #f4f6fb;
            background-image:
                radial-gradient(circle at top right, rgba(78,115,223,0.15), transparent 45%),
                radial-gradient(circle at bottom left, rgba(111,66,193,0.12), transparent 45%);
            min-height: 100vh;
            padding: 30px;
        }

        .container {
            width: 75%;
            margin: auto;
            background: #ffffff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.10);
            border-top: 6px solid #4e73df; /* SAME AS HOME */
        }

        h2 {
            text-align: center;
            margin-bottom: 24px;
            color: #4e73df; /* HOME BLUE */
            font-size: 22px;
            font-weight: 600;
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
            border-color: #4e73df;
            box-shadow: 0 0 0 3px rgba(78,115,223,0.25);
        }

        .btn {
            padding: 10px 16px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            color: #ffffff;
            background-color: #4e73df;
            min-width: 140px;
            transition: background 0.2s ease, box-shadow 0.2s ease;
        }

        .btn:hover {
            background-color: #2e59d9;
            box-shadow: 0 6px 16px rgba(78,115,223,0.4);
        }

        .btn-back {
            padding: 10px 16px;
            border-radius: 6px;
            border: 2px solid #4e73df;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            color: #4e73df;
            background-color: transparent;
            text-decoration: none;
            min-width: 140px;
            text-align: center;
            transition: background 0.2s ease;
        }

        .btn-back:hover {
            background-color: #e8effd;
        }

        .btn-group {
            margin-top: 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Update Role</h2>
    <form action="${pageContext.request.contextPath}/role/update" method="post">
        <div class="form-group">
            <label>ID</label>
            <input type="text" name="id" value="${role.id}" readonly />
        </div>
        <div class="form-group">
            <label>Identifier</label>
            <input type="text" name="identifier" value="${role.identifier}" readonly />
        </div>
        <div class="form-group">
            <label>Description</label>
            <input type="text" name="description" value="${role.description}" required />
        </div>
        <div class="btn-group">
            <a href="${pageContext.request.contextPath}/role/list" class="btn-back">
                ← Back
            </a>
            <button type="submit" class="btn">
                Update Role
            </button>
        </div>
    </form>
</div>
</body>
</html>