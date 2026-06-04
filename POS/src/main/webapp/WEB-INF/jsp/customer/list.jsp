<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS - Customers</title>

    <style>
        *, *::before, *::after {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: "Segoe UI", Arial, sans-serif;
            background: #f0f2f5;
            padding: 28px;
            color: #333;
        }

        .list-wrap {
            background: #fff;
            border-radius: 8px;
            border: 1px solid #dde1e7;
            padding: 24px;
        }

        .list-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 18px;
        }

        .list-header h2 {
            margin: 0;
            font-size: 18px;
            color: #1e3a5f;
        }

        .btn {
            display: inline-block;
            padding: 7px 14px;
            border-radius: 5px;
            font-size: 13px;
            font-weight: 500;
            cursor: pointer;
            border: none;
            text-decoration: none;
            transition: opacity 0.15s;
        }

        .btn:hover { opacity: 0.88; }

        .btn-primary { background: #1e6fd9; color: #fff; }
        .btn-warning { background: #f0a500; color: #fff; }
        .btn-danger  { background: #dc3545; color: #fff; } /* ✅ Delete */
        .btn-home    { background: #6c757d; color: #fff; }
        .btn-sm      { padding: 5px 11px; font-size: 12px; }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background: #1e3a5f;
            color: #fff;
            padding: 10px 14px;
            text-align: left;
            font-size: 13px;
            font-weight: 600;
        }

        td {
            padding: 10px 14px;
            border-bottom: 1px solid #eef0f3;
            font-size: 14px;
            vertical-align: middle;
        }

        tr:hover td { background: #f7f9fc; }

        th.center,
        td.center { text-align: center; }

        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 24px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            inset: 0;
            background-color: #dc3545;
            border-radius: 30px;
            transition: 0.4s;
            cursor: pointer;
        }

        .slider:before {
            content: "";
            position: absolute;
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: #fff;
            border-radius: 50%;
            transition: 0.4s;
        }

        input:checked + .slider {
            background-color: #28a745;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }
    </style>
</head>

<body>

<div class="list-wrap">

    <div class="list-header">
        <h2>Customers</h2>
        <div>
            <a href="${pageContext.request.contextPath}/" class="btn btn-home">Home</a>
            <a href="${pageContext.request.contextPath}/customer/add" class="btn btn-primary">
                + Add Customer
            </a>
        </div>
    </div>

    <table>
        <thead>
            <tr>
                <th>Name</th>
                <th>Phone</th>
                <th>Email</th>
                <th>Party Type</th>
                <th class="center">Status</th>
                <th class="center">Action</th>
            </tr>
        </thead>

        <tbody>
            <c:forEach items="${customers}" var="cust">
                <tr>
                    <td>${cust.customerName}</td>
                    <td>${cust.phoneNum}</td>
                    <td>${cust.username}</td>
                    <td>${cust.partyType}</td>

                    <!-- Status Toggle -->
                    <td class="center">
                        <form method="get"
                              action="${pageContext.request.contextPath}/customer/toggle"
                              style="margin:0; display:flex; justify-content:center;">
                            <input type="hidden" name="identifier" value="${cust.identifier}" />
                            <label class="switch">
                                <input type="checkbox"
                                       name="status"
                                       onchange="this.form.submit()"
                                       <c:if test="${cust.status}">checked</c:if> />
                                <span class="slider"></span>
                            </label>
                        </form>
                    </td>

                    <!--  Edit + Delete -->
                    <td class="center">
                        <a class="btn btn-warning btn-sm"
                           href="${pageContext.request.contextPath}/customer/update?identifier=${cust.identifier}">
                            Edit
                        </a>

                        <a class="btn btn-danger btn-sm"
                           href="${pageContext.request.contextPath}/customer/delete?identifier=${cust.identifier}"
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