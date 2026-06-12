<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Management | Model Profile</title>

    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: #F4F5F7;
            min-height: 100vh;
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

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #1F2937;
        }

        .profile-row {
            display: flex;
            justify-content: space-between;
            padding: 14px 0;
            align-items: center;
        }

        .label {
            font-weight: 700;
            width: 45%;
            color: #4B5563;
        }

        .value {
            width: 55%;
            text-align: right;
            font-weight: 600;
        }

        input[type="checkbox"] {
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
            cursor: pointer;
            border: none;
            font-weight: 600;
        }

        .fixed-value {
            opacity: 0.85;
            cursor: not-allowed;
        }

        .edit-btn {
            background: #0B3C5D;
            color: white;
        }

        .save-btn {
            background: #16A34A;
            color: white;
            display: none;
        }

        .back-link {
            display: block;
            margin-top: 20px;
            font-size: 13px;
            color: #6B7280;
            text-decoration: none;
            font-weight: 600;
            text-align: center;
        }
    </style>
</head>

<body>
<div class="profile-card">
    <div class="brand-header">
        <h1>POS Management</h1>
    </div>

    <div class="profile-body">
        <form action="${pageContext.request.contextPath}/models/update" method="post">

            <!-- DB identity -->
            <input type="hidden" name="id" value="${model.id}" />
            <input type="hidden" name="identifier" value="${model.identifier}" />

            <h2>Model Configuration</h2>

            <!-- Model Identifier -->
            <div class="profile-row">
                <span class="label">Model Name</span>
                <span class="value fixed-value">${model.identifier}</span>
            </div>

            <!-- Status -->
            <div class="profile-row">
                <span class="label">Status</span>

                <!-- View -->
                <span class="value">
                    <c:choose>
                        <c:when test="${model.status}">
                            Active
                        </c:when>
                        <c:otherwise>
                            Inactive
                        </c:otherwise>
                    </c:choose>
                </span>

                <!-- Edit -->
                <input class="editable"
                       type="checkbox"
                       name="status"
                       <c:if test="${model.status}">checked</c:if> />
            </div>

            <div class="actions">
                <button type="button"
                        class="btn edit-btn"
                        onclick="enableEdit()">Edit Model</button>
                <button type="submit"
                        class="btn save-btn">Save Changes</button>
            </div>

            <a href="${pageContext.request.contextPath}/models/list" class="back-link">
                ← Back to Model List
            </a>
        </form>
    </div>
</div>

<script>
    function enableEdit() {

        /* Hide only editable values (NOT identifier) */
        document.querySelectorAll('.value:not(.fixed-value)')
            .forEach(v => v.style.display = 'none');

        /* Show editable inputs */
        document.querySelectorAll('.editable')
            .forEach(e => e.style.display = 'inline-block');

        document.querySelector('.edit-btn').style.display = 'none';
        document.querySelector('.save-btn').style.display = 'inline-block';
        document.querySelector('h2').innerText = "Edit Model Details";
    }
</script>

</body>
</html>