<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Models List</title>

    <style>
        body {
            font-family: Arial, Helvetica, sans-serif;
            background-color: #f4f6f8;
            margin: 0;
            padding: 0;
        }

        .container {
            width: 85%;
            margin: 30px auto;
            background: #ffffff;
            padding: 20px;
            border-radius: 6px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        h2 {
            margin-bottom: 20px;
            color: #333;
        }

        .add-btn {
            display: inline-block;
            margin-bottom: 15px;
            padding: 8px 14px;
            background-color: #28a745;
            color: #ffffff;
            text-decoration: none;
            border-radius: 4px;
            font-size: 14px;
        }

        .add-btn:hover {
            background-color: #218838;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        table th,
        table td {
            padding: 12px;
            border-bottom: 1px solid #ddd;
            text-align: left;
        }

        table th {
            background-color: #007bff;
            color: white;
        }

        table tr:hover {
            background-color: #f1f1f1;
        }

        .actions a {
            margin-right: 10px;
            padding: 5px 10px;
            text-decoration: none;
            font-size: 13px;
            border-radius: 4px;
        }

        .edit {
            background-color: #ffc107;
            color: #000;
        }

        .delete {
            background-color: #dc3545;
            color: #fff;
        }

        .edit:hover {
            background-color: #e0a800;
        }

        .delete:hover {
            background-color: #c82333;
        }

        /* ================= Toggle Switch CSS ================= */
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
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: #ccc;
            transition: 0.4s;
            border-radius: 24px;
        }

        .slider::before {
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

        input:checked + .slider::before {
            transform: translateX(22px);
        }

        input:focus + .slider {
            box-shadow: 0 0 1px #28a745;
        }
        /* ================= Home Button ================= */
        .btn-home {
            display: inline-block;
            padding: 8px 14px;
            margin-right: 10px;
            background-color: #6c757d; /* gray */
            color: #ffffff;
            text-decoration: none;
            border-radius: 4px;
            font-size: 14px;
        }

        .btn-home:hover {
            background-color: #5a6268;
        }


        /* ===================================================== */
    </style>
</head>

<body>
<div class="container">
    <h2>Models List</h2>

    <!--  HOME + ADD BUTTONS -->
    <div style="margin-bottom: 15px;">
        <a href="${pageContext.request.contextPath}/" class="btn btn-home">
            Home
        </a>

    <a href="${pageContext.request.contextPath}/models/add" class="add-btn">
        + Add Models
    </a>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Models</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach var="modelObj" items="${Models}">
            <tr>
                <td>${modelObj.id}</td>
                <td>${modelObj.identifier}</td>

                <td>
                    <form method="get"
                          action="${pageContext.request.contextPath}/models/toggle">
                        <input type="hidden"
                               name="identifier"
                               value="${modelObj.identifier}" />

                        <label class="switch">
                            <input type="checkbox"
                                   name="status"
                                   value="true"
                                   onchange="this.form.submit()"
                                   <c:if test="${modelObj.status}">checked</c:if> />
                            <span class="slider"></span>
                        </label>
                    </form>
                </td>

                <td class="actions">
                    <a href="${pageContext.request.contextPath}/models/save?id=${modelObj.id}" class="edit">
                        Edit
                    </a>
                    <a href="${pageContext.request.contextPath}/models/delete?id=${modelObj.id}" class="delete">
                        Delete
                    </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>