<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Warehouse</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 30px;
        }

        .container {
            max-width: 600px;
            margin: auto;
        }

        h2 {
            margin-bottom: 20px;
            text-align: center;
        }

        label {
            display: block;
            margin-top: 12px;
            font-weight: bold;
        }

        input[type="text"] {
            width: 100%;
            padding: 8px;
            margin-top: 5px;
            box-sizing: border-box;
        }

        .btn {
            padding: 8px 14px;
            text-decoration: none;
            border-radius: 4px;
            color: white;
            border: none;
            cursor: pointer;
            margin-top: 20px;
        }

        .btn-save {
            background-color: #28a745;
        }

        .btn-back {
            background-color: #6c757d;
            margin-left: 10px;
        }

        /* Toggle Switch */
        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 24px;
            margin-top: 10px;
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
            transition: 0.4s;
            border-radius: 30px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: 0.4s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #28a745;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }

        .actions {
            text-align: center;
            margin-top: 25px;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Edit Warehouse</h2>

    <form method="post"
          action="${pageContext.request.contextPath}/warehouse/update">

        <!-- Hidden ID -->
        <input type="hidden" name="id" value="${warehouse.id}" />

        <!--  Name -->
        <label>Name</label>
        <input type="text"
               name="identifier"
               value="${warehouse.identifier}"
               readonly />

        <!-- Code -->
        <label>Code</label>
        <input type="text"
               name="code"
               value="${warehouse.code}"/>

        <!--  Location -->
        <label>Location</label>
        <input type="text"
               name="location"
               value="${warehouse.location}"/>

        <!--  Status -->
        <label>Status</label>
        <label class="switch">
            <input type="checkbox"
                   name="status"
                   value="true"
                   <c:if test="${warehouse.status}">checked</c:if>>
            <span class="slider"></span>
        </label>

        <!--  Buttons -->
        <div class="actions">
            <button type="submit" class="btn btn-save">
                Update
            </button>

            <a href="${pageContext.request.contextPath}/warehouse/list"
               class="btn btn-back">
                Cancel
            </a>
        </div>

    </form>

</div>

</body>
</html>