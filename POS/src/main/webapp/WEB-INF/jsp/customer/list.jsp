<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer List</title>

    <style>
        body {
            margin: 0;
            padding: 32px;
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #f4f6fb, #eef1f8);
        }

        h2 {
            text-align: center;
            color: #374a9e;
            margin-bottom: 25px;
        }

        .table-wrapper {
            overflow-x: auto;
            background: #fff;
            border-radius: 14px;
            box-shadow: 0 12px 30px rgba(0,0,0,0.08);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            min-width: 900px;
        }

        th {
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: #ffffff;
            padding: 14px;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        td {
            padding: 14px;
            text-align: center;
            border-bottom: 1px solid #edf0f7;
            font-size: 14px;
            color: #333;
        }

        tr:last-child td {
            border-bottom: none;
        }

        tbody tr {
            transition: background 0.2s ease;
        }

        tbody tr:hover {
            background: #f7f9ff;
        }

        .alert {
            padding: 16px;
            background: #fff3cd;
            color: #856404;
            border-radius: 10px;
            text-align: center;
            font-size: 14px;
            max-width: 600px;
            margin: 30px auto;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        }

        /* Action buttons */
        .btn-action {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 34px;
            height: 34px;
            border-radius: 8px;
            text-decoration: none;
            font-size: 16px;
            margin: 0 4px;
            transition: all 0.2s ease;
        }

        .btn-edit {
            background: #e8f0ff;
            color: #2b5fd9;
        }

        .btn-edit:hover {
            background: #d6e4ff;
            transform: scale(1.05);
        }

        .btn-delete {
            background: #ffe8ea;
            color: #d63341;
        }

        .btn-delete:hover {
            background: #ffd0d5;
            transform: scale(1.05);
        }

        /* Footer buttons */
        .footer-actions {
            margin-top: 30px;
            display: flex;
            justify-content: center;
            gap: 18px;
        }

        .btn {
            padding: 12px 20px;
            border-radius: 12px;
            font-weight: 600;
            text-decoration: none;
            font-size: 14px;
            transition: all 0.25s ease;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }

        .btn-home {
            background: #6c757d;
            color: #fff;
        }

        .btn-home:hover {
            background: #5a6268;
            transform: translateY(-1px);
        }

        .btn-add {
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: #fff;
        }

        .btn-add:hover {
            transform: translateY(-2px) scale(1.03);
        }
    </style>
</head>

<body>

<h2>Customer List</h2>

<c:if test="${empty customers}">
    <div class="alert">No customers found</div>
</c:if>

<c:if test="${not empty customers}">
    <div class="table-wrapper">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Identifier</th>
                <th>Phone</th>
                <th>Email</th>
                <th>Type</th>
                <th>Address</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${customers}" var="customer">
                <tr>
                    <td>${customer.id}</td>
                    <td>${customer.identifier}</td>
                    <td>${customer.phoneno}</td>
                    <td>${customer.email}</td>
                    <td>${customer.partytype}</td>
                    <td>${customer.address}</td>
                    <td>
                        <a href="/customer/get?identifier=${customer.identifier}"
                           class="btn-action btn-edit"
                           title="Edit Customer">✏️</a>
                        <a href="/customer/delete?identifier=${customer.identifier}"
                           class="btn-action btn-delete"
                           title="Delete Customer"
                           onclick="return confirm('Are you sure you want to delete this customer?');">🗑</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>

<div class="footer-actions">
    <a href="/" class="btn btn-home">🏠 Home</a>
    <a href="/customer/add" class="btn btn-add">＋ Add Customer</a>
</div>

</body>