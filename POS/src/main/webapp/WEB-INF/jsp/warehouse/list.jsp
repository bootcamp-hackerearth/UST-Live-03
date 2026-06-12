<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <title>Warehouse List</title>
    <style>
        body {
            background-color: #FFF8F0;
            font-family: "Segoe UI", Arial, sans-serif;
            padding-top: 40px;
        }

        .container {
            width: 90%;
            margin: auto;
        }

        h2 {
            text-align: center;
            color: #4B2E2B;
            font-weight: 600;
            margin-bottom: 30px;
        }

        .top-bar {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin-bottom: 20px;
        }

        .top-bar .btn {
            background-color: #6b4a46;
            color: #FFF8F0;
            border-radius: 10px;
            padding: 10px 20px;
            text-decoration: none;
        }

        .top-bar .btn:hover {
            background-color: #543835;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            border-radius: 12px;
            overflow: hidden;
        }

        th {
            background-color: #4B2E2B;
            color: #FFF8F0;
            padding: 14px;
            text-transform: uppercase;
            font-size: 14px;
        }

        td {
            padding: 16px;
            text-align: center;
        }

        tr:nth-child(even) {
            background-color: #e8dcd2;
        }

        tr:hover {
            background-color: #f5e6dc;
        }

        .btn-edit {
            background-color: #4B2E2B;
            color: #FFF8F0;
            border-radius: 8px;
            padding: 6px 12px;
        }

        .btn-edit:hover {
            background-color: #3a2421;
        }

        .btn-delete {
            background-color: #8d3c36;
            color: #FFF8F0;
            border-radius: 8px;
            padding: 6px 12px;
        }

        .btn-delete:hover {
            background-color: #702f2a;
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
        <a href="${pageContext.request.contextPath}/" class="btn">Home</a>
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
                       class="btn btn-edit"
                       title = "Edit Warehouse">
                       <i class="fa-solid fa-pen"></i>
                    </a>
                    <a href="${pageContext.request.contextPath}/warehouse/delete?identifier=${warehouse.identifier}"
                       class="btn btn-delete"
                       onclick="return confirm('Are you sure you want to delete this warehouse?');"
                       title = "Delete Warehouse">
                       <i class="fa-solid fa-trash"></i>
                    </a>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>