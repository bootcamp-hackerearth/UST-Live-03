<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Warehouse List</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 30px;
        }

        h2 {
            margin-bottom: 15px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 10px;
            border: 1px solid #ddd;
            text-align: center;
        }

        th {
            background-color: #f4f4f4;
        }

        .btn {
            padding: 6px 12px;
            text-decoration: none;
            border-radius: 4px;
            color: white;
            font-size: 14px;
            margin-right: 5px;
        }

        .btn-home { background-color: #6c757d; } /* Home */
        .btn-add { background-color: #28a745; }
        .btn-edit { background-color: #007bff; }
        .btn-delete { background-color: #dc3545; }

        .no-data {
            text-align: center;
            font-style: italic;
        }

        /* Toggle Switch */
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
            cursor: pointer;
            inset: 0;
            background-color: #dc3545;
            transition: 0.4s;
            border-radius: 30px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: 0.4s;
            border-radius: 50%;
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

<h2>Warehouse List</h2>

<!--  HOME + ADD BUTTONS -->
<div style="margin-bottom: 15px;">
    <a href="${pageContext.request.contextPath}/"
       class="btn btn-home">
        Home
    </a>

    <a href="${pageContext.request.contextPath}/warehouse/add"
       class="btn btn-add">
        Add Warehouse
    </a>
</div>

<table>
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Code</th>
            <th>Location</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
    </thead>

    <tbody>
        <c:forEach items="${warehouseList}" var="w">
            <tr>
                <td>${w.id}</td>
                <td>${w.identifier}</td>
                <td>${w.code}</td>
                <td>${w.location}</td>

                <!-- STATUS TOGGLE -->
                <td class="status-cell">
                    <form method="get"
                          action="${pageContext.request.contextPath}/warehouse/toggle">

                        <input type="hidden" name="identifier"
                               value="${w.identifier}" />

                        <label class="switch">
                            <input type="checkbox"
                                   name="status"
                                   onchange="this.form.submit()"
                                   <c:if test="${w.status}">checked</c:if> />
                            <span class="slider"></span>
                        </label>
                    </form>
                </td>

                <td>
                    <a href="${pageContext.request.contextPath}/warehouse/update?id=${w.id}"
                       class="btn btn-edit">
                        Edit
                    </a>

                    <a href="${pageContext.request.contextPath}/warehouse/delete?id=${w.id}"
                       class="btn btn-delete"
                       onclick="return confirm('Are you sure you want to delete this warehouse?')">
                        Delete
                    </a>
                </td>
            </tr>
        </c:forEach>

        <c:if test="${empty warehouseList}">
            <tr>
                <td colspan="6" class="no-data">
                    No warehouse records found.
                </td>
            </tr>
        </c:if>
    </tbody>
</table>

</body>
</html>
