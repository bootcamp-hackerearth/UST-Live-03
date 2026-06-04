<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Brand</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card-container {
            position: relative;
            width: 430px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        .back-icon {
            position: absolute;
            top: 16px;
            left: 16px;
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            color: #4b6cb7;
            text-decoration: none;
            font-weight: 600;
            background: rgba(75, 108, 183, 0.08);
            border-radius: 50%;
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            font-size: 13px;
            font-weight: 500;
            color: #333;
            margin-bottom: 6px;
            display: block;
        }

        input, textarea, select {
            width: 100%;
            padding: 11px 14px;
            border-radius: 8px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        textarea {
            resize: none;
        }

        .btn-row {
            display: flex;
            gap: 10px;
            margin-top: 15px;
        }

        .btn-submit, .btn-reset {
            flex: 1;
            padding: 13px;
            border-radius: 10px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            border: none;
        }

        .btn-submit {
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
        }

        .btn-reset {
            background: #e0e0e0;
        }

        .error-message {
            margin-bottom: 16px;
            padding: 10px;
            background: rgba(220, 53, 69, 0.12);
            border: 1px solid #dc3545;
            color: #dc3545;
            border-radius: 8px;
            text-align: center;
            font-size: 13px;
            font-weight: 500;
        }
    </style>
</head>

<body>

<div class="card-container">
    <a href="/brand/list" class="back-icon">←</a>

    <h2>Add Brand</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form:form method="post"
               action="/brand/add"
               modelAttribute="brandDto"
               enctype="multipart/form-data">

        <div class="form-group">
            <label>Brand Name</label>
            <form:input path="identifier" placeholder="Enter brand name"/>
        </div>

        <div class="form-group">
            <label>Description</label>
            <form:textarea path="description" rows="3" placeholder="Description"/>
        </div>

        <div class="btn-row">
            <button type="reset" class="btn-reset">Reset</button>
            <button type="submit" class="btn-submit">Save</button>
        </div>

    </form:form>
</div>

</body>
</html>