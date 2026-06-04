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
            background-color: #f6f7f9;
            color: #111827;
        }

        .topbar {
            height: 56px;
            background-color: #020617;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 20px;
            border-bottom: 1px solid #1e293b;
        }

        .topbar-left {
            display: flex;
            align-items: center;
            gap: 14px;
        }

        .top-title {
            font-size: 16px;
            font-weight: 600;
            color: #e5e7eb;
        }

        .home-btn {
            padding: 7px 16px;
            background-color: #1e293b;
            color: #e5e7eb;
            text-decoration: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
            border: 1px solid #334155;
        }

        .logout-btn {
            background: #dc2626;
            border: none;
            color: white;
            padding: 7px 16px;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
        }

        .page-title {
            text-align: center;
            padding: 22px 0 14px;
            font-size: 28px;
            font-weight: 700;
            color: #020617;
        }

        .container {
            width: 95%;
            max-width: 1100px;
            margin: 0 auto 28px;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 6px 18px rgba(0,0,0,0.08);
            padding: 26px;
        }

        .list-actions {
            display: flex;
            justify-content: flex-end;
            margin-bottom: 18px;
        }

        .add-btn {
            padding: 9px 18px;
            background-color: #2563eb;
            color: #ffffff;
            text-decoration: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            font-size: 15px;
        }

        thead {
            background-color: #e5e7eb;
        }

        th {
            padding: 16px;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 0.06em;
            color: #020617;
            border-bottom: 1px solid #cbd5e1;
            text-align: center;
            font-weight: 700;
        }

        td {
            padding: 16px;
            text-align: center;
            border-bottom: 1px solid #e5e7eb;
            font-weight: 500;
        }

        tbody tr:hover {
            background-color: #f1f5f9;
        }

        .status-toggle {
            padding: 6px 14px;
            border-radius: 999px;
            font-size: 13px;
            font-weight: 600;
            color: white;
            cursor: pointer;
            border: none;
        }

        .status-true { background-color: #16a34a; }
        .status-false { background-color: #dc2626; }


        .action-link {
            padding: 7px 16px;
            border-radius: 6px;
            text-decoration: none;
            font-weight: 600;
            font-size: 14px;
            color: #ffffff;
            display: inline-block;
        }

        .edit { background-color: #2563eb; }
        .delete {
            background-color: #dc2626;
            margin-left: 8px;
        }
    </style>
</head>

<body>

<div class="topbar">
    <div class="topbar-left">
        <div class="top-title">POS Application</div>
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>
    </div>

    <form action="${pageContext.request.contextPath}/logout" method="post">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<div class="page-title">Racks Management</div>

<div class="container">

    <div class="list-actions">
        <a href="${pageContext.request.contextPath}/racks/add" class="add-btn">Add Rack</a>
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