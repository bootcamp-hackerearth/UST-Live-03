<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Retail Management | Role Details</title>

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
            position: relative;
            overflow: hidden;
        }

        .brand-header {
            background: #0B3C5D;
            padding: 25px;
            color: #FFFFFF;
            text-align: center;
        }

        .brand-header h1 {
            margin: 0;
            font-size: 22px;
            font-weight: 600;
            letter-spacing: 0.8px;
        }

        .profile-body {
            padding: 30px 40px;
        }

        h2 {
            color: #1F2937;
            font-size: 18px;
            margin-bottom: 20px;
            text-align: center;
            font-weight: 600;
        }

        .profile-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px 0;
            border-bottom: 1px solid #E5E7EB;
        }

        .label {
            font-size: 13px;
            font-weight: 700;
            color: #4B5563;
            width: 40%;
        }

        .value, .static-value {
            font-size: 14px;
            color: #1F2937;
            width: 60%;
            text-align: right;
        }

        input[type="text"] {
            width: 60%;
            padding: 8px 12px;
            border: 1px solid #E5E7EB;
            border-radius: 8px;
            font-size: 14px;
            display: none; /* Hidden until edit mode */
            box-sizing: border-box;
        }

        input:focus {
            outline: none;
            border-color: #0B3C5D;
            background: #F9FAFB;
        }

        .actions {
            text-align: center;
            margin-top: 30px;
        }

        .btn {
            padding: 10px 24px;
            border-radius: 8px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: opacity 0.2s;
            display: inline-block;
            text-decoration: none;
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

        .back-link:hover {
            color: #0B3C5D;
            text-decoration: underline;
        }

        /* Toast Styles */
        .toast {
            position: fixed;
            top: 24px;
            right: -400px;
            min-width: 280px;
            padding: 16px 20px;
            border-radius: 12px;
            color: #FFFFFF;
            font-size: 14px;
            font-weight: 600;
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
            z-index: 9999;
            transition: all 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55);
        }

        .toast-success { background: #16A34A; }
        .toast.show { right: 24px; }
    </style>
</head>

<body>
    <c:if test="${not empty message}">
        <div id="toast" class="toast toast-success">
            ${message}
        </div>
    </c:if>

    <div class="profile-card">
        <div class="brand-header">
            <h1>POS Retail Management</h1>
        </div>

        <div class="profile-body">
            <form action="${pageContext.request.contextPath}/role/update" method="post">
                <input type="hidden" name="id" value="${role.id}" />

                <h2>Role Configuration</h2>

                <div class="profile-row">
                    <span class="label">Role Name</span>
                    <span class="static-value">${role.identifier}</span>
                    <input type="hidden" name="identifier" value="${role.identifier}" />
                </div>

                <div class="profile-row">
                    <span class="label">Description</span>
                    <span class="value">
                        <c:choose>
                            <c:when test="${not empty role.description}">
                                ${role.description}
                            </c:when>
                            <c:otherwise>
                                <i style="color:#9CA3AF;">No description provided</i>
                            </c:otherwise>
                        </c:choose>
                    </span>
                    <input type="text"
                           name="description"
                           value="${role.description}"
                           placeholder="e.g. System Administrator" />
                </div>

                <div class="actions">
                    <button type="button" class="btn edit-btn" onclick="enableEdit()">Edit Role</button>
                    <button type="submit" class="btn save-btn">Save Changes</button>

                    <a href="${pageContext.request.contextPath}/role/list" class="back-link">
                        ← Back to Roles List
                    </a>
                </div>
            </form>
        </div>
    </div>

    <script>
        function enableEdit() {
            document.querySelectorAll('.value')
                .forEach(v => v.style.display = 'none');
            document.querySelectorAll('input[type="text"]')
                .forEach(i => i.style.display = 'inline-block');
            document.querySelector('.edit-btn').style.display = 'none';
            document.querySelector('.save-btn').style.display = 'inline-block';
            document.querySelector('h2').innerText = "Edit Role Details";
        }
        document.addEventListener("DOMContentLoaded", function () {
            const toast = document.getElementById("toast");
            if (toast) {
                setTimeout(() => toast.classList.add("show"), 200);
                setTimeout(() => toast.classList.remove("show"), 3500);
            }
        });
    </script>
</body>
</html>