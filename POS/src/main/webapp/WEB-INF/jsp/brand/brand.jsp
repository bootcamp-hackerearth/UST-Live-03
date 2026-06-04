<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Brand</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            margin: 0;
            padding: 0;
        }

        .container {
            width: 40%;
            margin: 60px auto;
            background: #ffffff;
            padding: 25px;
            border-radius: 6px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #333;
        }

        label {
            font-weight: bold;
            display: block;
            margin-bottom: 6px;
        }

        input[type="text"] {
            width: 100%;
            padding: 8px;
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        .btn {
            padding: 8px 15px;
            border-radius: 4px;
            font-size: 14px;
            cursor: pointer;
            border: none;
        }

        .update {
            background-color: #007bff;
            color: white;
        }

        .cancel {
            background-color: #6c757d;
            color: white;
            text-decoration: none;
            padding: 8px 15px;
            margin-left: 10px;
        }

        .btn:hover {
            opacity: 0.9;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Edit Brand</h2>

    <form:form method="post"
               action="/brand/update"
               modelAttribute="brandDto">

    <form:hidden path="identifier"/>


        <!-- Brand Name -->
        <label for="name">Brand Name</label>
        <form:input path="identifier"/>



        <br/>

        <button type="submit" class="btn update">Update</button>
        <a href="${pageContext.request.contextPath}/brand/list" class="btn cancel">
            Cancel
        </a>
    </form:form>
</div>

</body>
</html>