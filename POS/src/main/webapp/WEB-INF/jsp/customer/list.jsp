<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
<title>Customer List</title>
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

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }

        .page-header h2 {
            margin: 0;
            color: #4B2E2B;
            font-weight: 600;
        }

        .register-btn {
            background-color: #4B2E2B;
            color: #FFF8F0;
            border: none;
            padding: 8px 18px;
            font-size: 14px;
            font-weight: 600;
            border-radius: 8px;
            text-decoration: none;
            display: inline-block;
            margin-right: 10px;
        }

        .register-btn:hover {
            background-color: #3a2421;
            color: #FFF8F0;
        }

        .btn-secondary {
            background-color: #4B2E2B;
            color: #FFF8F0;
            border: none;
            padding: 8px 18px;
            font-size: 14px;
            font-weight: 600;
            border-radius: 8px;
            text-decoration: none;
            display: inline-block;
        }

        .btn-secondary:hover {
            background-color: #3a2421;
            color: #FFF8F0;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            border-radius: 14px;
            overflow: hidden;
            box-shadow: 0 10px 25px rgba(75, 46, 43, 0.15);
        }

        th {
            background-color: #4B2E2B;
            color: #FFF8F0;
            padding: 14px;
            font-size: 13px;
            text-transform: uppercase;
        }

        td {
            padding: 14px;
            text-align: center;
            font-size: 14px;
        }

        tr:nth-child(even) {
            background-color: #efe4dc;
        }

        tr:hover {
            background-color: #f7ebe4;
        }

        .btn {
            background-color: #6b4a46;
            color: #FFF8F0;
            border-radius: 10px;
            padding: 6px 14px;
            text-decoration: none;
            font-weight: 500;
        }

        .btn:hover {
            background-color: #543835;
        }

        .action-cell {
            display: flex;
            justify-content: center;
            gap: 8px;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 22px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            inset: 0;
            background-color: #cfc4bb;
            border-radius: 20px;
            transition: 0.4s;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 16px;
            width: 16px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            border-radius: 50%;
            transition: 0.4s;
        }

        input:checked + .slider {
            background-color: #6b4a46;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }

        @media (max-width: 900px) {
            .page-header {
                flex-direction: column;
                gap: 15px;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="page-header">
        <h2>Customer Management</h2>
        <div>
            <a href="/customer/add" class="register-btn">
                <i class="fa-solid fa-plus"></i> Add Customer
            </a>
            <a href="/" class="btn-secondary">
                Home
            </a>
        </div>
    </div>
    <c:if test="${not empty message}">
        <div style="text-align:center; color:green; margin-bottom:10px;">
            ${message}
        </div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${empty customers}">
        <div style="text-align:center; color:#4B2E2B;">
            No customers found
        </div>
    </c:if>
    <c:if test="${not empty customers}">
        <table>
            <tr>
                <th>ID</th>
                <th>Customer Name</th>
                <th>Party Type</th>
                <th>Balance</th>
                <th>Email</th>
                <th>Credit Limit</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            <c:forEach var="customer" items="${customers}">
                <tr>
                    <td>${customer.id}</td>
                    <td>${customer.name}</td>
                    <td>${customer.partyType}</td>
                    <td>${customer.balance} ${customer.balanceType}</td>
                    <td>${customer.email}</td>
                    <td>${customer.creditLimit}</td>
                    <td>
                        <form action="${pageContext.request.contextPath}/customer/toggleStatus" method="post">
                            <input type="hidden" name="identifier" value="${customer.identifier}"/>
                            <label class="switch">
                                <input type="checkbox"
                                       onchange="this.form.submit()"
                                       <c:if test="${customer.status}">checked</c:if>>
                                <span class="slider"></span>
                            </label>
                        </form>
                        <small>
                            <c:choose>
                                <c:when test="${customer.status}">
                                    Active
                                </c:when>
                                <c:otherwise>
                                    Inactive
                                </c:otherwise>
                            </c:choose>
                        </small>
                    </td>
                    <td class="action-cell">
                        <a href="/customer/get?identifier=${customer.identifier}"
                           class="btn"
                           title="Edit Customer">
                           <i class="fa-solid fa-pen"></i>
                        </a>
                        <a href="/customer/delete?identifier=${customer.identifier}"
                           class="btn"
                           onclick="return confirm('Are you sure you want to delete this customer?');"
                           title="Delete Customer">
                           <i class="fa-solid fa-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</div>
</body>
</html>