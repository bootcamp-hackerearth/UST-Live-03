<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Stock List</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            font-family: "Segoe UI", Tahoma, Arial, sans-serif;
            background: linear-gradient(120deg, #e0eafc, #cfdef3);
            padding: 30px;
            margin: 0;
        }

        .container {
            max-width: 1200px;
            margin: auto;
            background: #ffffff;
            padding: 25px 30px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.12);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #2c3e50;
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            margin-bottom: 15px;
        }

        .home-btn, .add-btn {
            padding: 10px 18px;
            text-decoration: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
            color: #fff;
        }

        .home-btn {
            background: #6c757d;
        }

        .add-btn {
            background: #28a745;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        thead {
            background-color: #343a40;
            color: white;
        }

        th, td {
            padding: 14px 12px;
            text-align: center;
            font-size: 14px;
        }

        tbody tr:nth-child(even) {
            background-color: #f8f9fa;
        }

        tbody tr:hover {
            background-color: #e9f2ff;
        }

        .price {
            font-weight: 600;
            color: #2e86de;
        }

        .status {
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 700;
            color: #fff;
        }

        .available {
            background-color: #28a745;
        }

        .out-of-stock {
            background-color: #dc3545;
        }

        .action-btn {
            padding: 7px 12px;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
            margin: 2px;
            color: #fff;
            display: inline-block;
        }

        .edit-btn {
            background-color: #007bff;
        }

        .delete-btn {
            background-color: #dc3545;
        }

        .no-data {
            text-align: center;
            font-style: italic;
            color: #888;
            padding: 20px;
        }
             .toggle-switch {
                    position: relative;
                    width: 50px;
                    height: 26px;
                    display: inline-block;
                }

                .toggle-switch input {
                    opacity: 0;
                    width: 0;
                    height: 0;
                }

                .slider {
                    position: absolute;
                    cursor: pointer;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background-color: #dc3545;
                    transition: 0.3s;
                    border-radius: 30px;
                }

                .slider:before {
                    position: absolute;
                    content: "";
                    height: 20px;
                    width: 20px;
                    left: 3px;
                    bottom: 3px;
                    background-color: white;
                    transition: 0.3s;
                    border-radius: 50%;
                }

                input:checked + .slider {
                    background-color: #198754;
                }

                input:checked + .slider:before {
                    transform: translateX(24px);
                }
    </style>
</head>

<body>

<div class="container">
    <h2>Stock List</h2>

    <div class="top-bar">
        <a class="home-btn" href="${pageContext.request.contextPath}/">⬅ Home</a>
        <a class="add-btn" href="${pageContext.request.contextPath}/stock/add">+ Add Stock</a>
    </div>

    <table>
        <thead>
        <tr>
            <th>Identifier</th>
            <th>Warehouse</th>
            <th>Productname</th>
            <th>Quantity</th>
            <th>Price</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
        </thead>

        <tbody>
        <c:choose>
            <c:when test="${not empty stock}">
                <c:forEach items="${stock}" var="s">
                    <tr>
                        <td>${s.identifier}</td>
                        <td>${s.warehouse}</td>
                        <td>${s.productname}</td>
                        <td>${s.quantity}</td>
                        <td class="price">${s.price}</td>
                               <td>
                                        <form method="get"
                                              action="${pageContext.request.contextPath}/stock/toggleStatus">
                                            <input type="hidden" name="identifier" value="${s.identifier}"/>

                                            <label class="toggle-switch">
                                                <input type="checkbox"
                                                       name="status"
                                                       onchange="this.form.submit()"
                                                       <c:if test="${s.status}">checked</c:if>>
                                                <span class="slider"></span>
                                            </label>
                                        </form>
                                    </td>
                        <td>
                            <a class="action-btn edit-btn"
                               href="${pageContext.request.contextPath}/stock/get?identifier=${s.identifier}">
                                Edit
                            </a>
                            <a class="action-btn delete-btn"
                               href="${pageContext.request.contextPath}/stock/delete?identifier=${s.identifier}"
                               onclick="return confirm('Are you sure you want to delete this stock?');">
                                Delete
                            </a>
                        </td>
                    </tr>
                </c:forEach>
            </c:when>

            <c:otherwise>
                <tr>
                    <td colspan="6" class="no-data">
                        No stock records available
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>
</div>

</body>
</html>