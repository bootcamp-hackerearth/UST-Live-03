<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Models</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
        }

        .container {
            width: 40%;
            margin: 60px auto;
            background: #fff;
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
            margin-bottom: 6px;
            display: block;
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
            border: none;
            color: white;
            cursor: pointer;
            font-size: 14px;
        }

        .save {
            background-color: #28a745;
        }

        .cancel {
            background-color: #6c757d;
            text-decoration: none;
            padding: 8px 15px;
            margin-left: 10px;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Add Models</h2>

    <form:form action="${pageContext.request.contextPath}/models/add"
               method="post"
               modelAttribute="modelsDto">

        <label>Model Name</label>
        <form:input path="identifier"/>

        <button type="submit" class="btn save">Save</button>
        <a href="${pageContext.request.contextPath}/models/list" class="btn cancel">
            Cancel
        </a>
    </form:form>
</div>
</body>
</html>