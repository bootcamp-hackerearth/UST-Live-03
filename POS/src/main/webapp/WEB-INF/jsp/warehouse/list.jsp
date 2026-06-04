<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Warehouse Management</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #ffffff;
        }

        .container {
            width: 95%;
            max-width: 1000px;
            margin: 40px auto;
            background: #ffffff;
            padding: 18px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }

        .app-title {
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #14b8a6;
            margin-bottom: 4px;
        }

        h2 {
            text-align: center;
            margin-bottom: 12px;
            font-size: 22px;
        }

        .actions {
            display: flex;
            justify-content: flex-end;
            gap: 8px;
            margin-bottom: 12px;
        }

        .home-btn {
            padding: 7px 16px;
            background: #ffffff;
            color: teal;
            text-decoration: none;
            border-radius: 18px;
            font-size: 13px;
            font-weight: 600;
            border: 1px solid teal;
        }

        .add-btn {
            padding: 7px 16px;
            background: teal;
            color: white;
            text-decoration: none;
            border-radius: 18px;
            font-size: 13px;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 12px;
            border-bottom: 1px solid #e5e7eb;
            text-align: center;
            font-size: 13px;
        }

        th {
            background: #f1f5f9;
            font-weight: 700;
        }

        tr:hover {
            background: #f8fafc;
        }

        .action-link {
            padding: 6px 12px;
            border-radius: 18px;
            color: white;
            text-decoration: none;
            font-weight: 600;
            font-size: 12px;
        }

        .edit {
            background: teal;
        }

        .delete {
            background: #ef4444;
            margin-left: 6px;
        }

        .empty {
            text-align: center;
            padding: 18px;
            font-size: 14px;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="app-title">POS Application</div>

    <h2>Warehouse Management</h2>

    <div class="actions">
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>

        <a href="${pageContext.request.contextPath}/wareHouse/add" class="add-btn">
            Add Warehouse
        </a>
    </div>

    <c:if test="${empty wareHouses}">
        <div class="empty">No warehouses found</div>
    </c:if>

    <c:if test="${not empty wareHouses}">
        <table>
            <tr>
                <th>Warehouse Name</th>
                <th>Location</th>
                <th>Manager</th>
                <th>Action</th>
            </tr>

            <c:forEach var="wh" items="${wareHouses}">
                <tr>
                    <td>${wh.identifier}</td>
                    <td>${wh.location}</td>
                    <td>${wh.manager}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/wareHouse/get?identifier=${wh.identifier}"
                           class="action-link edit">Edit</a>

                        <a href="${pageContext.request.contextPath}/wareHouse/delete?identifier=${wh.identifier}"
                           class="action-link delete"
                           onclick="return confirm('Are you sure you want to delete this warehouse?');">
                            Delete
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

</div>

</body>
</html>
