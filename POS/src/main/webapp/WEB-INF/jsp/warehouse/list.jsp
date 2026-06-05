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
            margin: 0;
            font-family: 'Poppins', 'Segoe UI', Arial, sans-serif;
            background: #fff8f0;
            min-height: 100vh;
            padding: 30px;
        }

        .container {
            width: 90%;
            margin: auto;
            background: #efe3d9;
            padding: 30px;
            border-radius: 16px;
            box-shadow: 0 14px 35px rgba(0, 0, 0, 0.15);
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }

        .top-bar h2 {
            margin: 0;
            color: #4a2e2b;
            font-size: 24px;
            font-weight: 700;
        }

        .top-buttons {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            min-width: 130px;
            min-height: 42px;
            padding: 10px 20px;
            border-radius: 10px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            color: #fff8f0;
            background-color: #4B2E2B;
            transition: 0.2s ease;
            box-sizing: border-box;
        }

        .btn:hover {
            background-color: #3a2421;
        }

        .btn-edit,
        .btn-delete {
            min-width: 42px;
            min-height: 42px;
            padding: 10px;
        }

        .btn-edit {
            background-color: #4B2E2B;
        }

        .btn-edit:hover {
            background-color: #3a2421;
        }

        .btn-delete {
            background-color: #4B2E2B;
        }

        .btn-delete:hover {
            background-color: #3a2421;
        }

        table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            border-radius: 14px;
            overflow: hidden;
        }

        th {
            background-color: #4B2E2B;
            color: #FFF8F0;
            padding: 16px;
            font-size: 14px;
            font-weight: 600;
            text-align: center;
        }

        tr {
            height: 70px;
        }

        tbody tr {
            background: #fff8f0;
        }

        tbody tr:nth-child(even) {
            background: #eadfd6;
        }

        tbody tr:hover {
            background: #e2cec1;
        }

        td {
            padding: 14px 16px;
            font-size: 14px;
            color: #000;
            text-align: center;
            vertical-align: middle;
        }

        .action-cell {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 12px;
            white-space: nowrap;
        }

        .action-cell i {
            font-size: 14px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="top-bar">
        <h2>Warehouse List</h2>
        <div class="top-buttons">
            <a href="${pageContext.request.contextPath}/warehouse/add" class="btn">
                <i class="fa-solid fa-plus"></i> Add Warehouse
            </a>
            <a href="${pageContext.request.contextPath}/" class="btn">
                Home
            </a>
        </div>
    </div>
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
                       title="Edit Warehouse">
                       <i class="fa-solid fa-pen"></i>
                    </a>
                    <a href="${pageContext.request.contextPath}/warehouse/delete?identifier=${warehouse.identifier}"
                       class="btn btn-delete"
                       onclick="return confirm('Are you sure you want to delete this warehouse?');"
                       title="Delete Warehouse">
                       <i class="fa-solid fa-trash"></i>
                    </a>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>