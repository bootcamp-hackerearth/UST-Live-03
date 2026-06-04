<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Warehouse List</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
            padding: 40px;
            color: #333;
        }

        .container {
            max-width: 1100px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        h2 {
            margin: 0;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            table-layout: fixed;
        }

        th {
            background-color: #f8f9fa;
            padding: 12px;
            text-align: left;
        }

        td {
            padding: 12px;
            border-bottom: 1px solid #eee;
            word-wrap: break-word;
        }

        tr:hover {
            background-color: #fafafa;
        }

        .address-col {
            max-width: 200px;
            white-space: normal;
        }

        .actions-col {
            width: 160px;
            white-space: nowrap;
        }

        .btn {
            padding: 6px 12px;
            text-decoration: none;
            border-radius: 4px;
            font-size: 13px;
            color: white;
        }

        .btn-add { background: #28a745; }
        .btn-edit { background: #007bff; }
        .btn-delete { background: #dc3545; }
        .btn-back { background: #6c757d; margin-right: 10px; }

        .empty-msg {
            text-align: center;
            padding: 40px;
            color: #888;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="header">
        <h2>Warehouse List</h2>

        <div>
            <a href="/" class="btn btn-back">Home</a>
            <a href="/warehouse/add" class="btn btn-add">+ Add Warehouse</a>
        </div>
    </div>

    <c:if test="${empty warehouses}">
        <div class="empty-msg">No warehouses found</div>
    </c:if>

    <c:if test="${not empty warehouses}">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Identifier</th>
                <th>Name</th>
                <th>Country</th>
                <th>Region</th>
                <th>Address</th>
                <th>Phone</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="w" items="${warehouses}">
                <tr>
                    <td>${w.id}</td>
                    <td>${w.identifier}</td>
                    <td>${w.name}</td>
                    <td>${w.country}</td>
                    <td>${w.region}</td>

                    <td class="address-col" title="${w.address}">
                        ${w.address}
                    </td>

                    <td>${w.phoneNo}</td>

                    <td class="actions-col">
                        <a href="/warehouse/get?identifier=${w.identifier}" class="btn btn-edit">Edit</a>
                        <a href="/warehouse/delete?identifier=${w.identifier}"
                           class="btn btn-delete"
                           onclick="return confirm('Delete this warehouse?');">
                            Delete
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