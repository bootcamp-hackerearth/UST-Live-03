<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Unit List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            padding: 30px;
        }

        .container {
            width: 85%;
            margin: auto;
            background: #ffffff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        h2 {
            text-align: center;
            margin-bottom: 24px;
            color: #1e293b;
            font-size: 22px;
            font-weight: 700;
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
        }

        .btn {
            padding: 10px 16px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            color: #ffffff;
            background-color: #1e293b;
        }

        .btn-edit {
            background-color: #2563eb;
        }

        .btn-edit:hover {
            background-color: #1e40af;
        }

        .btn-delete {
            background-color: #dc2626;
        }

        .btn-delete:hover {
            background-color: #b91c1c;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background-color: #1e293b;
            color: #ffffff;
            padding: 12px;
            font-size: 14px;
            text-align: center;
        }

        td {
            padding: 12px;
            font-size: 14px;
            color: #334155;
            text-align: center;
            border-bottom: 1px solid #e2e8f0;
        }

        tr:nth-child(even) {
            background-color: #f8fafc;
        }

        .status-cell {
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .form-switch .form-check-input {
            cursor: pointer;
            width: 2.6em;
            height: 1.4em;
        }

        .form-switch {
            margin-bottom: 0;
        }

        .action-cell {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="top-bar">
        <a href="${pageContext.request.contextPath}/" class="btn">
            Home
        </a>

        <a href="${pageContext.request.contextPath}/unit/add" class="btn">
            + Add Unit
        </a>
    </div>

    <h2>Unit List</h2>

    <table>
        <tr>
            <th>ID</th>
            <th>Identifier</th>
            <th>Unit Name</th>
            <th>Status</th>
            <th>Action</th>
        </tr>

        <c:forEach var="unit" items="${units}">
            <tr>
                <td>${unit.id}</td>
                <td>${unit.identifier}</td>
                <td>${unit.name}</td>

                <td>
                    <div class="status-cell">
                        <div class="form-check form-switch">
                            <input
                                class="form-check-input unit-toggle"
                                type="checkbox"
                                data-identifier="${unit.identifier}"
                                ${unit.status ? "checked" : ""}
                            >
                        </div>
                    </div>
                </td>

                <td class="action-cell">
                    <a href="${pageContext.request.contextPath}/unit/get?identifier=${unit.identifier}"
                       class="btn btn-edit">
                        Edit
                    </a>

                    <a href="${pageContext.request.contextPath}/unit/delete?identifier=${unit.identifier}"
                       class="btn btn-delete"
                       onclick="return confirm('Are you sure you want to delete this unit?');">
                        Delete
                    </a>
                </td>
            </tr>
        </c:forEach>

        <c:if test="${empty units}">
            <tr>
                <td colspan="5" style="text-align:center;">
                    No units found
                </td>
            </tr>
        </c:if>

    </table>

</div>

<script>
document.addEventListener("change", function (e) {
    if (!e.target.classList.contains("unit-toggle")) return;

    const checkbox = e.target;
    const identifier = checkbox.dataset.identifier;
    const status = checkbox.checked;

    fetch("${pageContext.request.contextPath}/unit/toggle", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            identifier: identifier,
            status: status
        })
    })
    .then(res => res.json())
    .then(data => {
        if (!data.success) {
            alert(data.message || "Update failed");
            checkbox.checked = !status;
        }
    })
    .catch(() => {
        alert("Failed to update unit status");
        checkbox.checked = !status;
    });
});
</script>

</body>
</html>
