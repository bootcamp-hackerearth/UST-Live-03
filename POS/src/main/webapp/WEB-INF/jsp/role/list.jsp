<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Roles Management</title>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
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

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }

        h2 {
            color: #4B2E2B;
            font-weight: 600;
            margin: 0;
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

        th,
        td {
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
            background-color: #4B2E2B;
            color: #FFF8F0;
        }

        .btn-delete:hover {
            background-color: #3a2421;
        }

        .register-btn {
            background-color: #4B2E2B;
            color: #FFF8F0;
            border: none;
            padding: 8px 18px;
            font-size: 14px;
            font-weight: 600;
            border-radius: 8px;
            text-decoration: none;
            display: inline-block;
            margin-right: 10px;
        }

        .register-btn:hover {
            background-color: #3a2421;
            color: #FFF8F0;
        }

        .btn-secondary {
            background-color: #4B2E2B;
            color: #FFF8F0;
            border: none;
            padding: 8px 18px;
            font-size: 14px;
            font-weight: 600;
            border-radius: 8px;
            text-decoration: none;
            display: inline-block;
        }

        .btn-secondary:hover {
            background-color: #3a2421;
            color: #FFF8F0;
        }

        .empty-message {
            text-align: center;
            color: #8d3c36;
            font-weight: 600;
            padding: 20px;
        }

        @media (max-width: 900px) {
            .container {
                width: 95%;
            }

            table {
                font-size: 12px;
            }

            .page-header {
                flex-direction: column;
                gap: 15px;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="page-header">
        <h2>Roles Management</h2>
        <div>
            <a href="/role/add" class="register-btn">
                <i class="fa-solid fa-plus"></i> Add Role
            </a>
            <a href="/" class="btn-secondary">
                Home
            </a>
        </div>
    </div>
    <c:if test="${empty roles}">
        <div class="empty-message">
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
</div>
</body>
</html>