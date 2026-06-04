<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Unit Management | POS</title>

    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet"/>

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
            max-width: 1000px;
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

        h3 {
            text-align: center;
            margin-bottom: 20px;
            color: #fff;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            color: #fff;
        }

        th {
            padding: 12px;
            font-size: 14px;
            text-align: left;
            color: #ddd;
            border-bottom: 1px solid rgba(255,255,255,0.2);
        }

        td {
            padding: 12px;
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }

        tr:hover {
            background: rgba(255,255,255,0.08);
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .btn-add {
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            border: none;
            border-radius: 20px;
            padding: 8px 16px;
            color: white;
            font-size: 13px;
            cursor: pointer;
        }

        .btn-add:hover {
            opacity: 0.9;
        }

        .toggle {
            width: 45px;
            height: 24px;
            border-radius: 20px;
            position: relative;
            display: inline-block;
            cursor: pointer;
            background: rgba(255,255,255,0.3);
            transition: 0.3s;
        }

        .toggle::before {
            content: '';
            position: absolute;
            width: 20px;
            height: 20px;
            border-radius: 50%;
            top: 2px;
            left: 2px;
            background: #ffffff;
            transition: 0.3s;
        }

        .toggle.active {
            background: #ff4800;
        }

        .toggle.active::before {
            transform: translateX(21px);
        }

        .actions {
            display: flex;
            gap: 18px;
        }

        .icon-btn {
            border: none;
            background: transparent;
            cursor: pointer;
            font-size: 15px;
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
            padding: 20px;
            color: #ccc;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="card">

        <div class="top-bar">
            <h3>Unit Management</h3>

            <button class="btn-add" onclick="window.location.href='/unit/add'">
                + Add Unit
            </button>
        </div>

        <c:if test="${empty units}">
            <div class="empty">No units found</div>
        </c:if>

        <c:if test="${not empty units}">
            <table>

                <thead>
                    <tr>
                        <th>SL</th>
                        <th>Unit Name</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach var="u" items="${units}" varStatus="i">
                        <tr>

                            <td>${i.count}</td>

                            <td>${u.identifier}</td>

                            <td>
                                <div class="toggle ${u.status ? 'active' : ''}"
                                     onclick="toggleStatus('${u.identifier}', this)">
                                </div>
                            </td>

                            <td>
                                <div class="actions">
                                    <a href="/unit/get?identifier=${u.identifier}" class="icon-btn edit-icon">
                                        <i class="fas fa-pen"></i>
                                    </a>

                                    <a href="/unit/delete?identifier=${u.identifier}"
                                       class="icon-btn delete-icon"
                                       onclick="return confirm('Delete this unit?')">
                                        <i class="fas fa-trash"></i>
                                    </a>
                                </div>
                            </td>

                        </tr>
                    </c:forEach>
                </tbody>

            </table>
        </c:if>

    </div>

</div>

<script>
function toggleStatus(identifier, element) {
    fetch('/unit/toggle?identifier=' + identifier, {
        method: 'POST'
    })
    .then(response => response.text())
    .then(() => {
        element.classList.toggle('active');
    })
    .catch(error => {
        alert('Error updating status');
        console.error(error);
    });
}
</script>

</body>
</html>