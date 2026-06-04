<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <style>
        body {
            margin: 0;
            font-family: 'Segoe UI', sans-serif;
            background: #eef2f7;
        }

        .header {
            background: #1e88e5;
            padding: 15px 25px;
            color: white;
            font-size: 20px;
            font-weight: bold;
        }

        .container {
            display: flex;
            justify-content: center;
            padding: 30px;
        }

        .card {
            width: 400px;
            background: white;
            padding: 22px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 18px;
            color: #333;
            font-size: 18px;
        }

        .error {
            text-align: center;
            color: #dc3545;
            font-size: 12px;
            margin-bottom: 10px;
        }

        .form-group {
            margin-bottom: 14px;
        }

        label {
            font-size: 12px;
            color: #555;
            font-weight: 500;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-top: 5px;
            border-radius: 6px;
            border: 1px solid #ddd;
            font-size: 13px;
        }

        select[multiple] {
            height: 90px;
        }

        input:focus, select:focus {
            border-color: #1e88e5;
            outline: none;
            box-shadow: 0 0 0 2px rgba(30,136,229,0.2);
        }

        .readonly {
            background: #f5f5f5;
            cursor: not-allowed;
        }

        .btn {
            width: 100%;
            padding: 11px;
            margin-top: 10px;
            border-radius: 6px;
            border: none;
            background: #28a745;
            color: white;
            font-weight: 600;
            cursor: pointer;
        }

        .btn:hover {
            opacity: 0.9;
        }

        .back {
            display: block;
            text-align: center;
            margin-top: 10px;
            font-size: 12px;
            color: #1e88e5;
            text-decoration: none;
        }

        .back:hover {
            text-decoration: underline;
        }

        small {
            font-size: 11px;
            color: #777;
        }
    </style>
</head>

<body>

<div class="header">
    POS - Edit Node
</div>

<div class="container">

    <div class="card">

        <h2>Update Node</h2>

        <c:if test="${empty node}">
            <div class="error">
                Node not found
            </div>
        </c:if>

        <c:if test="${not empty node}">

            <form:form method="post"
                       action="/node/update"
                       modelAttribute="node">

                <div class="form-group">
                    <label>Node Identifier</label>
                    <form:input path="identifier" cssClass="readonly" readonly="true"/>
                </div>

                <div class="form-group">
                    <label>Node Path</label>
                    <form:input path="path" required="true"/>
                </div>

                <div class="form-group">
                    <label>Roles</label>
                    <form:select path="roles" multiple="true">
                        <form:options items="${roles}"
                                      itemValue="identifier"
                                      itemLabel="identifier"/>
                    </form:select>
                    <small>Hold Ctrl/Cmd to select multiple roles</small>
                </div>

                <button type="submit" class="btn">
                    Update Node
                </button>

            </form:form>

            <a href="/node/list" class="back">
                ← Back to Node List
            </a>

        </c:if>

    </div>

</div>

</body>
</html>