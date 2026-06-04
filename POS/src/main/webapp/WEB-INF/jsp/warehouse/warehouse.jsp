<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Warehouse</title>

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
        }

        h2 {
            text-align: center;
            color: #007bff;
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
            background: #007bff;
            color: white;
            border: none;
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

    <h2>Edit Warehouse</h2>

    <form:form action="/warehouse/update" method="post" modelAttribute="warehouseDto">

        <label>Identifier</label>
        <form:input path="identifier" readonly="true"/>

        <label>Name</label>
        <form:input path="name" required="true"/>

        <label>Country</label>
        <form:input path="country" required="true"/>

        <label>Region</label>
        <form:input path="region" required="true"/>

        <label>Address</label>
        <form:input path="address" required="true"/>

        <label>Phone Number</label>
                <form:input path="phoneNo"
                      pattern="[0-9]{10}"
                      title="Enter a valid 10 digit phone number"
                      required="true"/>
                <form:errors path="phoneNo" cssClass="field-error"/>

        <input type="submit" value="Update" class="btn"/>

    </form:form>

</div>

</body>
</html>