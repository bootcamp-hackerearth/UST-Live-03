<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Node</title>

    <style>
        body {
            font-family: "Segoe UI", Arial, sans-serif;
            background-color: #f4f6fb;
            background-image:
                radial-gradient(circle at top right, rgba(78,115,223,0.12), transparent 45%),
                radial-gradient(circle at bottom left, rgba(78,115,223,0.08), transparent 45%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .card {
            width: 540px;
            background: #ffffff;
            border-radius: 16px;
            padding: 32px;
            box-shadow: 0 20px 45px rgba(0,0,0,0.12);
        }

        h3 {
            text-align: center;
            color: #4e73df;
            margin-bottom: 28px;
            font-weight: 700;
        }

        .form-group {
            display: grid;
            grid-template-columns: 150px 1fr;
            align-items: start;
            gap: 14px;
            margin-bottom: 18px;
        }

        label {
            font-size: 14px;
            font-weight: 600;
            color: #444;
            margin-top: 6px;
        }

        input {
            padding: 10px 12px;
            border-radius: 8px;
            border: 1px solid #ccd3e0;
            font-size: 14px;
            background: #f9fafc;
        }

        input:focus {
            outline: none;
            border-color: #4e73df;
            box-shadow: 0 0 0 3px rgba(78,115,223,0.25);
            background: #ffffff;
        }

        .role-box {
            border: 1px solid #ccd3e0;
            border-radius: 8px;
            padding: 10px;
            background: #f9fafc;
        }

        .role-item {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 6px;
            font-size: 14px;
            color: #333;
        }

        .role-item input[type="checkbox"] {
            accent-color: #4e73df;
        }

        button {
            width: 100%;
            margin-top: 20px;
            padding: 10px;
            background: linear-gradient(135deg, #4e73df, #2e59d9);
            border: none;
            border-radius: 6px;
            color: #ffffff;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            box-shadow: 0 6px 14px rgba(78,115,223,0.35);
        }

        .back-btn {
            display: block;
            width: 60%;
            margin: 12px auto 0 auto;
            padding: 8px;
            text-align: center;
            border-radius: 6px;
            border: 2px solid #4e73df;
            color: #4e73df;
            font-size: 13px;
            font-weight: 600;
            text-decoration: none;
            background: transparent;
        }

        .back-btn:hover {
            background-color: #4e73df;
            color: #ffffff;
        }
    </style>
    <script>
        function validateRoles() {
            const roles = document.querySelectorAll('input[name="roles[]"]:checked');
            if (roles.length === 0) {
                alert("Please select at least one role");
                return false; // stop form submission
            }
            return true; // allow submit
        }
    </script>
</head>
<body>
<div class="card">
    <h3>Add Node</h3>
    <form action="/node/add" method="post" onsubmit="return validateRoles();">
        <div class="form-group">
            <label>Node Name</label>
            <input type="text" name="identifier" placeholder="Enter node name" required>
        </div>
        <div class="form-group">
            <label>Path</label>
            <input type="text" name="path" placeholder="Enter node path" required>
        </div>
        <div class="form-group">
            <label>Roles</label>
            <div class="role-box">
                <c:forEach items="${roles}" var="role">
                    <div class="role-item">
                        <input type="checkbox"
                               name="roles[]"
                               value="${role.identifier}" />
                        <span>${role.identifier}</span>
                    </div>
                </c:forEach>
            </div>
        </div>
        <button type="submit">Add Node</button>
        <a href="/node/list" class="back-btn">
            ← Back to Node List
        </a>
    </form>
</div>
</body>
</html>