<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Warehouse Management</title>

    <style>
        body { margin:0; font-family:"Segoe UI", Roboto, Arial, sans-serif; background:#f6f7f9; }

        /* TOP BAR */
        .topbar {
            height:56px; background:#020617; display:flex;
            justify-content:space-between; align-items:center;
            padding:0 20px; border-bottom:1px solid #1e293b;
        }
        .topbar-left { display:flex; align-items:center; gap:14px; }
        .top-title { color:#e5e7eb; font-weight:600; }
        .home-btn {
            padding:6px 14px; background:#1e293b; color:#e5e7eb;
            text-decoration:none; border-radius:6px; font-weight:600;
        }
        .logout-btn {
            background:#dc2626; color:white; border:none;
            padding:7px 16px; border-radius:6px; font-weight:600;
            cursor: pointer;
        }

        /* PAGE */
        .container {
            width:95%; max-width:1100px; margin:32px auto;
            background:#fff; padding:26px; border-radius:12px;
            box-shadow:0 6px 18px rgba(0,0,0,0.08);
        }
        .actions { text-align:right; margin-bottom:14px; }
        .add-btn {
            background:#2563eb; color:white; text-decoration:none;
            padding:7px 16px; border-radius:6px; font-weight:600;
        }

        table { width:100%; border-collapse:collapse; }
        th, td { padding:14px; border-bottom:1px solid #e5e7eb; text-align:center; }
        th { background:#e5e7eb; }

        .action-link {
            padding:6px 14px; border-radius:6px;
            color:white; text-decoration:none; font-weight:600;
        }
        .edit { background:#2563eb; }
        .delete { background:#dc2626; margin-left:8px; }

        .status-btn {
            border:none;
            padding:6px 16px;
            border-radius:16px;
            font-weight:600;
            cursor:pointer;
            color:#fff;
        }
        .status-active { background:#16a34a; }
        .status-inactive { background:#dc2626; }
    </style>
</head>

<body>

<div class="topbar">
    <div class="topbar-left">
        <div class="top-title">POS Application</div>
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>
    </div>

    <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
</div>

<div class="container">
    <h2 style="text-align:center;">Warehouse Management</h2>

    <div class="actions">
        <a href="${pageContext.request.contextPath}/wareHouse/add" class="add-btn">Add Warehouse</a>
    </div>

    <c:if test="${empty wareHouses}">
        <div style="text-align:center;">No warehouses found</div>
    </c:if>

    <c:if test="${not empty wareHouses}">
        <table>
<tr>
<th>Warehouse Name</th>
<th>Location</th>
<th>Manager</th>
<th>Status</th>
<th>Action</th>
</tr>

<c:forEach var="wh" items="${wareHouses}">
<tr>
<td>${wh.identifier}</td>
<td>${wh.location}</td>
<td>${wh.manager}</td>


<td>
    <button
        class="status-btn ${wh.status ? 'status-active' : 'status-inactive'}"
        onclick="toggleWarehouseStatus('${wh.identifier}', this)">
        ${wh.status ? 'Active' : 'Inactive'}
    </button>
</td>

<td>
    <a href="${pageContext.request.contextPath}/wareHouse/get?identifier=${wh.identifier}"
       class="action-link edit">Edit</a>

    <a href="${pageContext.request.contextPath}/wareHouse/delete?identifier=${wh.identifier}"
       class="action-link delete"
       onclick="return confirm('Are you sure you want to delete this warehouse?');">
        Delete
    </a>
</td>
</tr>
</c:forEach>
        </table>
    </c:if>
</div>

<script>
    function toggleWarehouseStatus(identifier, button) {
        fetch('${pageContext.request.contextPath}/wareHouse/toggle-status', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'identifier=' + encodeURIComponent(identifier)
        })
        .then(() => {
            if (button.classList.contains('status-active')) {
                button.classList.remove('status-active');
                button.classList.add('status-inactive');
                button.innerText = 'Inactive';
            } else {
                button.classList.remove('status-inactive');
                button.classList.add('status-active');
                button.innerText = 'Active';
            }
        })
        .catch(() => alert('Failed to update status'));
    }
</script>

</body>
</html>