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
</style>
</head>

<body>

<h2 style="text-align:center;">Node List</h2>
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

 .add-node-btn {
            padding: 10px 16px;
            background: #2B2B2B;
            color: white;
            border-radius: 6px;
            font-weight: 600;
            text-decoration: none;
            display: inline-block;
        }

        .add-node-btn:hover {
            background: #444;
        }
        .back-btn {
                    background: #E5E7EB;
                    color: #111827;
                }

                .back-btn:hover {
                    background: #D1D5DB;
                }
                .btn-secondary {
                                    padding: 10px 16px;
                                    background: #2B2B2B;
                                    color: white;
                                    border-radius: 6px;
                                    font-weight: 600;
                                    text-decoration: none;
                                }

                                .btn-secondary:hover {
                                    background: #444;
                                }

</style>
</head>

<body>

<h2 style="text-align:center;">Node List</h2>


<div style="text-align:right; margin-bottom:15px;">
    <a href="/node/add" class="add-node-btn">
        + Add Node
    </a>

    <a href="/">
            <button class="btn-secondary">Back</button>
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


