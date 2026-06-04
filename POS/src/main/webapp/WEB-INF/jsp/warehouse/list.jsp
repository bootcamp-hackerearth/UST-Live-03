<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Warehouse List</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            padding: 30px;
        }

        .container {
            width: 90%;
            margin: auto;
            background: #ffffff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        h2 {
            text-align: center;
            margin-bottom: 24px;
            color: #1e293b;
            font-size: 22px;
            font-weight: 700;
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
        }

        .btn {
            padding: 10px 16px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            color: #ffffff;
            background-color: #1e293b;
        }

        .btn-edit {
            background-color: #2563eb;
        }

        .btn-edit:hover {
            background-color: #1e40af;
        }

        .btn-delete {
            background-color: #dc2626;
        }

        .btn-delete:hover {
            background-color: #b91c1c;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background-color: #1e293b;
            color: #ffffff;
            padding: 12px;
            font-size: 14px;
            text-align: center;
        }

        td {
            padding: 12px;
            font-size: 14px;
            color: #334155;
            text-align: center;
            border-bottom: 1px solid #e2e8f0;
        }

        tr:nth-child(even) {
            background-color: #f8fafc;
        }

        .action-cell {
            display: flex;
            justify-content: center;
            gap: 10px;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="top-bar">
        <a href="${pageContext.request.contextPath}/" class="btn">
            Home
        </a>

        <a href="${pageContext.request.contextPath}/warehouse/add" class="btn">
            + Add Warehouse
        </a>
    </div>

    <h2>Warehouse List</h2>

    <table>
        <tr>
            <th>ID</th>
            <th>Identifier</th>
            <th>Region</th>
            <th>City</th>
            <th>State</th>
            <th>Country</th>
            <th>Capacity</th>
            <th>Contact Name</th>
            <th>Contact Number</th>
            <th>Action</th>
        </tr>

        <c:forEach var="warehouse" items="${warehouses}">
            <tr>
                <td>${warehouse.id}</td>
                <td>${warehouse.identifier}</td>
                <td>${warehouse.region}</td>
                <td>${warehouse.city}</td>
                <td>${warehouse.state}</td>
                <td>${warehouse.country}</td>
                <td>${warehouse.capacity}</td>
                <td>${warehouse.contactName}</td>
                <td>${warehouse.contactNumber}</td>

                <td class="action-cell">
                    <a href="${pageContext.request.contextPath}/warehouse/get?identifier=${warehouse.identifier}"
                       class="btn btn-edit">
                        Edit
                    </a>

                    <a href="${pageContext.request.contextPath}/warehouse/delete?identifier=${warehouse.identifier}"
                       class="btn btn-delete"
                       onclick="return confirm('Are you sure you want to delete this warehouse?');">
                        Delete
                    </a>
                </td>
            </tr>
        </c:forEach>

        <c:if test="${empty warehouses}">
            <tr>
                <td colspan="10" style="text-align:center;">
                    No warehouses found
                </td>
            </tr>
        </c:if>

    </table>
</div>

</body>
</html>