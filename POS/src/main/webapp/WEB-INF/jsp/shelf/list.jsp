<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Shelf List</title>

    <style>
        body {
            font-family: "Segoe UI", Arial, sans-serif;
            background: #F6F7F9;
            padding: 30px;
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #111827;
        }

        .table-card {
            background: #fff;
            border-radius: 16px;
            padding: 20px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.06);
            border: 1px solid #E5E7EB;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 12px;
            text-align: center;
        }

        th {
            background: #2B2B2B;
            color: white;
        }

        tr:nth-child(even) {
            background: #F9FAFB;
        }

        .top-right-actions {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            margin-bottom: 15px;
        }

        .btn {
            padding: 6px 14px;
            border-radius: 8px;
            border: none;
            font-weight: 600;
            cursor: pointer;
        }

        .add-btn {
            background: #2B2B2B;
            color: white;
        }

        .edit-btn {
            background: #2B2B2B;
            color: white;
        }

        .delete-btn {
            background: #B91C1C;
            color: white;
        }

        a {
            text-decoration: none;
        }

        .msg {
            background: #DCFCE7;
            color: #166534;
            padding: 10px 14px;
            border-radius: 10px;
            margin-bottom: 15px;
            text-align: center;
            width: fit-content;
            margin-left: auto;
            margin-right: auto;
        }


        .toggle-container {
            cursor: pointer;
            display: inline-block;
        }

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
            background-color: #d1d5db;
            border-radius: 24px;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            transition: 0.4s;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            border-radius: 50%;
            transition: 0.4s;
        }


        input:not(:checked) + .slider {
        background-color: #dc2626;
        }

        input:checked + .slider {
        background-color: #22c55e;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }

    </style>
</head>

<body>

<h2>Shelf List</h2>

<div class="top-right-actions">
    <a href="/shelf/add">
        <button class="btn add-btn">Add Shelf</button>
    </a>
    <a href="/">
        <button class="btn">Home</button>
    </a>
</div>

<div id="content"></div>

<c:if test="${not empty message}">
    <div class="msg">${message}</div>
</c:if>

<div class="table-card">
    <table>
        <tr>
            <th>ID</th>
            <th>Shelf Name</th>
            <th>Status</th>
            <th>Action</th>
        </tr>

        <c:forEach items="${shelves}" var="shelf">
            <tr>
                <td>${shelf.id}</td>
                <td>${shelf.identifier}</td>
                <td>
                   <div class="toggle-container"
                                         onclick="window.location.href='${pageContext.request.contextPath}/shelf/toggle?identifier=${shelf.identifier}'">

                                        <label class="switch">
                                            <input type="checkbox" ${shelf.status ? "checked" : ""} disabled>
                                            <span class="slider"></span>
                                        </label>

                                    </div>
                                </td>
                <td>
                    <a href="/shelf/get?identifier=${shelf.identifier}">
                        <button class="btn edit-btn">Edit</button>
                    </a>
                    <a href="/shelf/delete?identifier=${shelf.identifier}"
                       onclick="return confirm('Delete this shelf?')">
                        <button class="btn delete-btn">Delete</button>
                    </a>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>

</body>
</html>