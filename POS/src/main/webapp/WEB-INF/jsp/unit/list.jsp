<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Unit Management | POS</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            margin: 0;
            font-family: 'Inter', 'Segoe UI';
            background: linear-gradient(135deg, #f6f8fb, #eef2f7);
        }

        .topbar {
            height: 70px;
            background: linear-gradient(135deg, #4f46e5, #6366f1);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 30px;
            color: #ffffff;
        }

        .content {
            padding: 40px;
        }

        .card {
            background: #ffffff;
            border-radius: 18px;
            padding: 25px;
            box-shadow: 0 25px 60px rgba(0,0,0,0.08);
        }

        table {
            font-size: 14px;
        }

        .toggle {
            width: 45px;
            height: 24px;
            border-radius: 20px;
            position: relative;
            display: inline-block;
            cursor: pointer;
            background: #d1d5db;
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
            background: #dc2626;
        }

        .toggle.active::before {
            transform: translateX(21px);
        }

        .edit {
            color: #4f46e5;
            margin-right: 10px;
            text-decoration: none;
            font-weight: 500;
        }

        .delete {
            color: #dc2626;
            text-decoration: none;
            font-weight: 500;
        }

        .empty {
            text-align: center;
            padding: 40px;
            color: #6b7280;
        }
    </style>
</head>

<body>

<div class="topbar">
    <h5>Unit Management</h5>

    <button class="btn btn-light btn-sm"
            onclick="window.location.href='/'">
        Dashboard
    </button>
</div>

<div class="content">

    <div class="card">

        <div class="d-flex justify-content-between mb-3">
            <h5>Unit List</h5>

            <button class="btn btn-primary"
                    onclick="window.location.href='/unit/add'">
                + Add Unit
            </button>
        </div>

        <c:if test="${empty units}">
            <div class="empty">
                No units found
            </div>
        </c:if>

        <c:if test="${not empty units}">
            <table class="table table-hover">

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
                                <a href="/unit/get?identifier=${u.identifier}"
                                   class="edit">
                                    Edit
                                </a>

                                <a href="/unit/delete?identifier=${u.identifier}"
                                   class="delete"
                                   onclick="return confirm('Delete this unit?')">
                                    Delete
                                </a>
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