<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Racks Management | POS</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", sans-serif;
            padding: 40px;
            background: radial-gradient(circle at 20% 30%, rgba(255,122,0,0.15), transparent 40%),
                        radial-gradient(circle at 80% 70%, rgba(255,72,0,0.15), transparent 40%),
                        linear-gradient(135deg, #1f1f1f, #3a2e2a);
        }

        .container {
            max-width: 1100px;
            margin: auto;
        }

        .card {
            background: rgba(255,255,255,0.12);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
            border: 1px solid rgba(255,255,255,0.15);
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        h5 {
            color: #fff;
            margin: 0;
        }

        .add-btn {
            padding: 10px 16px;
            border-radius: 20px;
            border: none;
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: #fff;
            text-decoration: none;
            font-size: 13px;
            cursor: pointer;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
            color: #fff;
        }

        th {
            background: rgba(255,255,255,0.1);
            padding: 12px;
            font-size: 13px;
            text-align: left;
        }

        td {
            padding: 12px;
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }

        tr:hover {
            background: rgba(255,255,255,0.05);
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 48px;
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
            background-color: rgba(255,255,255,0.3);
            transition: .4s;
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
            transition: .4s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #ff4800;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }

        .actions {
            display: flex;
            gap: 12px;
            align-items: center;
        }

        .icon-btn {
            border: none;
            background: transparent;
            cursor: pointer;
            font-size: 15px;
            text-decoration: none;
        }

        .edit-icon {
            color: #ff7a00;
        }

        .delete-icon {
            color: #ff4d4d;
        }

        .icon-btn:hover {
            transform: scale(1.1);
        }

        .empty {
            text-align: center;
            color: #ffb3b3;
            padding: 20px;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="card">

        <div class="header">
            <h5>Racks List</h5>

            <button class="add-btn"
                    onclick="window.location.href='/racks/add'">
                + Add Rack
            </button>
        </div>

        <table>

            <thead>
                <tr>
                    <th>ID</th>
                    <th>Rack Name</th>
                    <th>Shelves</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>

            <tbody>

                <c:if test="${empty racks}">
                    <tr>
                        <td colspan="5" class="empty">
                            No racks found
                        </td>
                    </tr>
                </c:if>

                <c:forEach var="rack" items="${racks}" varStatus="i">
                    <tr>

                        <td>${i.count}</td>

                        <td>${rack.identifier}</td>

                        <td>${rack.shelves}</td>

                        <td>
                            <label class="switch">
                                <input type="checkbox"
                                       ${rack.status ? 'checked' : ''}
                                       onchange="toggleStatus('${rack.identifier}')">

                                <span class="slider"></span>
                            </label>
                        </td>

                        <td>
                            <div class="actions">

                                <a href="/racks/get?identifier=${rack.identifier}"
                                   class="icon-btn edit-icon">
                                    <i class="fas fa-pen"></i>
                                </a>

                                <a href="/racks/delete?identifier=${rack.identifier}"
                                   class="icon-btn delete-icon"
                                   onclick="return confirm('Delete this rack?')">
                                    <i class="fas fa-trash"></i>
                                </a>

                            </div>
                        </td>

                    </tr>
                </c:forEach>

            </tbody>

        </table>

    </div>

</div>

<script>
    function toggleStatus(identifier) {
        window.location.href =
            '/racks/toggle?identifier=' + identifier;
    }
</script>

</body>
</html>