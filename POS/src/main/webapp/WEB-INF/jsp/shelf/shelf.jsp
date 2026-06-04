<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Retail Management | Shelf Details</title>

    <style>
        body {
            background: #F4F5F7;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            font-family: "Segoe UI";
        }

        .profile-card {
            width: 450px;
            background: white;
            border-radius: 16px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        }

        .brand-header {
            background: #0B3C5D;
            padding: 25px;
            text-align: center;
            color: white;
        }

        .profile-body {
            padding: 30px 40px;
        }

        .profile-row {
            display: flex;
            justify-content: space-between;
            padding: 14px 0;
        }

        .editable {
            display: none;
        }

        .btn {
            padding: 10px 24px;
            border-radius: 8px;
            border: none;
            font-weight: 600;
        }

        .edit-btn { background: #0B3C5D; color: white; }
        .save-btn { background: #16A34A; color: white; display: none; }
    </style>
</head>

<body>

<div class="profile-card">
    <div class="brand-header">
        <h1>POS Retail Management</h1>
    </div>

    <div class="profile-body">

        <form action="${pageContext.request.contextPath}/shelf/update" method="post">
            <input type="hidden" name="id" value="${shelf.id}" />
            <input type="hidden" name="identifier" value="${shelf.identifier}" />
            <h2>Shelf Configuration</h2>
            <div class="profile-row">
                <span>Shelf Identifier</span>
                <span class="value">${shelf.identifier}</span>
            </div>

            <div class="profile-row">
                <span>Status</span>
                <span class="value">${shelf.status ? 'Active' : 'Inactive'}</span>
                <input class="editable"
                       type="checkbox"
                       name="status"
                       <c:if test="${shelf.status}">checked</c:if> />
            </div>

            <div class="actions" style="text-align:center; margin-top:25px;">
                <button type="button"
                        class="btn edit-btn"
                        onclick="enableEdit()">Edit Shelf</button>
                <button type="submit"
                        class="btn save-btn">Save Changes</button>
            </div>

            <a href="${pageContext.request.contextPath}/shelf/list"
               class="back-link">
                ← Back to Shelf List
            </a>

        </form>
    </div>
</div>

<script>
    function enableEdit() {
        document.querySelectorAll('.value')
            .forEach(v => v.style.display = 'none');
        document.querySelectorAll('.editable')
            .forEach(e => e.style.display = 'inline-block');
        document.querySelector('.edit-btn').style.display = 'none';
        document.querySelector('.save-btn').style.display = 'inline-block';
    }
</script>

</body>
</html>