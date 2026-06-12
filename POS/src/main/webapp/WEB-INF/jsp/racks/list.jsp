<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Racks Management</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #ffffff;
            color: #020617;
        }

        /* ===== CONTAINER ===== */
        .container {
            width: 95%;
            max-width: 1000px;
            margin: 40px auto;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            padding: 18px;
        }

        .app-title {
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #14b8a6;
            margin-bottom: 4px;
        }

        h2 {
            text-align: center;
            font-size: 22px;
            margin-bottom: 12px;
        }

        /* ===== ACTION BAR ===== */
        .list-actions {
            display: flex;
            justify-content: flex-end;
            gap: 8px;
            margin-bottom: 12px;
        }

        .home-btn {
            padding: 7px 16px;
            background: #ffffff;
            color: teal;
            text-decoration: none;
            border-radius: 18px;
            font-size: 13px;
            font-weight: 600;
            border: 1px solid teal;
        }

        .add-btn {
            padding: 7px 16px;
            background: teal;
            color: white;
            text-decoration: none;
            border-radius: 18px;
            font-size: 13px;
            font-weight: 600;
        }

        /* ===== TABLE ===== */
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
        }

        th, td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #e5e7eb;
        }

        th {
            background: #f1f5f9;
            font-weight: 700;
        }

        tbody tr:hover {
            background: #f8fafc;
        }

        /* ===== STATUS BUTTON ===== */
        .status-toggle {
            padding: 5px 12px;
            border-radius: 18px;
            font-size: 12px;
            font-weight: 600;
            color: white;
            border: none;
            cursor: pointer;
        }

        .status-true { background: teal; }
        .status-false { background: #9ca3af; }

        /* ===== ACTION BUTTONS ===== */
        .action-link {
            padding: 6px 12px;
            border-radius: 18px;
            text-decoration: none;
            font-weight: 600;
            font-size: 12px;
            color: white;
        }

        .edit {
            background: teal;
        }

        .delete {
            background: #ef4444;
            margin-left: 6px;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="app-title">POS Application</div>
    <h2>Racks Management</h2>
    <div class="list-actions">

        <!-- HOME -->
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>

        <!-- ADD -->
        <a href="${pageContext.request.contextPath}/racks/add" class="add-btn">
            Add Rack
        </a>
    </div>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Rack</th>
            <th>Shelfs</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach var="r" items="${rackss}">
            <tr>
                <td>${r.id}</td>
                <td>${r.identifier}</td>

                <td>
                    <c:choose>
                        <c:when test="${not empty r.shelfs}">
                            <c:forEach var="sh" items="${r.shelfs}" varStatus="st">
                                ${sh}<c:if test="${!st.last}">, </c:if>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>—</c:otherwise>
                    </c:choose>
                </td>

                <td>
                    <button class="status-toggle ${r.status ? 'status-true' : 'status-false'}"
                            onclick="toggleStatus('${r.identifier}')">
                        ${r.status ? 'Active' : 'Inactive'}
                    </button>
                </td>

                <td>
                    <a href="${pageContext.request.contextPath}/racks/get?identifier=${r.identifier}"
                       class="action-link edit">Edit</a>

                    <a href="${pageContext.request.contextPath}/racks/delete?identifier=${r.identifier}"
                       class="action-link delete"
                       onclick="return confirm('Are you sure?');">Delete</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>

<script>
function toggleStatus(identifier) {
    fetch('${pageContext.request.contextPath}/racks/toggle-status?identifier=' + identifier, {
        method: 'POST'
    }).then(() => location.reload());
}
</script>

</body>
</html>