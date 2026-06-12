<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Node</title>
    <style>
        body {
            font-family: "Segoe UI", Arial, sans-serif;
            background-color: #FFF8F0;
            margin: 0;
            padding: 0;
        }

        .container {
            width: 460px;
            margin: 90px auto;
            background-color: #ffffff;
            padding: 28px;
            border-radius: 18px;
            box-shadow: 0 18px 35px rgba(75, 46, 43, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 22px;
            color: #4B2E2B;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 22px;
        }

        th {
            background-color: #4B2E2B;
            color: #FFF8F0;
            padding: 12px;
            text-align: left;
            width: 35%;
            font-weight: 600;
            border-radius: 6px 0 0 6px;
        }

        td {
            padding: 12px;
            background-color: #fff3eb;
            border-radius: 0 6px 6px 0;
        }

        tr {
            border-bottom: 10px solid transparent;
        }

        input[type="text"] {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccb7b2;
            border-radius: 6px;
            font-size: 14px;
        }

        .multi-role-box {
            background-color: #fff3eb;
            border: 1px solid #e6d3cd;
            border-radius: 10px;
            padding: 10px;
            max-height: 140px;
            overflow-y: auto;
        }

        .role-item {
            display: flex;
            align-items: center;
            font-size: 14px;
            margin-bottom: 6px;
            color: #4B2E2B;
            font-weight: 500;
        }

        .role-item input {
            margin-right: 8px;
            accent-color: #4B2E2B;
        }

        .btn-save {
            padding: 12px;
            width: 100%;
            font-size: 15px;
            border-radius: 10px;
            border: none;
            cursor: pointer;
            font-weight: 600;
            background-color: #4B2E2B;
            color: #FFF8F0;
        }

        .btn-save:hover {
            background-color: #3a2421;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Edit Node</h2>
    <form action="/node/update" method="post">
        <table>
            <tr>
                <th>ID</th>
                <td>
                    <input type="text" name="id" value="${node.id}" readonly>
                </td>
            </tr>
            <tr>
                <th>Identifier</th>
                <td>
                    <input type="text" name="identifier" value="${node.identifier}" readonly>
                </td>
            </tr>
            <tr>
                <th>Path</th>
                <td>
                    <input type="text" name="path" value="${node.path}">
                </td>
            </tr>
            <tr>
                <th>Roles</th>
                <td>
                    <div class="multi-role-box">
                        <c:forEach items="${roles}" var="role">
                            <div class="role-item">
                                <input type="checkbox"
                                       id="role_${role.identifier}"
                                       name="roles"
                                       value="${role.identifier}"
                                       <c:if test="${fn:contains(node.roles, role.identifier)}">
                                           checked
                                       </c:if> />
                                <label for="role_${role.identifier}">
                                    ${role.identifier}
                                </label>
                            </div>
                        </c:forEach>
                    </div>
                </td>
            </tr>
        </table>
        <input type="submit" value="Save" class="btn-save">
    </form>
</div>
</body>
</html>