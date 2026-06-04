<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Customer List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            padding: 30px;
        }

        .container-box {
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
            font-size: 24px;
            font-weight: 700;
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
        }

        .btn-custom {
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

        .btn-custom:hover {
            color: #ffffff;
            opacity: 0.9;
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
            vertical-align: middle;
        }

        td {
            padding: 12px;
            font-size: 14px;
            color: #334155;
            border-bottom: 1px solid #e2e8f0;
            text-align: center;
            vertical-align: middle;
        }

        tr:nth-child(even) {
            background-color: #f8fafc;
        }

        .status-cell {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
        }

        .form-switch .form-check-input {
            cursor: pointer;
            width: 2.6em;
            height: 1.4em;
        }

        .action-cell {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
        }

        .status-text {
            font-size: 13px;
            font-weight: 600;
        }

        .active-text {
            color: #16a34a;
        }

        .inactive-text {
            color: #dc2626;
        }

        .alert-box {
            padding: 15px;
            background: #e0f2fe;
            border-radius: 8px;
            text-align: center;
            color: #0369a1;
            font-weight: 500;
        }
    </style>
</head>

<body>

<div class="container-box">

    <div class="top-bar">
        <a href="${pageContext.request.contextPath}/"
           class="btn-custom">
            Home
        </a>

        <a href="${pageContext.request.contextPath}/customer/add"
           class="btn-custom">
            + Add Customer
        </a>
    </div>

    <h2>Customer List</h2>

    <c:if test="${empty customers}">
        <div class="alert-box">
            No customers found.
        </div>
    </c:if>

    <c:if test="${not empty customers}">
        <table>

            <thead>
            <tr>
                <th>Sl</th>
                <th>Name</th>
                <th>Phone</th>
                <th>Email</th>
                <th>Party Type</th>
                <th>Balance</th>
                <th>Status</th>
                <th style="width: 220px;">Action</th>
            </tr>
            </thead>

            <tbody>

            <c:forEach items="${customers}" var="customer" varStatus="status">

                <tr>

                    <td>${status.index + 1}</td>

                    <td>${customer.name}</td>

                    <td>${customer.phoneNo}</td>

                    <td>${customer.email}</td>

                    <td>${customer.partyType}</td>

                    <td>
                        ${customer.balance}
                        <small class="text-muted">
                            (${customer.balanceType})
                        </small>
                    </td>

                    <td>

                        <div class="status-cell">

                            <form action="${pageContext.request.contextPath}/customer/toggleStatus"
                                  method="get">

                                <input type="hidden"
                                       name="identifier"
                                       value="${customer.identifier}" />

                                <div class="form-check form-switch">

                                    <input
                                            class="form-check-input"
                                            type="checkbox"
                                            onchange="this.form.submit()"
                                            <c:if test="${customer.status}">
                                                checked
                                            </c:if>
                                    >

                                </div>

                            </form>

                            <span class="status-text
                                <c:choose>
                                    <c:when test='${customer.status}'>
                                        active-text
                                    </c:when>
                                    <c:otherwise>
                                        inactive-text
                                    </c:otherwise>
                                </c:choose>">

                                <c:choose>
                                    <c:when test="${customer.status}">
                                        Active
                                    </c:when>
                                    <c:otherwise>
                                        Inactive
                                    </c:otherwise>
                                </c:choose>

                            </span>

                        </div>

                    </td>

                    <td>

                        <div class="action-cell">

                            <a href="${pageContext.request.contextPath}/customer/get?identifier=${customer.identifier}"
                               class="btn-custom btn-edit">
                                Edit
                            </a>

                            <a href="${pageContext.request.contextPath}/customer/delete?identifier=${customer.identifier}"
                               class="btn-custom btn-delete"
                               onclick="return confirm('Are you sure you want to delete this customer?');">
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