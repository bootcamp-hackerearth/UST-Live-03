<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
        }

        .card {
            border-radius: 12px;
            border: 1px solid #ddd;
            max-width: 480px;
            margin: 60px auto;
            box-shadow: 0 6px 18px rgba(0,0,0,.08);
        }

        .card-header {
            background: #000;
            color: #fff;
            text-align: center;
            font-weight: 600;
            padding: 14px;
            border-radius: 12px 12px 0 0;
        }

        .form-control {
            border-radius: 6px;
        }

        .form-label {
            font-weight: 500;
        }

        .dropdown-box {
            border: 1px solid #ccc;
            padding: 8px 12px;
            border-radius: 6px;
            background: #fff;
            cursor: pointer;
        }

        .dropdown-list {
            display: none;
            position: absolute;
            width: 100%;
            max-height: 180px;
            overflow-y: auto;
            background: white;
            border: 1px solid #ddd;
            border-radius: 6px;
            margin-top: 5px;
            z-index: 999;
        }

        .role-dropdown.active .dropdown-list {
            display: block;
        }

        .dropdown-list label {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 6px 10px;
        }

        .dropdown-list label:hover {
            background: #f1f1f1;
        }

        .btn {
            border-radius: 6px;
        }
    </style>
</head>

<body>

<div class="card">

    <div class="card-header">
        Edit Node
    </div>

    <div class="card-body">

        <c:if test="${not empty message}">
            <div class="alert alert-info text-center">
                ${message}
            </div>
        </c:if>

        <c:if test="${empty node}">
            <div class="alert alert-danger text-center">
                Node not found
            </div>
        </c:if>

        <c:if test="${not empty node}">

            <form:form action="${pageContext.request.contextPath}/node/update"
                       method="post"
                       modelAttribute="node">

                <form:hidden path="id"/>

                <div class="mb-3">
                    <label class="form-label">Node Identifier</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                readonly="true"/>
                </div>

                <div class="mb-3">
                    <label class="form-label">Node Path</label>
                    <form:input path="path"
                                cssClass="form-control"
                                required="true"/>
                </div>

                <div class="mb-4 role-dropdown position-relative" id="roleDropdown">

                    <label class="form-label">Assign Roles</label>

                    <div class="dropdown-box" id="selectedRolesText" onclick="toggleDropdown()">
                        <c:choose>
                            <c:when test="${not empty node.roles}">
                                <c:forEach var="r" items="${node.roles}" varStatus="s">
                                    ${r}<c:if test="${!s.last}">, </c:if>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                Select Roles
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="dropdown-list">
                        <c:forEach items="${roles}" var="role">
                            <label>
                                <input type="checkbox"
                                       name="roles"
                                       value="${role.identifier}"
                                       onchange="updateSelectedRoles()"
                                       <c:if test="${node.roles.contains(role.identifier)}">checked</c:if>>
                                ${role.identifier}
                            </label>
                        </c:forEach>
                    </div>

                </div>

                <div class="d-grid gap-2 mt-4">
                    <button type="submit" class="btn btn-success">
                        Update
                    </button>

                    <a href="${pageContext.request.contextPath}/node/list"
                       class="btn btn-secondary">
                        Cancel
                    </a>
                </div>

            </form:form>

        </c:if>

    </div>

    <div class="card-footer text-center text-muted small">
        Node Management System
    </div>

</div>

<script>
function toggleDropdown() {
    document.getElementById("roleDropdown").classList.toggle("active");
}

function updateSelectedRoles() {
    const checked = document.querySelectorAll("#roleDropdown .dropdown-list input[type='checkbox']:checked");
    const values = Array.from(checked).map(cb => cb.value);

    document.getElementById("selectedRolesText").innerText =
        values.length > 0 ? values.join(", ") : "Select Roles";
}

document.addEventListener("click", function (e) {
    const dropdown = document.getElementById("roleDropdown");
    if (!dropdown.contains(e.target)) {
        dropdown.classList.remove("active");
    }
});
</script>

</body>
</html>
