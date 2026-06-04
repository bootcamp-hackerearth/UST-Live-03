<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>POS Management | Warehouse Profile</title>

<style>
    body {
        margin: 0;
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        background: #F4F5F7;
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .profile-card {
        background: #FFFFFF;
        max-width: 450px;
        width: 100%;
        border-radius: 16px;
        box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        overflow: hidden;
    }

    .brand-header {
        background: #0B3C5D;
        padding: 25px;
        color: white;
        text-align: center;
    }

    .profile-body {
        padding: 30px 40px;
    }

    h2 {
        text-align: center;
        margin-bottom: 20px;
        font-weight: 600;
    }

    .profile-row {
        display: flex;
        justify-content: space-between;
        padding: 14px 0;
        align-items: center;
    }

    .label {
        width: 40%;
        font-weight: 700;
        color: #4B5563;
    }

    .value {
        width: 60%;
        text-align: right;
        font-weight: 600;
    }

    .readonly {
        color: #6B7280;
    }

    input[type="text"] {
        width: 60%;
        padding: 8px 10px;
        border-radius: 8px;
        border: 1px solid #E5E7EB;
        display: none;
        box-sizing: border-box;
    }

    .editable {
        display: none;
    }

    .actions {
        text-align: center;
        margin-top: 25px;
    }

    .btn {
        padding: 10px 24px;
        border-radius: 8px;
        border: none;
        cursor: pointer;
        font-weight: 600;
    }

    .edit-btn { background: #0B3C5D; color: white; }
    .save-btn { background: #16A34A; color: white; display: none; }

    .back-link {
        display: block;
        margin-top: 20px;
        font-size: 13px;
        color: #6B7280;
        text-decoration: none;
        font-weight: 600;
        text-align: center;
    }

    .back-link:hover {
        color: #0B3C5D;
        text-decoration: underline;
    }
</style>
</head>

<body>
<div class="profile-card">
    <div class="brand-header">
        <h1>POS Management</h1>
    </div>

    <div class="profile-body">
        <form action="${pageContext.request.contextPath}/warehouse/update" method="post">

            <input type="hidden" name="id" value="${warehouse.id}" />
            <input type="hidden" name="identifier" value="${warehouse.identifier}" />

            <h2>Warehouse Configuration</h2>

            <!-- ✅ READONLY -->
            <div class="profile-row">
                <span class="label">Warehouse Name</span>
                <span class="value readonly">${warehouse.identifier}</span>
            </div>

            <!-- ✅ EDITABLE -->
            <div class="profile-row">
                <span class="label">Location</span>
                <span class="value editable-value">${warehouse.location}</span>
                <input class="editable" type="text"
                       name="location"
                       value="${warehouse.location}" />
            </div>

            <!-- ✅ EDITABLE -->
            <div class="profile-row">
                <span class="label">Manager</span>
                <span class="value editable-value">${warehouse.manager}</span>
                <input class="editable" type="text"
                       name="manager"
                       value="${warehouse.manager}" />
            </div>

            <div class="actions">
                <button type="button"
                        class="btn edit-btn"
                        onclick="enableEdit()">Edit Warehouse</button>
                <button type="submit"
                        class="btn save-btn">Save Changes</button>
            </div>

            <a href="${pageContext.request.contextPath}/warehouse/list"
               class="back-link">
                ← Back to Warehouse List
            </a>

        </form>
    </div>
</div>

<script>
    function enableEdit() {

        /* Hide ONLY editable display values */
        document.querySelectorAll('.editable-value')
            .forEach(v => v.style.display = 'none');

        /* Show inputs */
        document.querySelectorAll('.editable')
            .forEach(i => i.style.display = 'inline-block');

        document.querySelector('.edit-btn').style.display = 'none';
        document.querySelector('.save-btn').style.display = 'inline-block';

        document.querySelector('h2').innerText = "Edit Warehouse Details";
    }
</script>

</body>
</html>
