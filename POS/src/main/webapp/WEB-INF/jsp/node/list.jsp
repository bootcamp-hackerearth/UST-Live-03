<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="stylesheet"
    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <meta charset="UTF-8">
    <title>Nodes Management</title>
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

        a {
            text-decoration: none;
            font-weight: 600;
            font-size: 14px;
        }

        .id-link {
            color: #4B2E2B;
        }

        .btn {
            padding: 6px 14px;
            border-radius: 6px;
            display: inline-block;
            font-size: 13px;
            font-weight: 600;
        }

        .btn-delete {
            background-color: #8d3c36;
            color: white;
        }

        .btn-delete:hover {
            background-color: #702f2a;
        }

        .btn-edit {
            background-color: #4B2E2B;
            color: #FFF8F0;
        }

        .btn-edit:hover {
            background-color: #3a2421;
        }

        .add-node {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 20px;
            background-color: #4B2E2B;
            color: #FFF8F0;
            border-radius: 10px;
            font-weight: 600;
        }

        .add-node:hover {
            background-color: #3a2421;
        }

        .top-bar {
            margin-bottom: 20px;
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

        @media (max-width: 900px) {
            .container {
                width: 95%;
            }

            table {
                font-size: 12px;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Nodes Management</h2>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Path</th>
            <th>Roles</th>
            <th>Edit</th>
            <th>Delete</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${nodes}" var="node">
            <tr>
                <td>
                    <a class="id-link" href="/node/get?identifier=${node.identifier}">
                        ${node.id}
                    </a>
                </td>
                <td>${node.identifier}</td>
                <td>${node.path}</td>
                <td>${node.roles}</td>
                <td>
                    <a class="btn btn-edit"
                        href="/node/get?identifier=${node.identifier}"
                        title="Edit Node">
                        <i class="fa-solid fa-pen"></i>
                    </a>
                </td>
                <td>
                    <a class="btn btn-delete"
                       href="/node/delete?identifier=${node.identifier}"
                       title="Delete Node"
                       onclick="return confirm('Are you sure you want to delete this node?');">
                       <i class="fa-solid fa-trash"></i>
                    </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <div class="top-bar mt-3">
        <a href="/" class="home-btn">Home</a>
    </div>
    <div style="text-align:center;">
        <a href="/node/add" class="add-node">+ Add New Node</a>
    </div>
</div>
</body>
</html>