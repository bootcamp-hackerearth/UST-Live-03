<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Warehouse List</title>

    <style>
        body {
            font-family: "Segoe UI", Arial, sans-serif;
            background: #F6F7F9;
            padding: 30px;
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #111827;
        }

        .table-card {
            background: #fff;
            border-radius: 16px;
            padding: 20px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.06);
            border: 1px solid #E5E7EB;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 12px;
            text-align: center;
        }

        th {
            background: #2B2B2B;
            color: white;
        }

        tr:nth-child(even) {
            background: #F9FAFB;
        }

        .top-right-actions {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            margin-bottom: 15px;
        }

        .btn {
            padding: 6px 14px;
            border-radius: 8px;
            border: none;
            font-weight: 600;
            cursor: pointer;
        }

        .add-btn {
            background: #2B2B2B;
            color: white;
        }

        .edit-btn {
            background: #2B2B2B;
            color: white;
        }

        .delete-btn {
            background: #B91C1C;
            color: white;
        }

        a {
            text-decoration: none;
        }

        .msg {
            background: #DCFCE7;
            color: #166534;
            padding: 10px 14px;
            border-radius: 10px;
            margin-bottom: 15px;
            text-align: center;
            width: fit-content;
            margin-left: auto;
            margin-right: auto;
        }
    </style>
</head>

<body>

<h2>Warehouse List</h2>

<div class="top-right-actions">
    <a href="/warehouse/add">
        <button class="btn add-btn">Add Warehouse</button>
    </a>
    <a href="/">
        <button class="btn">Home</button>
    </a>
</div>

<c:if test="${not empty message}">
    <div class="msg">${message}</div>
</c:if>

<div class="table-card">
    <table>
        <tr>
            <th>Warehouse Name</th>
            <th>Region</th>
            <th>Country</th>
            <th>Location</th>
            <th>Contact Name</th>
            <th>Contact Number</th>
            <th>Action</th>
        </tr>

        <c:forEach items="${warehouses}" var="warehouse">
            <tr>
                <td>${warehouse.identifier}</td>
                <td>${warehouse.region}</td>
                <td>${warehouse.country}</td>
                <td>${warehouse.location}</td>
                <td>${warehouse.contactName}</td>
                <td>${warehouse.contactNumber}</td>
                <td>
                    <a href="/warehouse/get?identifier=${warehouse.identifier}">
                        <button class="btn edit-btn">Edit</button>
                    </a>
                    <a href="/warehouse/delete?identifier=${warehouse.identifier}"
                       onclick="return confirm('Delete this warehouse?')">
                        <button class="btn delete-btn">Delete</button>
                    </a>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>

</body>
</html>