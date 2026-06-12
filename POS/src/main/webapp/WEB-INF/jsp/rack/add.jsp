<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Rack</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #F6F7F9;
            padding: 30px;
        }

        h2 {
            text-align: center;
            color: #111827;
            margin-bottom: 20px;
        }

        .form-container {
            max-width: 420px;
            margin: auto;
            background: #ffffff;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 6px 14px rgba(0,0,0,0.08);
        }

        label {
            font-weight: 600;
            margin-top: 15px;
            display: block;
        }

        input, select {
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

        .save-btn {
            flex: 1;
            background: #2B2B2B;
            color: white;
            border: none;
            padding: 10px;
            border-radius: 6px;
            font-weight: 600;
        }

        .back-btn {
            flex: 1;
            background: #E5E7EB;
            color: #111827;
            padding: 10px;
            text-align: center;
            text-decoration: none;
            border-radius: 6px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<h2>Add Rack</h2>

<div class="form-container">

    <form:form action="/rack/add" method="post" modelAttribute="rackDto">

        <label>Rack Name</label>
        <form:input path="identifier" required="true"/>

        <label>Shelf</label>
        <form:select path="shelves" multiple="true" required="true">
            <form:option value="" label="-- Select Shelf --"/>
            <form:options
                items="${shelves}"
                itemValue="identifier"
                itemLabel="identifier"/>
        </form:select>

        <label>Status</label>
        <form:select path="status">
            <form:option value="ACTIVE">ACTIVE</form:option>
            <form:option value="INACTIVE">INACTIVE</form:option>
        </form:select>

        <div class="btn-group">
            <button type="submit" class="save-btn">Save</button>
            <a href="/rack/list" class="back-btn">Back</a>
        </div>

    </form:form>

</div>

</body>
</html>