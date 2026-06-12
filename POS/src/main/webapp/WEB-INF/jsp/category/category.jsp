<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Category</title>

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

        input, select {
            width: 100%;
            padding: 11px 14px;
            border-radius: 8px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        .btn-submit {
            margin-top: 12px;
            width: 100%;
            padding: 13px;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
        }

        .btn-cancel {
            margin-top: 10px;
            width: 100%;
            padding: 11px;
            background: #f1f1f1;
            color: #333;
            border: none;
            border-radius: 10px;
            font-size: 14px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
        }

        .alert-danger {
            background: #fdecea;
            color: #c62828;
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 15px;
            text-align: center;
        }
    </style>
</head>

<body>

<div class="card-container">

    <h2>Edit Category</h2>

    <c:if test="${empty categoryDto}">
        <div class="alert-danger">Category not found</div>
    </c:if>

    <c:if test="${not empty categoryDto}">
        <form:form action="/category/update"
                   method="post"
                   modelAttribute="categoryDto">

            <form:hidden path="id"/>

            <!-- Category (Readonly) -->
            <div class="form-group">
                <label>Category</label>
                <form:input path="identifier" readonly="true"/>
            </div>

            <!-- Super Category Dropdown -->
            <div class="form-group">
                <label>Super Category</label>
                <form:select path="superCategory">
                    <form:option value="">-- Select Super Category --</form:option>
                    <form:options items="${categories}"
                                  itemValue="identifier"
                                  itemLabel="identifier"/>
                </form:select>
            </div>

            <input type="submit" value="Update Category" class="btn-submit"/>
            <a href="/category/list" class="btn-cancel">Cancel</a>

        </form:form>
    </c:if>

</div>

</body>
</html>