<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer Management</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #ffffff;
        }

        /* ===== CONTAINER ===== */
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
            color: #14b8a6; /* light teal */
            margin-bottom: 4px;
        }

        h2 {
            text-align: center;
            font-size: 24px;
            margin-bottom: 10px;
        }

        /* ===== ACTIONS ===== */
        .list-actions {
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
            background: teal;
            color: white;
            padding: 7px 16px;
            border-radius: 18px;
            font-weight: 600;
            text-decoration: none;
            font-size: 13px;
        }

        /* ===== TABLE ===== */
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
        }

        th {
            background: #f1f5f9;
            padding: 12px;
            text-align: center;
            font-weight: 700;
            border-bottom: 1px solid #e5e7eb;
        }

        td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #e5e7eb;
        }

        tr {
            line-height: 1.3;
        }

        tbody tr:hover {
            background: #f8fafc;
        }

        /* ===== ACTION BUTTONS ===== */
        .action-link {
            padding: 6px 12px;
            border-radius: 18px;
            font-weight: 600;
            color: white;
            text-decoration: none;
            font-size: 12px;
        }

        .edit { background: teal; }
        .delete { background: #ef4444; margin-left: 6px; }
    </style>
</head>

<body>

<div class="container">
    <div class="app-title">POS Application</div>
    <h2>Customer Management</h2>
    <div class="list-actions">
        <a class="home-btn" href="${pageContext.request.contextPath}/">Home</a>
        <a class="add-btn" href="${pageContext.request.contextPath}/customer/add">
            Add Customer
        </a>
    </div>

    <table>
        <thead>
        <tr>
            <th>Phone</th>
            <th>Name</th>
            <th>Party Type</th>
            <th>Credit</th>
            <th>Credit Type</th>
            <th>Action</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach var="c" items="${customers}">
            <tr>
                <td>${c.identifier}</td>
                <td>${c.customerName}</td>
                <td>${c.partyType}</td>
                <td>${c.credit}</td>
                <td>${c.creditType}</td>

                <td>
                    <a class="action-link edit"
                       href="${pageContext.request.contextPath}/customer/get?identifier=${c.identifier}">
                        Edit
                    </a>

                    <a class="action-link delete"
                       href="${pageContext.request.contextPath}/customer/delete?identifier=${c.identifier}"
                       onclick="return confirm('Are you sure you want to delete this customer?');">
                        Delete
                    </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

</body>
</html>