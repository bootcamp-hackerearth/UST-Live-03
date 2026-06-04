<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
<meta charset="UTF-8">
<title>Display Nodes</title>
<link rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

<style>
    body {
        font-family: 'Poppins', Arial, sans-serif;
        background-color: #f4f6fb;
        padding: 40px;
    }

    .button-row {
        width: 60%;
        margin: 20px auto 0;
        display: flex;
        justify-content: space-between;
    }

    .nav-btn {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        background-color: #4e73df;
        color: #ffffff;
        padding: 10px 18px;
        border-radius: 6px;
        font-weight: 500;
        font-size: 14px;
        text-decoration: none;
        transition: all 0.3s ease;
    }

    .nav-btn:hover {
        background-color: #2e59d9;
    }

    .nav-btn i {
        color: #ffffff;
        font-size: 16px;
    }

    table {
        width: 60%;
        margin: auto;
        border-collapse: collapse;
        background-color: #ffffff;
        border-radius: 10px;
        overflow: hidden;
        box-shadow: 0 4px 12px rgba(0,0,0,0.06);
    }

    th {
        background-color: #4e73df;
        color: #ffffff;
        padding: 14px;
        text-align: left;
        font-size: 14px;
        font-weight: 500;
    }

    td {
        padding: 14px;
        border-bottom: 1px solid #e6e9f5;
        font-size: 14px;
        color: #555;
        vertical-align: middle;
    }

    tr:nth-child(even) {
        background-color: #f8f9ff;
    }

    tr:hover {
        background-color: #eef3ff;
    }

    .action-btn {
        padding: 6px 12px;
        border-radius: 4px;
        font-size: 13px;
        font-weight: 500;
        text-decoration: none;
        margin-right: 6px;
        display: inline-block;
    }

    .update {
        background-color: #4e73df;
        color: #ffffff;
    }

    .update:hover {
        background-color: #2e59d9;
    }

    .delete {
        background-color: #e74a3b;
        color: #ffffff;
    }

    .delete:hover {
        background-color: #c0392b;
    }
</style>
</head>

<body>

<table>
    <tr>
        <th>ID</th>
        <th>Identifier</th>
        <th>Path</th>
        <th>Roles</th>
        <th>Actions</th>
    </tr>

    <c:forEach items="${nodes}" var="node">
        <tr>
            <td>${node.id}</td>
            <td>${node.identifier}</td>
            <td>${node.path}</td>
            <td>
                <c:forEach items="${node.roles}" var="role" varStatus="s">
                    ${role}<c:if test="${!s.last}">, </c:if>
                </c:forEach>
            </td>
            <td>
                <a href="/node/get?identifier=${node.identifier}"
                   class="action-btn update">
                    Update
                </a>
                <a href="/node/delete?identifier=${node.identifier}"
                   class="action-btn delete"
                   onclick="return confirm('Are you sure you want to delete this node?');">
                    Delete
                </a>
            </td>
        </tr>
    </c:forEach>
</table>
<div class="button-row">
    <a href="/" class="nav-btn">
        <i class="bi bi-house-fill"></i>
        Home
    </a>
    <a href="/node/add" class="nav-btn">
        + Add Node
    </a>
</div>
</body>
</html>