<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Category Management</title>

    <style>
        body {
            margin:0;
            font-family:"Segoe UI", Roboto, Arial, sans-serif;
            background:#ffffff;
        }

        /* ===== CONTAINER ===== */
        .container {
            width:95%;
            max-width:1000px;
            margin:40px auto;
            background:#fff;
            padding:18px;
            border-radius:12px;
            box-shadow:0 4px 12px rgba(0,0,0,0.15);
        }


        .app-title {
            text-align:center;
            font-size:14px;
            font-weight:600;
            color:#14b8a6; /* light teal */
            margin-bottom:4px;
        }

        h2 {
            text-align:center;
            font-size:24px;
            margin-bottom:10px;
        }

        /* ===== ACTIONS ===== */
        .actions {
            text-align:right;
            margin-bottom:12px;
        }

        .home-btn {
            padding:7px 16px;
            background:#ffffff;
            color:teal;
            text-decoration:none;
            border-radius:18px;
            font-size:13px;
            font-weight:600;
            border:1px solid teal;
        }

        .add-btn {
            background:teal;
            color:white;
            text-decoration:none;
            padding:7px 16px;
            border-radius:18px;
            font-weight:600;
            font-size:13px;
            margin-left:6px;
        }

        /* ===== TABLE ===== */
        table {
            width:100%;
            border-collapse:collapse;
            font-size:13px;
        }

        th, td {
            padding:12px;
            border-bottom:1px solid #e5e7eb;
            text-align:center;
        }

        th {
            background:#f1f5f9;
            font-weight:700;
        }

        tr {
            line-height:1.3;
        }

        tbody tr:hover {
            background:#f8fafc;
        }

        /* ===== STATUS ===== */
        .status-toggle {
            padding:5px 12px;
            border-radius:20px;
            font-size:12px;
            font-weight:600;
            color:white;
            cursor:pointer;
            border:none;
        }

        .status-true { background-color: teal; }
        .status-false { background-color: #9ca3af; }

        /* ===== ACTION BUTTONS ===== */
        .action-link {
            padding:6px 12px;
            border-radius:18px;
            color:white;
            text-decoration:none;
            font-weight:600;
            font-size:12px;
        }

        .edit { background: teal; }
        .delete { background: #ef4444; margin-left:6px; }
    </style>
</head>

<body>

<div class="container">


    <div class="app-title">POS Application</div>

    <h2>Category Management</h2>

    <div class="actions">
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>
        <a href="${pageContext.request.contextPath}/category/add" class="add-btn">Add Category</a>
    </div>

    <c:if test="${empty categorys}">
        <div style="text-align:center;">No categories found</div>
    </c:if>

    <c:if test="${not empty categorys}">
        <table>
            <tr>
                <th>Category Name</th>
                <th>Super Category</th>
                <th>Status</th>
                <th>Action</th>
            </tr>

            <c:forEach var="cat" items="${categorys}">
                <tr>
                    <td>${cat.identifier}</td>
                    <td>${cat.superCategory}</td>

                    <td>
                        <button
                            class="status-toggle ${cat.status ? 'status-true' : 'status-false'}"
                            onclick="toggleStatus('${cat.identifier}')">
                            ${cat.status ? 'Active' : 'Inactive'}
                        </button>
                    </td>

                    <td>
                        <a href="${pageContext.request.contextPath}/category/get?identifier=${cat.identifier}"
                           class="action-link edit">Edit</a>

                        <a href="${pageContext.request.contextPath}/category/delete?identifier=${cat.identifier}"
                           class="action-link delete"
                           onclick="return confirm('Are you sure you want to delete this category?');">
                            Delete
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

</div>

<script>
    function toggleStatus(identifier) {
        fetch('${pageContext.request.contextPath}/category/toggle-status?identifier=' + identifier, {
            method: 'POST'
        }).then(() => location.reload());
    }
</script>

</body>
</html>