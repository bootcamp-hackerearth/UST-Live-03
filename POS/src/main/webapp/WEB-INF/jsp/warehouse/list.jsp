<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Warehouse Dashboard</title>

<style>

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Segoe UI', Arial, sans-serif;
    background: linear-gradient(135deg, #eef2ff, #f8fafc);
    color: #1e293b;
    padding: 40px 20px;
}

.container {
    width: 95%;
    max-width: 1100px;
    margin: auto;
}

h2 {
    text-align: center;
    margin-bottom: 30px;
    font-weight: 600;
    color: #0f172a;
}

.table-container {
    background: #ffffff;
    border-radius: 12px;
    padding: 20px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 8px 20px rgba(0,0,0,0.05);
}

table {
    width: 100%;
    border-collapse: collapse;
}

thead {
    background: #6366f1;
    color: white;
}

th {
    padding: 14px;
    font-size: 12px;
    text-transform: uppercase;
}

td {
    padding: 12px;
    text-align: center;
    font-size: 14px;
    border-bottom: 1px solid #e5e7eb;
}

tbody tr:hover {
    background: #f1f5f9;
}

.badge {
    padding: 4px 10px;
    border-radius: 6px;
    font-size: 12px;
    background: #e0e7ff;
    color: #3730a3;
}

.action-btn {
    display: inline-block;
    padding: 6px 10px;
    margin: 2px;
    border-radius: 6px;
    font-size: 12px;
    text-decoration: none;
    color: white;
}

.edit-btn {
    background: #6366f1;
}

.edit-btn:hover {
    background: #4f46e5;
}

.delete-btn {
    background: #ef4444;
}

.delete-btn:hover {
    background: #dc2626;
}

.empty {
    padding: 20px;
    color: #64748b;
}

.btn-container {
    text-align: center;
    margin-top: 25px;
}

.btn {
    display: inline-block;
    padding: 10px 20px;
    margin: 8px;
    border-radius: 6px;
    text-decoration: none;
    font-size: 14px;
    color: white;
}

.add-btn {
    background: #6366f1;
}

.add-btn:hover {
    background: #4f46e5;
}

.home-btn {
    background: #0ea5e9;
}

.home-btn:hover {
    background: #0284c7;
}

.switch {
    position: relative;
    display: inline-block;
    width: 42px;
    height: 22px;
}

.switch input { display: none; }

.slider {
    position: absolute;
    cursor: pointer;
    top: 0; left: 0; right: 0; bottom: 0;
    background: #ccc;
    border-radius: 22px;
    transition: .3s;
}

.slider:before {
    position: absolute;
    content: "";
    height: 16px; width: 16px;
    left: 3px; bottom: 3px;
    background: white;
    border-radius: 50%;
    transition: .3s;
}

input:checked + .slider {
    background: #10b981;
}

input:checked + .slider:before {
    transform: translateX(20px);
}

.status-label {
    margin-left: 8px;
    font-size: 13px;
    font-weight: 500;
    color: #475569;
}

.status-label.active { color: #10b981; }
.status-label.inactive { color: #ef4444; }

.toast {
    position: fixed;
    top: 20px;
    right: 20px;
    background: #1e3a8a;
    color: white;
    padding: 12px 18px;
    border-radius: 8px;
    display: none;
}

</style>
</head>

<body>

<div class="container">

    <h2>Warehouse Management</h2>

    <div class="table-container">

        <table>
            <thead>
            <tr>
                <th>SL No</th>
                <th>Warehouse Name</th>
                <th>Country</th>
                <th>Region</th>
                <th>Address</th>
                <th>Phone Number</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>

            <c:choose>

                <c:when test="${not empty warehouses}">
                    <c:forEach var="warehouse" items="${warehouses}">

                        <tr>

                            <td>${warehouse.id}</td>

                            <td><span class="badge">${warehouse.identifier}</span></td>

                            <td>${warehouse.country}</td>

                            <td>${warehouse.region}</td>

                            <td>${warehouse.address}</td>

                            <td>${warehouse.phoneNumber}</td>

                            <td>
                                <label class="switch">
                                    <input type="checkbox"
                                           <c:if test="${warehouse.status}">checked</c:if>
                                           onchange="toggleStatus('${warehouse.identifier}', this)">
                                    <span class="slider"></span>
                                </label>

                                <span class="status-label">
                                    <c:choose>
                                        <c:when test="${warehouse.status}">
                                            Active
                                        </c:when>
                                        <c:otherwise>
                                            Inactive
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </td>

                            <td>

                                <a class="action-btn edit-btn"
                                   href="${pageContext.request.contextPath}/warehouse/get?identifier=${warehouse.identifier}">
                                    Edit
                                </a>

                                <a class="action-btn delete-btn"
                                   href="${pageContext.request.contextPath}/warehouse/delete?identifier=${warehouse.identifier}"
                                   onclick="return confirm('Are you sure you want to delete this warehouse?');">
                                    Delete
                                </a>

                            </td>

                        </tr>

                    </c:forEach>
                </c:when>

                <c:otherwise>
                    <tr>
                        <td colspan="6" class="empty">
                            No warehouse records found
                        </td>
                    </tr>
                </c:otherwise>

            </c:choose>

            </tbody>

        </table>

    </div>

    <div class="btn-container">

        <a href="${pageContext.request.contextPath}/" class="btn add-btn">
            Go to Home
        </a>

        <a href="${pageContext.request.contextPath}/warehouse/add" class="btn home-btn">
            Add New Warehouse
        </a>

    </div>

</div>

<div id="toast" class="toast">Status Updated</div>

<script>
function toggleStatus(identifier, el) {

    let status = el.checked;

    fetch('${pageContext.request.contextPath}/warehouse/toggle', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'identifier=' + identifier + '&status=' + status
    })
    .then(res => res.text())
    .then(() => {

        let label = el.closest("td").querySelector(".status-label");
        label.innerText = status ? "Active" : "Inactive";

        label.classList.remove("active", "inactive");
        label.classList.add(status ? "active" : "inactive");

        showToast();
    })
    .catch(() => {
        alert("Error updating status");
        el.checked = !status;
    });
}

function showToast() {
    let t = document.getElementById("toast");
    t.style.display = "block";
    setTimeout(() => t.style.display = "none", 800);
}
</script>

</body>
</html>