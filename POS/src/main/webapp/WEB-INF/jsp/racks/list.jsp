<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Rack List</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            margin: 0;
            padding: 0;
        }

        .container {
            width: 85%;
            margin: 30px auto;
            background: #ffffff;
            padding: 25px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        h2 { margin: 0; }

        .add-btn {
            background-color: #0d6efd;
            color: #fff;
            padding: 10px 16px;
            border-radius: 5px;
            text-decoration: none;
            font-size: 14px;
        }

        /*  Home button (added) */
        .home-btn {
            background-color: #6c757d;
            color: #fff;
            padding: 10px 16px;
            border-radius: 5px;
            text-decoration: none;
            font-size: 14px;
            margin-right: 10px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 14px;
            border-bottom: 1px solid #e0e0e0;
            text-align: center;
        }

        th {
            background-color: #0d6efd;
            color: #fff;
        }

        /* Toggle Switch */
        .switch {
            position: relative;
            width: 46px;
            height: 24px;
            display: inline-block;
        }

        .switch input { opacity: 0; }

        .slider {
            position: absolute;
            inset: 0;
            background-color: #dc3545;
            border-radius: 30px;
            transition: 0.4s;
            cursor: pointer;
        }

        .slider:before {
            content: "";
            position: absolute;
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            border-radius: 50%;
            transition: 0.4s;
        }

        input:checked + .slider {
            background-color: #28a745;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }

        .edit-btn {
            background-color: #ffc107;
            color: #000;
            padding: 6px 12px;
            border-radius: 4px;
            text-decoration: none;
            font-size: 13px;
        }

        .delete-btn {
            background-color: #dc3545;
            color: #fff;
            padding: 6px 12px;
            border-radius: 4px;
            border: none;
            cursor: pointer;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="header">
        <h2>Rack List</h2>
        <div>
            <a href="${pageContext.request.contextPath}/" class="home-btn">
                Home
            </a>
            <a href="${pageContext.request.contextPath}/racks/add" class="add-btn">
                + Add Rack
            </a>
        </div>
    </div>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Identifier</th>
            <th>Shelves</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach var="rack" items="${racks}">
            <tr>
                <td>${rack.id}</td>
                <td>${rack.identifier}</td>
                <td>${rack.shelf}</td>

                <!-- Status -->
                <td>
                    <form method="get"
                          action="${pageContext.request.contextPath}/racks/toggle">
                        <input type="hidden" name="identifier"
                               value="${rack.identifier}" />

                        <label class="switch">
                            <input type="checkbox"
                                   name="status"
                                   onchange="this.form.submit()"
                                   <c:if test="${rack.status}">checked</c:if> />
                            <span class="slider"></span>
                        </label>
                    </form>
                </td>

                <!-- Actions -->
                <td>
                    <a href="${pageContext.request.contextPath}/racks/update?identifier=${rack.identifier}"
                       class="edit-btn">Edit</a>

                    <form method="get"
                          action="${pageContext.request.contextPath}/racks/delete"
                          style="display:inline;"
                          onsubmit="return confirm('Delete this rack?');">
                        <input type="hidden" name="id" value="${rack.id}" />
                        <button type="submit" class="delete-btn">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>

</body>
</html>