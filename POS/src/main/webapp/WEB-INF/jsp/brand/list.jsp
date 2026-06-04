<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Brand List</title>

    <style>
        body {
            font-family: 'Poppins', sans-serif;
            padding: 30px;
            background: #ffffff;
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .add-btn {
            padding: 10px 18px;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: #fff;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
        }

        th, td {
            padding: 14px;
            border-bottom: 1px solid #eee;
            text-align: center;
        }

        th {
            background: #f1f3f8;
            font-weight: 600;
        }

        img {
            width: 40px;
            height: 40px;
            border-radius: 50%;
        }

        .status-active {
            color: green;
            font-weight: 600;
        }

        .status-inactive {
            color: red;
            font-weight: 600;
        }

        .action a {
            margin: 0 8px;
            font-size: 18px;
            text-decoration: none;
            transition: transform 0.2s ease;
        }

        .action a:hover {
            transform: scale(1.2);
        }

        .edit-icon {
            color: #4b6cb7;
        }

        .delete-icon {
            color: #d9534f;
        }
    </style>
</head>

<body>

<div class="top-bar">
    <h2>Brand List</h2>
    <a href="/brand/add" class="add-btn">+ Add Brand</a>
</div>

<table>
    <thead>
        <tr>
            <th>SL</th>
            <th>Icon</th>
            <th>Brand Name</th>
            <th>Description</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
    </thead>

    <tbody>
        <c:forEach items="${brands}" var="brand" varStatus="loop">
            <tr>
                <td>${loop.index + 1}</td>

                <td>
                    <img src="/uploads/${brand.icon}" alt="icon">
                </td>

                <td>${brand.identifier}</td>
                <td>${brand.description}</td>

                <td>
                    <c:choose>
                        <c:when test="${brand.status}">
                            <span class="status-active">Active</span>
                        </c:when>
                        <c:otherwise>
                            <span class="status-inactive">Inactive</span>
                        </c:otherwise>
                    </c:choose>
                </td>

                <td class="action">
                    <a href="/brand/get?identifier=${brand.identifier}"
                       class="edit-icon"
                       title="Edit">
                        ✏️
                    </a>

                    <a href="/brand/delete?identifier=${brand.identifier}"
                       class="delete-icon"
                       title="Delete"
                       onclick="return confirm('Delete this brand?')">
                        🗑
                    </a>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

</body>
</html>