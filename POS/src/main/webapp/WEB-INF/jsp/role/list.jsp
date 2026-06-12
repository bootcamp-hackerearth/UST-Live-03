<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <meta charset="UTF-8">
    <title>Roles Management</title>
    <style>
        * {
            box-sizing: border-box;
        }

        body {
            font-family: "Segoe UI", Tahoma, sans-serif;
            background-color: #FFF8F0;
            margin: 0;
            padding: 40px;
        }

        .container {
            width: 70%;
            margin: auto;
            background: #ffffff;
            padding: 30px;
            border-radius: 18px;
            box-shadow: 0 18px 35px rgba(75, 46, 43, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #4B2E2B;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            border-radius: 12px;
            overflow: hidden;
        }

        thead {
            background-color: #4B2E2B;
            color: #FFF8F0;
        }

        th, td {
            padding: 14px;
            text-align: center;
        }

        th {
            font-size: 13px;
            letter-spacing: 0.6px;
            text-transform: uppercase;
        }

        tbody tr:nth-child(even) {
            background-color: #fff3eb;
        }

        tbody tr:hover {
            background-color: #f1e3dc;
        }

        .btn {
            padding: 6px 14px;
            border-radius: 6px;
            display: inline-block;
            font-size: 13px;
            font-weight: 600;
            text-decoration: none;
        }

        .btn-edit {
            background-color: #4B2E2B;
            color: #FFF8F0;
        }

        .btn-edit:hover {
            background-color: #3a2421;
        }

        .btn-delete {
            background-color: #4b2e2b;
            color: #FFF8F0;
        }

        .btn-delete:hover {
            background-color: #3a2421;
        }

        .top-bar {
            margin-top: 20px;
            text-align: center;
        }

        .home-btn {
            display: inline-block;
            background-color: #6b4a46;
            color: #FFF8F0;
            padding: 8px 18px;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.3s ease;
        }

        .home-btn:hover {
            background-color: #4B2E2B;
        }

        .add-role {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 20px;
            background-color: #4B2E2B;
            color: #FFF8F0;
            border-radius: 10px;
            font-weight: 600;
            text-decoration: none;
        }

        .add-role:hover {
            background-color: #3a2421;
        }

        @media (max-width: 900px) {
            .container {
                width: 95%;
            }

            table {
                font-size: 12px;
            }
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 22px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            inset: 0;
            background-color: #cfc4bb;
            border-radius: 20px;
            transition: 0.4s;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 16px;
            width: 16px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            border-radius: 50%;
            transition: 0.4s;
        }

        input:checked + .slider {
                    background-color: #6b4a46;
        }

        input:checked + .slider:before {
                    transform: translateX(24px);
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Roles Management</h2>
    <c:if test="${empty roles}">
        <div style="text-align:center; color:#8d3c36; font-weight:600;">
            No roles found
        </div>
    </c:if>
    <c:if test="${not empty roles}">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Role</th>
                <th>Description</th>
                <th>Status</th>
                <th>Edit</th>
                <th>Delete</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="role" items="${roles}">
                <tr>
                    <td>${role.id}</td>
                    <td>${role.identifier}</td>
                    <td>${role.description}</td>
                    <td class="text-center">
                        <form action="${pageContext.request.contextPath}/role/toggleStatus" method="post">
                            <input type="hidden" name="identifier" value="${role.identifier}"/>
                                <label class="switch">
                                    <input type="checkbox"
                                        onchange="this.form.submit()"
                                        <c:if test="${role.status}">checked</c:if>>
                                    <span class="slider"></span>
                                </label>
                        </form>
                        <small class="text-primary">
                            <c:choose>
                                <c:when test="${role.status}">Active</c:when>
                                <c:otherwise>Inactive</c:otherwise>
                            </c:choose>
                        </small>
                    </td>
                    <td>
                        <a class="btn btn-edit"
                            href="/role/get?identifier=${role.identifier}"
                            title="Edit Role">
                            <i class="fa-solid fa-pen"></i>
                        </a>
                    </td>
                    <td>
                        <a class="btn btn-delete"
                            href="/role/delete?identifier=${role.identifier}"
                            title="Delete Role"
                            onclick="return confirm('Are you sure you want to delete this role?');">
                            <i class="fa-solid fa-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
    <div class="top-bar">
        <a href="/" class="home-btn">Home</a>
    </div>
    <div style="text-align:center;">
        <a href="/role/add" class="add-role">+ Add New Role</a>
    </div>
</div>
</body>
</html>