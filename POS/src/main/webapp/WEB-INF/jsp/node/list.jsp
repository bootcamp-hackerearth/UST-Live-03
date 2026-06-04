<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Node List</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }

        html, body {
            height: 100%;
        }

        body {
            background-color: #f5f6fc;
            color: #667eea;
        }

        .header {
            height: 50px;
            display: flex;
            align-items: center;
            padding: 0 20px;
            background-color: #f5f6fc;
            box-shadow: 0 0 7px rgba(75,108,183,0.25);
        }

        .hamburger {
            font-size: 22px;
            cursor: pointer;
            margin-right: 15px;
        }

        .header h3 {
            flex-grow: 1;
        }

        .header a {
            background: #667eea;
            color: white;
            padding: 6px 10px;
            border-radius: 5px;
            font-size: 12px;
            text-decoration: none;
        }

        .sidebar {
            position: fixed;
            left: -250px;
            top: 0;
            width: 250px;
            height: 100vh;
            background: #f5f6fc;
            transition: 0.3s;
            padding-top: 15px;
        }

        .sidebar.active {
            left: 0;
            box-shadow: 0 0 20px rgba(75,108,183,0.25);
        }

        .close-btn {
            text-align: right;
            padding: 10px 20px;
            cursor: pointer;
            font-size: 20px;
        }

        .sidebar a {
            display: block;
            padding: 12px 18px;
            margin: 6px 10px;
            border: 1px solid #667eea;
            color: #667eea;
            text-decoration: none;
            border-radius: 5px;
            font-size: 14px;
        }

        .sidebar a:hover {
            background: #667eea;
            color: white;
        }

        .content {
            padding: 30px;
            transition: margin-left 0.3s;
            min-height: calc(100vh - 50px);
        }

        .content.shift {
            margin-left: 250px;
        }

        .card {
            background: white;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 0 15px rgba(75,108,183,0.25);
        }

        .card h3 {
            text-align: center;
            margin-bottom: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: center;
        }

        th {
            background: #667eea;
            color: white;
            padding: 10px;
        }

        td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }

        tr:hover {
            background: #f0f2ff;
        }

        .btn {
            padding: 6px 10px;
            border-radius: 5px;
            font-size: 12px;
            color: white;
            text-decoration: none;
            margin: 0 3px;
            display: inline-block;
        }

        .btn-danger { background: #e53e3e; }
        .btn-primary { background: #667eea; }
        .btn-secondary { background: #718096; }
        .btn-success { background: #38a169; }

        .alert {
            text-align: center;
            padding: 10px;
            background: #fff3cd;
            border-radius: 5px;
            margin-bottom: 15px;
        }

        .footer {
            margin-top: 20px;
            display: flex;
            justify-content: center;
            gap: 15px;
        }
    </style>
</head>

<body>

<div id="sidebar" class="sidebar">
    <div class="close-btn" onclick="toggleSidebar()">✖</div>
    <c:forEach var="node" items="${nodeslist}">
    <a href="${node.path}"> ${node.identifier}</a>
    </c:forEach>
</div>

<div class="header">
    <span class="hamburger" onclick="toggleSidebar()">☰</span>
    <h3>Node Management</h3>
    <a href="/logout">Logout</a>
</div>

<div id="content" class="content">
    <div class="card">

        <h3>List of Nodes</h3>

        <c:if test="${empty nodes}">
            <div class="alert">No Nodes found</div>
        </c:if>

        <c:if test="${not empty nodes}">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Node</th>
                    <th>Path</th>
                    <th>Roles</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="node" items="${nodes}">
                    <tr>
                        <td>${node.id}</td>
                        <td>${node.identifier}</td>
                        <td>${node.path}</td>
                        <td>${node.roles}</td>
                        <td>
                            <a class="btn btn-danger"
                               href="/node/delete?identifier=${node.identifier}"
                               onclick="return confirm('Are you sure you want to delete this node?');">
                                Delete
                            </a>
                            <a class="btn btn-primary"
                               href="/node/get?identifier=${node.identifier}">
                                Update
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>

        <div class="footer">
            <a class="btn btn-secondary" href="/">Home</a>
            <a class="btn btn-success" href="/node/add">+ Add New Node</a>
        </div>

    </div>
</div>

<script>
    function toggleSidebar() {
        document.getElementById("sidebar").classList.toggle("active");
        document.getElementById("content").classList.toggle("shift");
    }
</script>

</body>
</html>