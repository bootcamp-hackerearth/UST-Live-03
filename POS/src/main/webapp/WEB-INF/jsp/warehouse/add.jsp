<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Warehouse</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #F6F7F9;
            padding: 30px;
        }

        .top-actions {
            display: flex;
            justify-content: flex-end;
            margin-bottom: 15px;
        }

        .container {
            max-width: 420px;
            margin: auto;
            background: white;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 6px 14px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        label {
            font-weight: 600;
            margin-top: 15px;
            display: block;
        }

        input {
            width: 100%;
            padding: 10px;
            margin-top: 6px;
            border-radius: 6px;
            border: 1px solid #D1D5DB;
        }

        .btn-group {
            display: flex;
            gap: 10px;
            margin-top: 25px;
        }

        .btn {
            flex: 1;
            padding: 10px;
            border-radius: 6px;
            font-weight: 600;
            border: none;
            cursor: pointer;
            text-align: center;
        }

        .save-btn {
            background: #2B2B2B;
            color: white;
        }

        .back-btn {
            background: #E5E7EB;
            color: #111827;
            text-decoration: none;
            line-height: 36px;
        }
    </style>
</head>

<body>

<div class="top-actions">
    <a href="/warehouse/list" class="btn back-btn">Back</a>
</div>

<div class="container">
    <h2>Add Warehouse</h2>

    <form:form action="/warehouse/add" method="post" modelAttribute="warehouseDto">

        <label>Warehouse Name</label>
        <form:input path="identifier" required="true"/>

        <label>Region</label>
        <form:input path="region" required="true"/>

        <label>Country</label>
        <form:input path="country" required="true"/>

        <label>Location</label>
        <form:input path="location" required="true"/>

        <label>Contact Name</label>
        <form:input path="contactName"/>

        <label>Contact Number</label>
        <form:input path="contactNumber"/>

        <div class="btn-group">
            <button type="submit" class="btn save-btn">Save</button>
            <a href="/warehouse/list" class="btn back-btn">Cancel</a>
        </div>

    </form:form>
</div>

</body>
</html>
``