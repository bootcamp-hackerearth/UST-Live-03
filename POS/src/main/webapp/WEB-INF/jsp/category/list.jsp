<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Category Dashboard</title>

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
    padding: 50px 20px;
}

/* Container */
.container {
    width: 96%;
    max-width: 1200px;
    margin: auto;
}

/* Title */
h2 {
    text-align: center;
    margin-bottom: 35px;
    font-weight: 600;
    font-size: 28px;
    color: #0f172a;
}

/* Top Actions */
.top-actions {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
}

/* Buttons */
.btn {
    display: inline-block;
    padding: 10px 20px;
    border-radius: 6px;
    text-decoration: none;
    font-size: 14px;
    color: white;
    transition: 0.2s;
}

.home-btn {
    background: #0ea5e9;
}

.home-btn:hover {
    background: #0284c7;
}

.add-btn {
    background: #6366f1;
}

.add-btn:hover {
    background: #4f46e5;
}

/* Card */
.table-container {
    background: #ffffff;
    border-radius: 14px;
    padding: 25px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 10px 25px rgba(0,0,0,0.06);
}

/* Table */
table {
    width: 100%;
    border-collapse: collapse;
}

/* Header */
thead {
    background: #6366f1;
    color: white;
}

th {
    padding: 16px;
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

/* Rows */
td {
    padding: 14px;
    text-align: center;
    font-size: 14px;
    border-bottom: 1px solid #e5e7eb;
}

tbody tr:hover {
    background: #f1f5f9;
}

/* Badge */
.badge {
    padding: 5px 12px;
    border-radius: 8px;
    font-size: 13px;
    background: #e0e7ff;
    color: #3730a3;
    font-weight: 500;
}

/* Actions */
.action-btn {
    display: inline-block;
    padding: 6px 12px;
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

/* Empty */
.empty {
    padding: 25px;
    color: #64748b;
    font-size: 15px;
}

/* Bottom Buttons */
.btn-container {
    text-align: center;
    margin-top: 30px;
}

/* TOGGLE */
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
    height: 16px;
    width: 16px;
    left: 3px;
    bottom: 3px;
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

/* Status label */
.status-label {
    margin-left: 8px;
    font-size: 13px;
    font-weight: 500;
}

.status-label.active { color: #10b981; }
.status-label.inactive { color: #ef4444; }

/* Toast */
.toast {
    position: fixed;
    top: 20px;
    right: 20px;
    background: #1e3a8a;
    color: white;
    padding: 12px 18px;
    border-radius: 8px;
    display: none;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

</style>
</head>

<body>

<div class="container">

    <h2>Category Management</h2>

    <div class="table-container">

        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Category</th>
                <th>Super Category</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>

            <c:choose>

                <c:when test="${not empty category}">
                    <c:forEach var="cat" items="${category}">

                        <tr>

                            <!-- ID -->
                            <td>${cat.id}</td>

                            <!-- Category Name -->
                            <td>
                                ${cat.identifier}
                            </td>

                            <!-- Super Category -->
                            <td>
                                <c:choose>
                                    <c:when test="${not empty cat.superCategory}">
                                        <span class="badge">${cat.superCategory}</span>
                                    </c:when>
                                    <c:otherwise>
                                        -
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <!-- Status -->
                            <td>
                                <label class="switch">
                                    <input type="checkbox"
                                    <c:if test="${cat.status}">checked</c:if>
                                    onchange="toggleStatus('${cat.identifier}', this)">
                                    <span class="slider"></span>
                                </label>

                                <span class="status-label ${cat.status ? 'active' : 'inactive'}">
                                    <c:choose>
                                        <c:when test="${cat.status}">
                                            Active
                                        </c:when>
                                        <c:otherwise>
                                            Inactive
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </td>

                            <!-- Actions -->
                            <td>

                               <c:url var="editUrl" value="/category/get">
                                   <c:param name="identifier" value="${cat.identifier}" />
                               </c:url>

                               <a class="action-btn edit-btn" href="${editUrl}">
                                   Edit
                               </a>

                                <a class="action-btn delete-btn"
                                   href="${pageContext.request.contextPath}/category/delete?identifier=${cat.identifier}"
                                   onclick="return confirm('Are you sure you want to delete this category?');">
                                    Delete
                                </a>

                            </td>

                        </tr>

                    </c:forEach>
                </c:when>

                <c:otherwise>
                    <tr>
                        <td colspan="4" class="empty">
                            No category records found
                        </td>
                    </tr>
                </c:otherwise>

            </c:choose>

            </tbody>

        </table>

    </div>

    <!-- Bottom Buttons -->
    <div class="btn-container">

        <a href="${pageContext.request.contextPath}/" class="btn home-btn">
            Go to Home
        </a>

        <a href="${pageContext.request.contextPath}/category/add" class="btn add-btn">
            Add New Category
        </a>

    </div>

</div>

<!-- Toast -->
<div id="toast" class="toast">Status Updated</div>

<script>

function toggleStatus(identifier, el) {

    let status = el.checked;

    fetch('${pageContext.request.contextPath}/category/toggle', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'identifier=' + encodeURIComponent(identifier) + '&status=' + status
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