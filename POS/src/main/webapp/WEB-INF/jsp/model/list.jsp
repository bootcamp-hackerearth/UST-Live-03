<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Model List</title>
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
            vertical-align: middle;
        }

        td {
            padding: 12px;
            font-size: 14px;
            color: #334155;
            border-bottom: 1px solid #e2e8f0;
            text-align: center;
            vertical-align: middle;
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
        <a href="${pageContext.request.contextPath}/" class="btn">Home</a>
        <a href="${pageContext.request.contextPath}/model/add" class="btn">
            + Add Model
        </a>
    </div>

    <h2>Model List</h2>

    <table>
        <tr>
            <th>ID</th>
            <th>Identifier</th>
            <th>Model Name</th>
            <th>Status</th>
            <th>Action</th>
        </tr>

        <c:forEach var="model" items="${models}">
            <tr>
                <td>${model.id}</td>
                <td>${model.identifier}</td>
                <td>${model.name}</td>
                <!--  Status toggle -->
                <td>
                    <div class="status-cell">
                        <div class="form-check form-switch">
                            <input
                                class="form-check-input model-toggle"
                                type="checkbox"
                                data-identifier="${model.identifier}"
                                ${model.status ? "checked" : ""}
                            >
                        </div>
                    </div>
                </td>

                <td>
                    <div class="action-cell">
                        <a href="${pageContext.request.contextPath}/model/get?identifier=${model.identifier}"
                           class="btn btn-edit">
                            Edit
                        </a>
                        <a href="${pageContext.request.contextPath}/model/delete?identifier=${model.identifier}"
                           class="btn btn-delete"
                           onclick="return confirm('Are you sure you want to delete this model?');">
                            Delete
                        </a>
                    </div>
                </td>
            </tr>
        </c:forEach>
    </table>

</div>

<script>
document.addEventListener("change", function (e) {
    if (!e.target.classList.contains("model-toggle")) return;

    const checkbox = e.target;
    const identifier = checkbox.dataset.identifier;
    const status = checkbox.checked;

    fetch("/model/toggle", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ identifier, status })
    })
    .then(res => res.json())
    .then(data => {
        if (!data.success) {
            alert(data.message || "Update failed");
            checkbox.checked = !status;
        }
    })
    .catch(() => {
        alert("Failed to update model status");
        checkbox.checked = !status;
    });
});
</script>

</body>
</html>