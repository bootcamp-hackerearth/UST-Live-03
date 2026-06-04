<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer List</title>

    <style>
        body {
            font-family: "Segoe UI", Arial, sans-serif;
            background: #F6F7F9;
            padding: 30px;
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        .card {
            background: white;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #E5E7EB;
        }

        th {
            background: #2B2B2B;
            color: white;
            text-align: center;
        }

        .actions {
            text-align: center;
        }

        .btn {
            padding: 6px 12px;
            border-radius: 6px;
            border: none;
            font-weight: 600;
            cursor: pointer;
        }

        .add-btn {
            background: #2B2B2B;
            color: white;
        }

        .edit-btn {
            background: #2563EB;
            color: white;
        }

        .delete-btn {
            background: #DC2626;
            color: white;
        }

        a {
            text-decoration: none;
        }

        .address {
            font-size: 13px;
            color: #374151;
        }

        .msg {
            background: #DCFCE7;
            color: #166534;
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 15px;
            text-align: center;
        }

        .top-right-actions {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            margin-bottom: 15px;
        }
    </style>
</head>

<body>

<h2>Customer List</h2>

<div class="top-right-actions">
    <a href="/customer/add">
        <button class="btn add-btn">Add Customer</button>
    </a>
    <a href="/">
        <button class="btn">Home</button>
    </a>
</div>

<c:if test="${not empty message}">
    <div class="msg">${message}</div>
</c:if>

<div class="card">
    <table>
        <tr>
            <th>Email</th>
            <th>Name</th>
            <th>Type</th>
            <th>Phone</th>
            <th>Balance</th>
            <th>Credit Limit</th>
            <th>Action</th>
        </tr>

        <c:forEach items="${customers}" var="c">
            <tr>
                <td>${c.identifier}</td>
                <td>${c.customerName}</td>
                <td>${c.partyType}</td>
                <td>${c.phoneNo}</td>
                <td>${c.balance}</td>
                <td>${c.creditLimit}</td>

                <td class="actions">
                    <a href="/customer/get?identifier=${c.identifier}">
                        <button class="btn edit-btn">Edit</button>
                    </a>
                    <a href="/customer/delete?identifier=${c.identifier}"
                       onclick="return confirm('Delete this customer?')">
                        <button class="btn delete-btn">Delete</button>
                    </a>
                </td>
            </tr>
        </c:forEach>

    </table>
</div>

</body>
</html>