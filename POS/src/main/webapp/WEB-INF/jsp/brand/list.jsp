<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Brand List</title>

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

        .header-left {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        h2 {
            margin: 0;
        }

        /* ===== Buttons ===== */
        .home-btn {
            background-color: #6c757d;
            color: #fff;
            padding: 8px 14px;
            border-radius: 5px;
            text-decoration: none;
            font-size: 14px;
        }

        .home-btn:hover {
            background-color: #5a6268;
        }

        .add-btn {
            background-color: #0d6efd;
            color: #fff;
            padding: 10px 16px;
            border-radius: 5px;
            text-decoration: none;
            font-size: 14px;
              margin-left: 10px;
        }

        .add-btn:hover {
            background-color: #0b5ed7;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 14px;
            border-bottom: 1px solid #e0e0e0;
            vertical-align: middle;
            text-align: center;
        }

        th {
            background-color: #0d6efd;
            color: #fff;
        }

        /* ===== Status Toggle ===== */
        td.status-cell form {
            display: flex;
            justify-content: center;
            margin: 0;
        }

        .switch {
            position: relative;
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
            inset: 0;
            background-color: #dc3545;
            border-radius: 30px;
            cursor: pointer;
            transition: 0.4s;
        }

        .slider:before {
            content: "";
            position: absolute;
            width: 18px;
            height: 18px;
            left: 3px;
            bottom: 3px;
            background-color: #fff;
            border-radius: 50%;
            transition: 0.4s;
        }

        input:checked + .slider {
            background-color: #28a745;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }

        /* ===== Action Buttons ===== */
        .edit-btn {
            background-color: #ffc107;
            color: #000;
            padding: 6px 12px;
            border-radius: 4px;
            text-decoration: none;
            font-size: 13px;
            margin-right: 6px;
            display: inline-block;
        }

        .edit-btn:hover {
            background-color: #e0a800;
        }

        .delete-btn {
            background-color: #dc3545;
            color: #fff;
            padding: 6px 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 13px;
        }

        .delete-btn:hover {
            opacity: 0.9;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="header">
        <div class="header-left">
            <h2>Brand List</h2>
        </div>

        <div class="header-right">
            <a href="${pageContext.request.contextPath}/" class="home-btn">
                Home
            </a>

            <a href="${pageContext.request.contextPath}/brand/add" class="add-btn">
                + Add Brand
            </a>
        </div>
    </div>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Brand Name</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach var="brand" items="${brands}">
            <tr>
                <td>${brand.id}</td>
                <td>${brand.identifier}</td>

                <!-- Status Toggle -->
                <td class="status-cell">
                    <form method="get"
                          action="${pageContext.request.contextPath}/brand/toggle">

                        <input type="hidden"
                               name="identifier"
                               value="${brand.identifier}" />

                        <label class="switch">
                            <input type="checkbox"
                                   name="status"
                                   value="true"
                                   onchange="this.form.submit()"
                                   <c:if test="${brand.status}">checked</c:if> />
                            <span class="slider"></span>
                        </label>
                    </form>
                </td>

                <!-- Actions -->
                <td>
                    <a href="${pageContext.request.contextPath}/brand/update?identifier=${brand.identifier}"
                       class="edit-btn">
                        Edit
                    </a>

                    <form method="get"
                          action="${pageContext.request.contextPath}/brand/delete"
                          style="display:inline;"
                          onsubmit="return confirm('Are you sure you want to delete this brand?');">

                        <input type="hidden"
                               name="id"
                               value="${brand.id}" />

                        <button type="submit" class="delete-btn">
                            Delete
                        </button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>

</body>
</html>
