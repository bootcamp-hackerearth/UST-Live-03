<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Management | Category Profile</title>

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
        }

        .profile-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px 0;
            border-bottom: 1px solid #E5E7EB;
        }

        .label {
            font-weight: 700;
            width: 40%;
            font-size: 13px;
            color: #4B5563;
        }

        .value, .static-value {
            width: 60%;
            text-align: right;
            font-size: 14px;
            color: #1F2937;
        }

        select {
            width: 60%;
            padding: 8px 12px;
            border-radius: 8px;
            border: 1px solid #E5E7EB;
            display: none;
        }

        .actions {
            text-align: center;
            margin-top: 30px;
        }

        .btn {
            padding: 10px 24px;
            border-radius: 8px;
            border: none;
            cursor: pointer;
            font-weight: 600;
        }

        .edit-btn {
            background: #0B3C5D;
            color: #fff;
        }

        .save-btn {
            background: #16A34A;
            color: #fff;
            display: none;
        }

        .back-link {
            display: block;
            margin-top: 20px;
            text-align: center;
            font-size: 13px;
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
        <form action="${pageContext.request.contextPath}/category/update" method="post">

            <input type="hidden" name="id" value="${category.id}" />
            <input type="hidden" name="identifier" value="${category.identifier}" />

            <h2>Category Configuration</h2>

            <!-- Category Name -->
            <div class="profile-row">
                <span class="label">Category Name</span>
                <span class="static-value">${category.identifier}</span>
            </div>

            <!-- Super Category -->
            <div class="profile-row">
                <span class="label">Super Category</span>

                <!-- View mode -->
                <span class="value">
                    <c:choose>
                        <c:when test="${empty category.superCategory}">
                            <i style="color:#9CA3AF;">Top Level</i>
                        </c:when>
                        <c:otherwise>
                            ${category.superCategory}
                        </c:otherwise>
                    </c:choose>
                </span>

                <!-- Edit mode -->
                <select name="superCategory">
                    <option value="">-- No Parent (Top Level) --</option>

                    <c:forEach var="c" items="${categories}">
                        <option value="${c.identifier}"
                            <c:if test="${c.identifier eq category.superCategory}">
                                selected
                            </c:if>>
                            ${c.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="actions">
                <button type="button" class="btn edit-btn" onclick="enableEdit()">Edit Category</button>
                <button type="submit" class="btn save-btn">Save Changes</button>

                <a href="${pageContext.request.contextPath}/category/list" class="back-link">
                    ← Back to Categories List
                </a>
            </div>
        </form>
    </div>
</div>

<script>
    function enableEdit() {
        document.querySelectorAll('.value').forEach(v => v.style.display = 'none');
        document.querySelectorAll('select').forEach(s => s.style.display = 'inline-block');
        document.querySelector('.edit-btn').style.display = 'none';
        document.querySelector('.save-btn').style.display = 'inline-block';
        document.querySelector('h2').innerText = "Edit Category Details";
    }
</script>

</body>
</html>