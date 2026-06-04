<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
<title>Node List</title>

<style>
body {
    font-family: Arial;
    background: #F6F7F9;
    padding: 30px;
}

table {
    width: 100%;
    background: white;
    border-collapse: collapse;
    border-radius: 12px;
}

th {
    background: #2B2B2B;
    color: white;
    padding: 12px;
}

td {
    padding: 10px;
    border-bottom: 1px solid #E5E7EB;
    text-align: center;
}

a {
    color: #2B2B2B;
    font-weight: 600;
    text-decoration: none;
}
.top-right-actions {
            display: flex;
            justify-content: flex-end;
            margin-bottom: 15px;
            gap: 10px;
        }

.top-right-actions button {
            padding: 6px 14px;
            border: none;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 500;
            cursor: pointer;
        }

        .add-btn {
            background: #2B2B2B;
            color: white;
        }

        .add-btn:hover {
            background: #111111;
        }

        .back-btn {
            background: #E5E7EB;
            color: #111827;
        }

        .back-btn:hover {
            background: #D1D5DB;
        }
</style>
</head>

<body>

<h2 style="text-align:center;">Node List</h2>

<div class="top-right-actions">
    <a href="/node/add">
        <button class="add-btn">Add Node</button>
    </a>
    <a href="/">
        <button class="back-btn">Back</button>
    </a>
</div>

<table>
<tr>
<th>Path</th>
<th>Identifier</th>
<th>Roles</th>
<th>Actions</th>
</tr>

<c:forEach var="node" items="${nodes}">
<tr>
<td>${node.path}</td>
<td>${node.identifier}</td>
<td>${node.roles}</td>
<td>
    <a href="/node/get?identifier=${node.identifier}">Edit</a> |
    <a href="/node/delete?identifier=${node.identifier}">Delete</a>
</td>
</tr>
</c:forEach>

</table>

</body>
</html>
