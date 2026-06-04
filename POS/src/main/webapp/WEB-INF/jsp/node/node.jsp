<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Retail Management | Node Details</title>

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

        .value {
            font-size: 14px;
            color: #1F2937;
            width: 60%;
            text-align: right;
        }

        .edit-field {
            display: none;
            width: 60%;
            padding: 8px 12px;
            border-radius: 8px;
            border: 1px solid #E5E7EB;
            font-size: 14px;
            box-sizing: border-box;
            font-family: inherit;
        }

        input:focus, select:focus {
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
    </style>
</head>

<body>
    <div class="profile-card">
        <div class="brand-header">
            <h1>POS Management</h1>
        </div>

        <div class="profile-body">
            <form:form action="${pageContext.request.contextPath}/node/update" method="post" modelAttribute="node">
                <input type="hidden" name="id" value="${node.id}" />

                <h2>Node Configuration</h2>

                <div class="profile-row">
                    <span class="label">Node Name</span>
                    <span class="value">${node.identifier}</span>
                    <input type="hidden" name="identifier" value="${node.identifier}" />
                </div>

                <div class="profile-row">
                    <span class="label">Path Mapping</span>
                    <span class="value display-mode">${node.path}</span>
                    <input type="text" name="path" class="edit-field" value="${node.path}" required />
                </div>

                <div class="profile-row">
                    <span class="label">Assigned Roles</span>
                    <span class="value display-mode">
                        <c:choose>
                            <c:when test="${not empty node.roles}">
                                <c:forEach var="r" items="${node.roles}" varStatus="s">
                                    ${r}<c:if test="${!s.last}">, </c:if>
                                </c:forEach>
                            </c:when>
                            <c:otherwise><i style="color: #9CA3AF;">No roles assigned</i></c:otherwise>
                        </c:choose>
                    </span>

                    <select name="roles" class="edit-field" multiple style="height: 100px;">
                        <c:forEach var="role" items="${roles}">
                            <option value="${role.identifier}"
                                <c:if test="${node.roles.contains(role.identifier)}">selected</c:if>>
                                ${role.identifier}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="actions">
                    <button type="button" class="btn edit-btn" onclick="enableEdit()">Edit Node</button>
                    <button type="submit" class="btn save-btn">Save Changes</button>

                    <a href="${pageContext.request.contextPath}/node/list" class="back-link">
                        ← Back to Nodes List
                    </a>
                </div>
            </form:form>
        </div>
    </div>

    <script>
        function enableEdit() {
            document.querySelectorAll('.display-mode').forEach(el => el.style.display = 'none');
            document.querySelectorAll('.edit-field').forEach(el => el.style.display = 'inline-block');
            document.querySelector('.edit-btn').style.display = 'none';
            document.querySelector('.save-btn').style.display = 'inline-block';
            document.querySelector('h2').innerText = "Edit Node Details";
        }
    </script>
</body>
</html>