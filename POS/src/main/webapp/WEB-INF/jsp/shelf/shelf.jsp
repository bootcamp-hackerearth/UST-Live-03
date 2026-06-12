<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Shelf</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            background-color: #f1f5f9;
            background-image: radial-gradient(#cbd5e1 0.5px, transparent 0.5px);
            background-size: 24px 24px;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            color: #334155;
        }

        .container {
            width: 100%;
            max-width: 420px;
            background: #ffffff;
            padding: 40px;
            border-radius: 16px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1),
                        0 10px 10px -5px rgba(0,0,0,0.04);
        }

        h2 {
            text-align: center;
            margin-bottom: 30px;
            font-weight: 700;
            font-size: 24px;
            color: #0f172a;
        }

        .input-group {
            margin-bottom: 20px;
        }

        label {
            font-size: 14px;
            font-weight: 600;
            margin-bottom: 8px;
            display: block;
            color: #64748b;
        }

        input, select {
            width: 100%;
            padding: 12px 16px;
            border-radius: 10px;
            border: 1px solid #cbd5e1;
            background: #ffffff;
            color: #1e293b;
            font-size: 15px;
        }

        input[readonly] {
            background-color: #f8fafc;
            cursor: not-allowed;
            color: #64748b;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #6366f1;
            box-shadow: 0 0 0 4px rgba(99,102,241,0.1);
        }

        button {
            width: 100%;
            padding: 14px;
            margin-top: 10px;
            border-radius: 10px;
            border: none;
            background: #1e293b;
            color: white;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
        }

        button:hover {
            background: #0f172a;
        }

        .link-btn {
            display: block;
            text-align: center;
            margin-top: 25px;
            color: #64748b;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
        }

        .link-btn:hover {
            color: #4f46e5;
        }

        .bottom-error {
            margin-top: 20px;
            padding: 12px;
            text-align: center;
            border-radius: 8px;
            background: #fff1f2;
            border: 1px solid #fecdd3;
            color: #be123c;
            font-size: 14px;
            font-weight: 500;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Edit Shelf</h2>

    <form:form action="${pageContext.request.contextPath}/shelf/update"
               method="post"
               modelAttribute="shelf">

        <!-- Hidden ID -->
        <form:hidden path="id"/>

        <!-- Shelf Name (Read Only) -->
        <div class="input-group">
            <label>Shelf Name</label>
            <form:input path="identifier" readonly="true"/>
        </div>

        <!-- Status -->
        <div class="input-group">
            <label>Status</label>
            <form:select path="status">
                <form:option value="true">Active</form:option>
                <form:option value="false">Inactive</form:option>
            </form:select>
        </div>

        <button type="submit">Save Changes</button>

        <a href="${pageContext.request.contextPath}/shelf/list"
           class="link-btn">
            ← Back to Shelfs
        </a>

    </form:form>

    <c:if test="${not empty message}">
        <div class="bottom-error">
            ${message}
        </div>
    </c:if>

</div>

</body>
</html>