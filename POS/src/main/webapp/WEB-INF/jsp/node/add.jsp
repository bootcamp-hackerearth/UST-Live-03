<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Management | Add Node</title>
    <style>
        body {
            margin: 0; padding: 0;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: #F4F5F7;
            min-height: 100vh;
            display: flex; align-items: center; justify-content: center;
        }

        .container {
            width: 100%;
            max-width: 450px;
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .brand-header {
            background: #0B3C5D;
            padding: 25px;
            color: #FFFFFF;
            text-align: center;
        }

        .brand-header h1 {
            margin: 0;
            font-size: 22px;
            font-weight: 600;
            letter-spacing: 0.8px;
        }

        .form-body {
            padding: 35px 40px;
        }

        h2 {
            color: #1F2937;
            font-size: 18px;
            margin-bottom: 20px;
            text-align: center;
            font-weight: 600;
        }

        .form-group { margin-bottom: 20px; }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #4B5563;
            font-size: 13px;
        }

        input[type="text"], select {
            width: 100%;
            padding: 12px;
            border-radius: 8px;
            border: 1px solid #E5E7EB;
            box-sizing: border-box;
            font-size: 14px;
            font-family: inherit;
            outline: none;
            transition: border-color 0.2s;
        }

        input:focus, select:focus {
            border-color: #0B3C5D;
            background: #F9FAFB;
        }

        .role-select { height: 100px; }

        .btn-submit {
            width: 100%;
            padding: 12px;
            border-radius: 8px;
            border: none;
            background: #0B3C5D;
            color: white;
            cursor: pointer;
            font-weight: 600;
            font-size: 15px;
            margin-top: 10px;
            transition: opacity 0.2s;
        }

        .btn-submit:hover { opacity: 0.9; }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #6B7280;
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
        }

        .back-link:hover {
            color: #0B3C5D;
            text-decoration: underline;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="brand-header">
        <h1>POS Management</h1>
    </div>

    <div class="form-body">
        <h2>Create New Node</h2>

        <form:form action="${pageContext.request.contextPath}/node/add" method="post" modelAttribute="node">
            <div class="form-group">
                <label>Node Name</label>
                <input type="text" name="identifier" placeholder="e.g. User Management" required autofocus />
            </div>

            <div class="form-group">
                <label>Path</label>
                <input type="text" name="path" placeholder="e.g. /user/list" required />
            </div>

            <div class="form-group">
                <label>Authorized Roles (Ctrl + Click)</label>
                <form:select path="roles" multiple="true" class="role-select">
                    <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
                </form:select>
            </div>

            <button type="submit" class="btn-submit">Save Node</button>
            <a href="${pageContext.request.contextPath}/node/list" class="back-link">← Cancel and Return</a>
        </form:form>
    </div>
</div>

</body>
</html>