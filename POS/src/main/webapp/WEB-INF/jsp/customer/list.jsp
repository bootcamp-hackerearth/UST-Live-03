<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer List</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
            padding: 40px;
            color: #333;
        }

        .container {
            max-width: 1150px;
            margin: 0 auto;
            background: white;
            padding: 35px;
            border-radius: 12px;
            box-shadow: 0 3px 12px rgba(0,0,0,0.1);
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }

        h2 {
            margin: 0;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            table-layout: fixed;
        }

        th, td {
            padding: 14px 12px;
            border-bottom: 1px solid #eee;
            font-size: 14px;
            vertical-align: middle;
            text-align: center;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        th {
            background-color: #f8f9fa;
            border-bottom: 2px solid #ddd;
        }

        tr:hover {
            background-color: #f5f7f9;
        }

        th:nth-child(1), td:nth-child(1) { width: 50px; }
        th:nth-child(2), td:nth-child(2) { width: 150px; text-align: left; }
        th:nth-child(3), td:nth-child(3) { width: 200px; text-align: left; }
        th:nth-child(4), td:nth-child(4) { width: 120px; }
        th:nth-child(5), td:nth-child(5) { width: 100px; }
        th:nth-child(6), td:nth-child(6) { width: 100px; text-align: right; }
        th:nth-child(7), td:nth-child(7) { width: 110px; text-align: right; }
        th:nth-child(8), td:nth-child(8) { width: 130px; }

        .btn {
            padding: 6px 10px;
            text-decoration: none;
            border-radius: 5px;
            font-size: 13px;
            color: white;
            display: inline-block;
            text-align: center;
        }

        .btn-add    { background: #28a745; }
        .btn-edit   { background: #007bff; }
        .btn-delete { background: #dc3545; }
        .btn-back   { background: #6c757d; }

        .actions {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 6px;
        }

        .empty {
            text-align: center;
            padding: 40px;
            color: #888;
        }

        .currency {
            font-family: monospace;
            text-align: right;
        }

        .badge {
            padding: 3px 10px;
            border-radius: 12px;
            font-size: 12px;
            background: #e9ecef;
            color: #333;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="header">
        <h2>Customer List</h2>
        <div>
            <a href="/" class="btn btn-back">Home</a>
            <a href="/customer/add" class="btn btn-add">+ Add Customer</a>
        </div>
    </div>

    <c:if test="${empty customers}">
        <div class="empty">No customers available</div>
    </c:if>

    <c:if test="${not empty customers}">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Type</th>
                <th>Balance</th>
                <th>Credit Limit</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="c" items="${customers}">
                <tr>

                    <td>${c.id}</td>

                    <td style="text-align:left;"><strong>${c.customerName}</strong></td>

                    <td style="text-align:left;">${c.identifier}</td>

                    <td>${c.phoneNumber}</td>

                    <td><span class="badge">${c.partyType}</span></td>

                    <td class="currency">₹ ${c.balance}</td>

                    <td class="currency">₹ ${c.creditLimit}</td>

                    <td>
                        <div class="actions">
                            <a href="/customer/get?identifier=${c.identifier}"
                               class="btn btn-edit">Edit</a>

                            <a href="/customer/delete?identifier=${c.identifier}"
                               class="btn btn-delete"
                               onclick="return confirm('Delete this customer?');">
                                Delete
                            </a>
                        </div>
                    </td>

                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

</div>

</body>
</html>