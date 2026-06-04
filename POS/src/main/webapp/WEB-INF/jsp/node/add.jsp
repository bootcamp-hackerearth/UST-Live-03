<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Node</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: Arial, sans-serif;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            position: relative;
            width: 420px;
            background: #ffffff;
            padding: 30px 35px;
            border-radius: 12px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.25);
        }

        /* Back button */
        .back-btn {
            position: absolute;
            top: 16px;
            left: 16px;
            width: 34px;
            height: 34px;
            display: flex;
            align-items: center;
            justify-content: center;
            text-decoration: none;
            font-size: 18px;
            font-weight: bold;
            color: #4b6cb7;
            background: rgba(75, 108, 183, 0.1);
            border-radius: 50%;
            transition: all 0.25s ease;
        }

        .back-btn:hover {
            background: #4b6cb7;
            color: #fff;
            transform: translateX(-2px);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #4b6cb7;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 13px;
            font-weight: 600;
            color: #333;
        }

        input, select {
            width: 100%;
            padding: 10px 12px;
            margin-bottom: 15px;
            border-radius: 8px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        select[multiple] {
            height: 120px;
        }
        .success-message {
                    margin-bottom: 15px;
                    padding: 10px;
                    background: rgba(40, 167, 69, 0.12);
                    border: 1px solid #28a745;
                    color: #28a745;
                    border-radius: 8px;
                    font-size: 13px;
                    text-align: center;
                    font-weight: 500;
                }


        button {
            width: 100%;
            padding: 11px;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
        }

        button:hover {
            opacity: 0.9;
        }

        .cancel-link {
            display: block;
            margin-top: 10px;
            text-align: center;
            color: #6c757d;
            text-decoration: none;
            font-size: 14px;
        }

        .cancel-link:hover {
            text-decoration: underline;
        }

        .footer {
            margin-top: 20px;
            text-align: center;
            font-size: 12px;
            color: #888;
        }
    </style>
</head>

<body>

<div class="container">

    <!-- ✅ Back Button -->
    <a href="/node/list" class="back-btn" aria-label="Go back">&#8592;</a>

    <h2>Add Node</h2>

    <!-- ✅ ERROR MESSAGE (shows when node already exists) -->

<c:if test="${not empty message}">
    <div class="success-message">
        ${message}
    </div>
</c:if>


    <!-- ✅ Spring Form -->
    <form:form
            action="/node/add"
            method="post"
            modelAttribute="nodeDto">

        <label>Node Name</label>
        <form:input path="identifier" required="true"/>

        <label>Node Path</label>
        <form:input path="path" required="true"/>

        <label>Additional Roles</label>
        <form:select path="roles" multiple="true">
            <form:options
                    items="${roles}"
                    itemValue="identifier"
                    itemLabel="identifier"/>
        </form:select>

        <small style="color:#777;">
            Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple
        </small>

        <button type="submit" style="margin-top:15px;">
            Add Node
        </button>

        <a href="/node/list" class="cancel-link">Cancel</a>

    </form:form>

    <div class="footer">
        POS Management System
    </div>

</div>

</body>
</html>
