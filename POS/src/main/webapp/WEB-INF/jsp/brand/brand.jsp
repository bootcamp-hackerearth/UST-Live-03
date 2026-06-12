<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Brand</title>

    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background: #f7f8fc;
            padding: 30px;
        }

        .container {
            max-width: 600px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
        }

        label {
            font-weight: 600;
            margin-top: 15px;
            display: block;
        }

        input, textarea, select {
            width: 100%;
            padding: 10px;
            margin-top: 6px;
            border: 1px solid #ccc;
            border-radius: 6px;
        }

        input[readonly] {
            background: #f1f3f8;
            cursor: not-allowed;
        }

        .btn-group {
            margin-top: 25px;
            display: flex;
            justify-content: space-between;
        }

        .btn {
            padding: 10px 20px;
            border-radius: 6px;
            font-weight: 600;
            border: none;
            cursor: pointer;
        }

        .btn-save {
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
        }

        .btn-cancel {
            background: #ccc;
            color: #333;
            text-decoration: none;
            line-height: 38px;
            padding: 0 20px;
            border-radius: 6px;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Edit Brand</h2>

    <form action="/brand/update" method="post">

        <!-- Identifier (hidden) -->
        <input type="hidden" name="identifier" value="${brandDto.identifier}" />

        <!-- Brand Name (non editable) -->
        <label>Brand Name</label>
        <input type="text" value="${brandDto.identifier}" readonly />

        <!-- Description (editable) -->
        <label>Description</label>
        <textarea name="description" rows="4">${brandDto.description}</textarea>

        <!-- Status (editable) -->
        <label>Status</label>
        <select name="status">
            <option value="true" ${brandDto.status ? 'selected' : ''}>Active</option>
            <option value="false" ${!brandDto.status ? 'selected' : ''}>Inactive</option>
        </select>

        <div class="btn-group">
            <button type="submit" class="btn btn-save">Update</button>
            <a href="/brand/list" class="btn-cancel">Cancel</a>
        </div>

    </form>
</div>

</body>
</html>