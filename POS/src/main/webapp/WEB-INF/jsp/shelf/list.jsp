<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Shelf List</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            padding: 20px;
        }

        h2 { margin-bottom: 15px; }

        .btn {
            padding: 7px 14px;
            text-decoration: none;
            border-radius: 4px;
            color: #fff;
            font-size: 14px;
            margin-right: 5px;
        }

        .btn-home { background-color: #6c757d; } /* Home */
        .btn-add { background-color: #28a745; }
        .btn-edit { background-color: #007bff; }
        .btn-delete { background-color: #dc3545; }

        table {
            width: 100%;
            border-collapse: collapse;
            background: #fff;
        }

        th, td {
            padding: 12px;
            border-bottom: 1px solid #ddd;
            text-align: center;
        }

        th {
            background-color: #007bff;
            color: #fff;
        }

        tr:hover { background-color: #f1f1f1; }

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
            transition: .4s;
            border-radius: 34px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: .4s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #28a745;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }

        .no-data {
            font-style: italic;
            text-align: center;
        }
    </style>
</head>

<body>

<h2>Shelf List</h2>

<!-- Home + Add Shelf buttons -->
<a href="${pageContext.request.contextPath}/"
   class="btn btn-home">
    Home
</a>

<a href="${pageContext.request.contextPath}/shelf/add"
   class="btn btn-add">
    + Add Shelf
</a>

<br/><br/>

<table>
    <thead>
        <tr>
            <th>ID</th>
            <th>Identifier</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
    </thead>

    <tbody>
        <c:forEach var="shelf" items="${categories}">
            <tr>
                <td>${shelf.id}</td>
                <td>${shelf.identifier}</td>

                <!-- Status Toggle -->
                <td class="status-cell">
                    <form method="get"
                          action="${pageContext.request.contextPath}/shelf/toggle">

                        <input type="hidden" name="identifier"
                               value="${shelf.identifier}" />

                        <label class="switch">
                            <input type="checkbox"
                                   name="status"
                                   onchange="this.form.submit()"
                                   <c:if test="${shelf.status}">checked</c:if> />
                            <span class="slider"></span>
                        </label>
                    </form>
                </td>

                <td>
                    <a href="${pageContext.request.contextPath}/shelf/update?identifier=${shelf.identifier}"
                       class="btn btn-edit">
                        Edit
                    </a>

                    <a href="${pageContext.request.contextPath}/shelf/delete?id=${shelf.id}"
                       class="btn btn-delete"
                       onclick="return confirm('Are you sure you want to delete this shelf?');">
                        Delete
                    </a>
                </td>
            </tr>
        </c:forEach>

        <c:if test="${empty categories}">
            <tr>
                <td colspan="4" class="no-data">
                    No shelves found
                </td>
            </tr>
        </c:if>
    </tbody>
</table>

</body>
</html>
