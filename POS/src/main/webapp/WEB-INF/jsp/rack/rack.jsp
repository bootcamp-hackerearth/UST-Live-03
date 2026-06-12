<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Management | Rack Profile</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            background: #F4F5F7;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .profile-card {
            background: #FFFFFF;
            width: 100%;
            max-width: 450px;
            border-radius: 16px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .brand-header {
            background: #0B3C5D;
            padding: 25px;
            color: #FFFFFF;
            text-align: center;
        }

        .profile-body {
            padding: 30px 40px;
        }

        .profile-row {
            display: flex;
            justify-content: space-between;
            padding: 14px 0;
            align-items: center;
        }

        .label {
            width: 45%;
            font-weight: 700;
        }

        .value {
            width: 55%;
            text-align: right;
            font-weight: 600;
        }

        .readonly {
            color: #6B7280;
        }

        select {
            width: 55%;
            height: 120px;
            display: none;
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
            text-align: center;
            font-weight: 600;
            color: #6B7280;
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="profile-card">
    <div class="brand-header">
        <h1>POS Management</h1>
    </div>

    <div class="profile-body">

        <form action="${pageContext.request.contextPath}/rack/update" method="post">

            <input type="hidden" name="id" value="${rack.id}" />
            <input type="hidden" name="identifier" value="${rack.identifier}" />

            <h2>Rack Configuration</h2>

            <!-- ✅ READ ONLY -->
            <div class="profile-row">
                <span class="label">Rack Identifier</span>
                <span class="value readonly">${rack.identifier}</span>
            </div>

            <!-- ✅ EDITABLE -->
            <div class="profile-row">
                <span class="label">Shelves</span>

                <span class="value">
                    <c:forEach var="s" items="${rack.shelfs}" varStatus="st">
                        ${s}<c:if test="${!st.last}">, </c:if>
                    </c:forEach>
                </span>

                <select class="editable" name="shelfs" multiple>
                    <c:forEach var="shelf" items="${shelves}">
                        <option value="${shelf.identifier}"
                                <c:if test="${fn:contains(rack.shelfs, shelf.identifier)}">selected</c:if>>
                            ${shelf.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="actions">
                <button type="button" class="btn edit-btn" onclick="enableEdit()">Edit Rack</button>
                <button type="submit" class="btn save-btn">Save Changes</button>
            </div>

            <a href="${pageContext.request.contextPath}/rack/list" class="back-link">
                ← Back to Rack List
            </a>

        </form>
    </div>
</div>

<script>
function enableEdit() {
    document.querySelectorAll('.value:not(.readonly)').forEach(v => v.style.display = 'none');
    document.querySelectorAll('.editable').forEach(e => e.style.display = 'inline-block');

    document.querySelector('.edit-btn').style.display = 'none';
    document.querySelector('.save-btn').style.display = 'inline-block';
}
</script>

</body>
</html>