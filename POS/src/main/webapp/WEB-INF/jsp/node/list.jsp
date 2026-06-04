<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Nodes List</title>

    <style>
        body {
            margin: 0;
            padding: 30px;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
        }

        h2 {
            text-align: center;
            margin-bottom: 24px;
            color: #1e293b;
            font-size: 22px;
            font-weight: 700;
            letter-spacing: 0.4px;
        }

        .container {
            max-width: 1000px;
            margin: auto;
            background: #ffffff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        .btn {
            padding: 6px 11px;
            border-radius: 6px;
            border: none;
            font-size: 11px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            color: #ffffff;
            background-color: #1e293b;
            transition: background 0.2s ease, box-shadow 0.2s ease;
        }

        .btn:hover {
            background-color: #0f172a;
            box-shadow: 0 6px 18px rgba(15, 23, 42, 0.35);
        }

        .btn-delete {
            background-color: #dc2626;
        }

        .btn-delete:hover {
            background-color: #b91c1c;
        }

        .btn-edit {
            background-color: #16a34a;
            margin-right: 10px;
        }

        .btn-edit:hover {
            background-color: #15803d;
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        th {
            background-color: #1e293b;
            color: #ffffff;
            padding: 12px;
            font-size: 14px;
            text-align: left;
        }

        td {
            padding: 12px;
            font-size: 14px;
            color: #334155;
            border-bottom: 1px solid #e2e8f0;
        }

        tr:nth-child(even) {
            background-color: #f8fafc;
        }

        tr:hover {
            background-color: #f1f5f9;
        }

        .path {
            font-family: monospace;
            color: #475569;
        }
    </style>
</head>

<body>

<h2>Nodes List</h2>

<div class="container">

    <div class="top-bar">
        <a href="${pageContext.request.contextPath}/" class="btn">
            Home
        </a>

        <a href="${pageContext.request.contextPath}/node/add" class="btn">
            + Add Node
        </a>
    </div>

    <table>
        <tr>
            <th>ID</th>
            <th>Identifier</th>
            <th>Path</th>
            <th>Roles</th>
            <th>Actions</th>
        </tr>

        <c:forEach var="node" items="${nodes}">
            <tr>
                <td>${node.id}</td>
                <td>${node.identifier}</td>
                <td class="path">${node.path}</td>
                <td>${node.roles}</td>
                <td>
                    <a href="/node/get?identifier=${node.identifier}"
                       class="btn btn-edit">
                        Edit
                    </a>
                    <a href="/node/delete?identifier=${node.identifier}"
                       class="btn btn-delete">
                        Delete
                    </a>
                </td>
            </tr>
        </c:forEach>
    </table>

</div>

</body>
</html>