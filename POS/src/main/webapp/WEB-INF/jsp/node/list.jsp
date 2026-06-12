<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Node List</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        .add-node-btn {
            font-size: 18px;
            padding: 12px 30px;
            border-radius: 30px;
        }

        .back-btn {
            font-size: 18px;
            padding: 12px 30px;
            border-radius: 30px;
            margin-right: 15px;
        }

        .node-name {
            font-weight: 700;
            color: #0d6efd;
        }

        .node-path {
            font-weight: 600;
            font-family: monospace;
            color: #198754;
        }

        .card-custom {
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            border-radius: 12px;
        }
    </style>
</head>

<body>

<div class="container mt-4">

    <div class="card card-custom p-4">
        <h2 class="text-center mb-4">Node Management</h2>

        <div class="text-center mb-4">
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-secondary back-btn">
                <i class="bi bi-arrow-left-circle"></i>
                Back to Home
            </a>

            <a href="${pageContext.request.contextPath}/node/add"
               class="btn btn-success add-node-btn">
                <i class="bi bi-plus-circle"></i>
                Add New Node
            </a>
        </div>

        <table class="table table-hover table-bordered align-middle">
            <thead class="table-dark text-center">
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Path</th>
                <th>Roles</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="node" items="${nodes}">
                <tr>
                    <td class="text-center">${node.id}</td>

                    <td>
                        <span class="node-name">${node.identifier}</span>
                    </td>

                    <td>
                        <span class="node-path">${node.path}</span>
                    </td>

                    <td>${node.roles}</td>

                    <td class="text-center">
                       <a href="${pageContext.request.contextPath}/node/get?identifier=${node.identifier}"
                           class="btn btn-sm btn-warning">
                            <i class="bi bi-pencil-square"></i>
                            Edit
                        </a>

                        <a href="${pageContext.request.contextPath}/node/delete?identifier=${node.identifier}"
                           class="btn btn-sm btn-danger"
                           onclick="return confirm('Are you sure you want to delete this node?')">
                            <i class="bi bi-trash"></i>
                            Delete
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

</div>

</body>
</html>
