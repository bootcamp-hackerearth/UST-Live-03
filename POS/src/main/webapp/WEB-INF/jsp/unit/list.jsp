<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Retail POS | Unit List</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"/>

    <style>
        :root {
            --primary: #0f172a;
            --bg-light: #f8fafc;
            --border: #e2e8f0;
            --card-bg: #ffffff;
            --success: #22c55e;
        }

        body {
            margin: 0;
            min-height: 100vh;
            background: var(--bg-light);
            font-family: system-ui, -apple-system, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 40px;
        }

        .container {
            width: 100%;
            max-width: 900px;
            background: var(--card-bg);
            padding: 30px;
            border-radius: 12px;
            border-top: 5px solid var(--primary);
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 14px;
            border-bottom: 1px solid var(--border);
            text-align: center;
            font-size: 14px;
        }

        th {
            background: #f1f5f9;
            font-size: 12px;
            text-transform: uppercase;
            color: #64748b;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 40px;
            height: 20px;
            vertical-align: middle;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            inset: 0;
            cursor: pointer;
            background-color: #cbd5e1;
            border-radius: 34px;
            transition: .3s;
        }

        .slider:before {
            content: "";
            position: absolute;
            height: 14px;
            width: 14px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            border-radius: 50%;
            transition: .3s;
        }

        input:checked + .slider {
            background-color: var(--success);
        }

        input:checked + .slider:before {
            transform: translateX(20px);
        }

        .status-label {
            margin-left: 8px;
            font-size: 11px;
            font-weight: 700;
            min-width: 70px;
            display: inline-block;
        }

        .text-active { color: var(--success); }
        .text-inactive { color: #94a3b8; }

        .action-icon {
            margin: 0 8px;
            color: var(--primary);
            font-size: 16px;
            text-decoration: none;
        }

        .delete:hover { color: red; }

        .empty {
            background: #fef3c7;
            padding: 15px;
            text-align: center;
            border-radius: 6px;
        }

        .footer {
            margin-top: 25px;
            text-align: center;
        }

        .footer a {
            margin: 0 14px;
            font-weight: 600;
            text-decoration: none;
            color: var(--primary);
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Registered Units</h2>

    <c:if test="${empty units}">
        <div class="empty">No units found</div>
    </c:if>

    <c:if test="${not empty units}">
        <table>
            <thead>
            <tr>
                <th>Sl.</th>
                <th>Unit Name</th>
                <th>Description</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="unit" items="${units}" varStatus="loop">
                <tr>
                    <td>${loop.index + 1}</td>
                    <td><strong>${unit.identifier}</strong></td>
                     <td>${unit.description}</td>

                    <td>
                        <label class="switch">
                            <input type="checkbox"
                                   class="unit-toggle"
                                   data-id="${unit.identifier}"
                                   ${unit.status ? 'checked' : ''}>
                            <span class="slider"></span>
                        </label>

                        <span class="status-label ${unit.status ? 'text-active' : 'text-inactive'}">
                            ${unit.status ? 'ACTIVE' : 'INACTIVE'}
                        </span>
                    </td>

                    <td>
                        <a class="action-icon"
                           href="${pageContext.request.contextPath}/unit/get?identifier=${unit.identifier}">
                            <i class="fa fa-pen"></i>
                        </a>

                        <a class="action-icon delete"
                           href="${pageContext.request.contextPath}/unit/delete?identifier=${unit.identifier}"
                           onclick="return confirm('Delete this unit?')">
                            <i class="fa fa-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div class="footer">
        <a href="${pageContext.request.contextPath}/">Home</a>
        <a href="${pageContext.request.contextPath}/unit/add">Add Unit</a>
    </div>

</div>

<script>
    const contextPath = '${pageContext.request.contextPath}';

    document.querySelectorAll('.unit-toggle').forEach(toggle => {
        toggle.addEventListener('change', function () {

            const identifier = this.dataset.id;
            const status = this.checked;
            const label = this.closest('td').querySelector('.status-label');

            fetch(
                contextPath + '/unit/toggle?identifier=' +
                encodeURIComponent(identifier) +
                '&status=' + status,
                { method: 'POST' }
            )
            .then(response => {
                if (!response.ok) throw new Error();
                label.textContent = status ? 'ACTIVE' : 'INACTIVE';
                label.className =
                    'status-label ' + (status ? 'text-active' : 'text-inactive');
            })
            .catch(() => {
                this.checked = !status;
                alert('Failed to update unit status');
            });
        });
    });
</script>

</body>
</html>