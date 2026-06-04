<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

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
            background-color: #000;
            color: #fff;
            text-align: center;
            font-weight: 600;
            padding: 14px;
            border-radius: 12px 12px 0 0;
        }

        .form-control {
            border-radius: 6px;
        }

        .dropdown-box {
            border: 1px solid #ccc;
            padding: 8px 12px;
            border-radius: 6px;
            cursor: pointer;
            background: #fff;
        }

        .dropdown-list {
            display: none;
            position: absolute;
            width: 100%;
            max-height: 180px;
            overflow-y: auto;
            background: #fff;
            border: 1px solid #ddd;
            border-radius: 6px;
            margin-top: 5px;
            z-index: 1000;
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
    </style>
</head>

<body>

<div class="card">

    <div class="card-header">
        Update User
    </div>

    <div class="card-body">

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/user/update" method="post">

            <input type="hidden" name="oldUsername" value="${user.username}"/>
            <input type="hidden" name="id" value="${user.id}"/>

            <div class="mb-3">
                <label>Name</label>
                <input type="text" name="name" class="form-control"
                       value="${user.name}" required/>
            </div>

            <div class="mb-3">
                <label>Email</label>
                <input type="email" name="username" class="form-control"
                       value="${user.username}" required/>
            </div>

            <div class="mb-3">
                <label>Phone Number</label>
                <input type="text" name="phoneNo" class="form-control"
                       value="${user.phoneNo}" required/>
            </div>

            <div class="mb-3 position-relative role-dropdown" id="roleDropdown">

                <label>Select Roles</label>

                <div class="dropdown-box" id="selectedRolesText" onclick="toggleDropdown()">
                    <c:choose>
                        <c:when test="${not empty user.roles}">
                            <c:forEach var="r" items="${user.roles}" varStatus="s">
                                ${r}<c:if test="${!s.last}">, </c:if>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            Select Roles
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="dropdown-list">
                    <c:forEach var="role" items="${roles}">
                        <label>
                            <input type="checkbox" name="roles"
                                   value="${role.identifier}"
                                   onchange="updateSelectedRoles()"
                                   <c:if test="${user.roles.contains(role.identifier)}">checked</c:if>>
                            ${role.identifier}
                        </label>
                    </c:forEach>
                </div>
            </div>

            <div class="d-grid gap-2 mt-4">
                <button type="submit" class="btn btn-success">
                    Update User
                </button>

                <a href="${pageContext.request.contextPath}/user/list"
                   class="btn btn-secondary">
                    Back
                </a>
            </div>

        </form>

    </div>
</div>

<script>
    function toggleDropdown() {
        document.getElementById("roleDropdown").classList.toggle("active");
    }

    function updateSelectedRoles() {
        const checkedRoles = document.querySelectorAll(
            ".dropdown-list input[type='checkbox']:checked"
        );

        const values = Array.from(checkedRoles).map(cb => cb.value);
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