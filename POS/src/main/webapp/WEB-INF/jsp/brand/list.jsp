<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Brand List</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            font-family: "Segoe UI", Tahoma, Arial, sans-serif;
            background: linear-gradient(120deg, #e0eafc, #cfdef3);
            padding: 30px;
            margin: 0;
        }

        .container {
            max-width: 900px;
            margin: auto;
            background: #ffffff;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.12);
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        h2 {
            margin: 0;
            color: #2c3e50;
        }

        .header-actions {
            display: flex;
            gap: 10px;
        }

        .home-btn {
            padding: 10px 18px;
            background: #6c757d;
            color: #ffffff;
            text-decoration: none;
            border-radius: 6px;
            font-weight: 600;
        }

        .home-btn:hover {
            background: #5c636a;
        }

        .add-btn {
            padding: 10px 18px;
            background: #198754;
            color: #ffffff;
            text-decoration: none;
            border-radius: 6px;
            font-weight: 600;
        }

        .add-btn:hover {
            background: #157347;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 12px;
            border-bottom: 1px solid #ddd;
            text-align: center;
        }

        th {
            background-color: #f8f9fa;
            font-weight: 700;
        }

        tr:hover {
            background-color: #f1f3f5;
        }

        .toggle-switch {
            position: relative;
            width: 50px;
            height: 26px;
            display: inline-block;
        }

        .toggle-switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            cursor: pointer;
            inset: 0;
            background-color: #dc3545;
            transition: 0.3s;
            border-radius: 30px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 20px;
            width: 20px;
            left: 3px;
            bottom: 3px;
            background-color: #ffffff;
            transition: 0.3s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #198754;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }

        .btn {
            padding: 6px 12px;
            border-radius: 6px;
            border: none;
            cursor: pointer;
            font-weight: 600;
            font-size: 13px;
        }

        .btn-edit {
            background-color: #0d6efd;
            color: #ffffff;
        }

        .btn-delete {
            background-color: #dc3545;
            color: #ffffff;
        }

        .action-form {
            display: inline;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="header">
        <h2>Brand List</h2>
        <div class="header-actions">
            <a href="${pageContext.request.contextPath}/" class="home-btn">
                 Home
            </a>
            <a href="${pageContext.request.contextPath}/brand/add" class="add-btn">
                + Add New Brand
            </a>
        </div>
    </div>

    <table>
        <thead>
        <tr>
            <th>Brand Name</th>
            <th>Description</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
        </thead>

        <tbody>
        <c:if test="${empty brands}">
            <tr>
                <td colspan="4">No brands available</td>
            </tr>
        </c:if>

        <c:forEach items="${brands}" var="brand">
            <tr>
                <td>${brand.identifier}</td>
                <td>${brand.description}</td>

                <td>
                    <form method="get"
                          action="${pageContext.request.contextPath}/brand/toggleStatus">
                        <input type="hidden" name="identifier" value="${brand.identifier}"/>

                        <label class="toggle-switch">
                            <input type="checkbox"
                                   onchange="this.form.submit()"
                                   <c:if test="${brand.status}">checked</c:if>>
                            <span class="slider"></span>
                        </label>
                    </form>
                </td>

                <td>
                    <form class="action-form"
                          method="get"
                          action="${pageContext.request.contextPath}/brand/get">
                        <input type="hidden" name="identifier" value="${brand.identifier}"/>
                        <button type="submit" class="btn btn-edit">Edit</button>
                    </form>

                    <form class="action-form"
                          method="get"
                          action="${pageContext.request.contextPath}/brand/delete"
                          onsubmit="return confirm('Are you sure you want to delete this brand?');">
                        <input type="hidden" name="identifier" value="${brand.identifier}"/>
                        <button type="submit" class="btn btn-delete">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>

</body>
</html>