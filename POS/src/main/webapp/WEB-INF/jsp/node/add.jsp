<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Node</title>

    <style>
        body {
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: "Segoe UI", sans-serif;
            background: radial-gradient(circle at 20% 30%, rgba(255,122,0,0.15), transparent 40%),
                        radial-gradient(circle at 80% 70%, rgba(255,72,0,0.15), transparent 40%),
                        linear-gradient(135deg, #1f1f1f, #3a2e2a);
        }

        .card {
            width: 420px;
            background: rgba(255,255,255,0.12);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
            border: 1px solid rgba(255,255,255,0.15);
        }

        h4 {
            text-align: center;
            margin-bottom: 25px;
            color: #fff;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            font-size: 13px;
            color: #ddd;
            margin-bottom: 6px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: 1px solid rgba(255,255,255,0.2);
            background: rgba(255,255,255,0.1);
            color: #fff;
            font-size: 14px;
        }

        input::placeholder {
            color: #ccc;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #ff7a00;
            box-shadow: 0 0 6px rgba(255,122,0,0.3);
        }

        select[multiple] {
            height: 110px;
            border-radius: 12px;
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: none;
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: white;
            margin-top: 10px;
            cursor: pointer;
        }

        .btn-submit:hover {
            opacity: 0.9;
        }

        .btn-back {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: none;
            background: rgba(255,255,255,0.2);
            color: #fff;
            margin-top: 10px;
            text-decoration: none;
            text-align: center;
            display: block;
        }

        .btn-back:hover {
            background: rgba(255,255,255,0.3);
        }

        .message-box {
            margin-top: 12px;
            font-size: 13px;
            color: #ffb3b3;
            text-align: center;
        }

        .footer {
            margin-top: 12px;
            text-align: center;
            font-size: 12px;
            color: #ddd;
        }
    </style>
</head>

<body>

<div class="card">

    <h4>Add New Node</h4>

    <c:if test="${not empty node}">
        <div style="text-align:center; color:#b6ffb6; margin-bottom:10px;">
            ${node}
        </div>
    </c:if>

    <form:form method="post" action="/node/add" modelAttribute="nodeDto">

        <div class="form-group">
            <label>Node Name</label>
            <form:input path="identifier" placeholder="Enter node name"/>
        </div>

        <div class="form-group">
            <label>Path Name</label>
            <form:input path="path" placeholder="Enter path name"/>
        </div>

        <div class="form-group">
            <label>Roles</label>
            <form:select path="roles" multiple="true">
                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
            </form:select>
        </div>

        <button type="submit" class="btn-submit">Add Node</button>

        <a href="/node/list" class="btn-back">Back</a>

    </form:form>

    <div class="footer">
        POS Management System
    </div>

</div>

</body>
</html>