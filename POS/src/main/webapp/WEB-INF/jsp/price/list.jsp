<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Price List</title>

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
            margin-top: 15px;
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
            display: inline-block;
        }
        .btn-home {
            background-color: #6c757d;
        }
        .btn-add {
            background-color: #28a745;
        }
        .btn-edit {
            background-color: #007bff;
        }
        .btn-delete {
            background-color: #dc3545;
        }
        .no-data {
            text-align: center;
            font-style: italic;
        }
    </style>
</head>

<body>

<h2>Price List</h2>

<!--  HOME & ADD PRICE BUTTONS -->
<div style="margin-bottom: 15px;">
    <a href="/" class="btn btn-home">Home</a>

    <a href="${pageContext.request.contextPath}/price/add"
       class="btn btn-add">
        Add Price
    </a>
</div>

<!--PRICE TABLE -->
<table>
    <thead>
        <tr>
            <th>ID</th>
            <th>Cost Price</th>
            <th>Selling Price</th>
            <th>Actions</th>
        </tr>
    </thead>

    <tbody>
        <c:forEach items="${priceList}" var="p">
            <tr>
                <td>${p.id}</td>
                <td>${p.costPrice}</td>
                <td>${p.sellingPrice}</td>
                <td>
                    <!--  EDIT -->
                    <a href="${pageContext.request.contextPath}/price/update?identifier=${p.identifier}"
                       class="btn btn-edit">
                        Edit
                    </a>

                    <!-- DELETE -->
                    <a href="${pageContext.request.contextPath}/price/delete?identifier=${p.identifier}"
                       class="btn btn-delete"
                       onclick="return confirm('Are you sure you want to delete this price?');">
                        Delete
                    </a>
                </td>
            </tr>
        </c:forEach>

        <!--  NO DATA MESSAGE -->
        <c:if test="${empty priceList}">
            <tr>
                <td colspan="4" class="no-data">
                    No price records found.
                </td>
            </tr>
        </c:if>
    </tbody>
</table>

</body>
</html>