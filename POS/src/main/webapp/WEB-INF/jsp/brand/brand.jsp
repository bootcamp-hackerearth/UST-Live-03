<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Brand</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
            margin: 0;
            padding: 0;
        }

        /* BACK BUTTON */
        .back-btn {
            position: fixed;
            top: 20px;
            left: 20px;
            padding: 8px 14px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
        }

        .back-btn:hover {
            background: #5a6268;
        }

        /* CARD */
        .card {
            width: 420px;
            margin: 100px auto;
            background: #fff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 3px 12px rgba(0,0,0,0.1);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-top: 15px;
            font-size: 14px;
            font-weight: bold;
        }

        input, textarea ,select{
            width: 100%;
            padding: 10px;
            margin-top: 6px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 14px;
        }

        textarea {
            resize: vertical;
            min-height: 80px;
        }

        /* STATUS TOGGLE */
        .switch {
            position: relative;
            display: inline-block;
            width: 50px;
            height: 26px;
            margin-top: 10px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            cursor: pointer;
            background-color: #dc3545;
            border-radius: 26px;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            transition: 0.4s;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 20px;
            width: 20px;
            left: 3px;
            bottom: 3px;
            background: white;
            border-radius: 50%;
            transition: 0.4s;
        }

        input:checked + .slider {
            background-color: #28a745;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }

        /* BUTTON */
        .btn {
            margin-top: 25px;
            width: 100%;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 15px;
            cursor: pointer;
        }

        .btn:hover {
            background-color: #0056b3;
        }

        /* ERROR */
        .error {
            background: #fee2e2;
            color: #b91c1c;
            padding: 10px;
            border-radius: 6px;
            text-align: center;
            margin-bottom: 10px;
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/brand/list" class="back-btn">← Back</a>

<div class="card">

    <h2>Edit Brand</h2>

    <!-- ERROR -->
    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/brand/update"
               method="post"
               modelAttribute="brand">

        <!-- IDENTIFIER (Brand Name) -->
        <label>Brand Name</label>
        <form:input path="identifier" readonly="true"/>

        <!-- DESCRIPTION -->
        <label>Description</label>
        <form:textarea path="description"/>

        <!-- STATUS -->
        <label>Status</label>
        <form:select path="status">
            <form:option value="true">Active</form:option>
            <form:option value="false">Inactive</form:option>
        </form:select>



        <!-- SUBMIT -->
        <input type="submit" value="Update Brand" class="btn"/>

    </form:form>

</div>

</body>
</html>