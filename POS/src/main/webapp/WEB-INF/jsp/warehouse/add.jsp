<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Warehouse</title>

    <style>
        body {
            font-family: Arial;
            background: #f4f7f6;
        }

        .card {
            width: 420px;
            margin: 100px auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        h2 {
            text-align: center;
            color: #28a745;
        }

        label {
            display: block;
            margin-top: 15px;
        }

        input {
            width: 100%;
            padding: 10px;
            margin-top: 5px;
        }

        .btn {
            margin-top: 20px;
            width: 100%;
            padding: 10px;
            background: #28a745;
            color: white;
            border: none;
            cursor: pointer;
        }

        .error {
            color: red;
            text-align: center;
            margin-bottom: 10px;
        }

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
    </style>
</head>

<body>

<a href="/warehouse/list" class="back-btn">← Back</a>

<div class="card">

    <h2>Add Warehouse</h2>

    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <form:form action="/warehouse/add" method="post" modelAttribute="warehouseDto">

        <!-- Identifier -->
        <label>Identifier</label>
        <form:input path="identifier" required="true"/>

        <!-- Name -->
        <label>Name</label>
        <form:input path="name" required="true"/>

        <!-- Country -->
        <label>Country</label>
        <form:input path="country" required="true"/>

        <!-- Region -->
        <label>Region</label>
        <form:input path="region" required="true"/>

        <!-- Address -->
        <label>Address</label>
        <form:input path="address" required="true"/>

        <!-- PHONE -->
                <label>Phone Number</label>
                <form:input path="phoneNo"
                pattern="[0-9]{10}"
                title="Enter a valid 10 digit phone number"
                required="true"/>
                <form:errors path="phoneNo" cssClass="field-error"/>

        <input type="submit" value="Save" class="btn"/>

    </form:form>

</div>

</body>
</html>