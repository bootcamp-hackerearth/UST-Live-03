<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Shelf</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            padding: 20px;
        }

        h2 {
            margin-bottom: 20px;
        }

        form {
            background: #fff;
            padding: 25px;
            width: 400px;
            border-radius: 6px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-weight: bold;
        }

        input[type="text"] {
            width: 100%;
            padding: 8px;
            margin-bottom: 18px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        /* Toggle Switch */
        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 24px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            cursor: pointer;
            inset: 0;
            background-color: #dc3545;
            transition: .4s;
            border-radius: 34px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: .4s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #28a745;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }

        .btn {
            padding: 8px 15px;
            background-color: #28a745;
            border: none;
            color: #fff;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
        }

        .btn:hover {
            background-color: #218838;
        }
    </style>
</head>

<body>

<h2>Add Shelf</h2>

<form:form method="post"
           action="${pageContext.request.contextPath}/shelf/add"
           modelAttribute="shelfDto">

    <label>Identifier</label>
    <form:input path="identifier" />

        <label>Status</label>
        <form:select path="status">
            <form:option value="true">ACTIVE</form:option>
            <form:option value="false">INACTIVE</form:option>
        </form:select>


    <br/><br/>

    <button class="btn" type="submit">Save</button>

</form:form>

</body>
</html>