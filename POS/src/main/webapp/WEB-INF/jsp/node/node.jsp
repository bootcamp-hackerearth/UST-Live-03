<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #1f4037, #99f2c8);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            width: 420px;
            border-radius: 15px;
        }

        h4 {
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="card shadow-lg">
    <div class="card-body">

        <h4 class="text-center mb-4 text-primary">Edit Node</h4>

        <!-- MESSAGE -->
        <c:if test="${not empty message}">
            <div class="alert alert-info text-center">
                ${message}
            </div>
        </c:if>

        <!-- NODE NOT FOUND -->
        <c:if test="${empty node}">
            <div class="alert alert-danger text-center">
                Node not found
            </div>
        </c:if>

        <!-- FORM -->
        <c:if test="${not empty node}">
            <form:form action="${pageContext.request.contextPath}/node/update"
                       method="post"
                       modelAttribute="node">

                <form:hidden path="id"/>

                <!-- NODE NAME (READ ONLY) -->
                <div class="mb-3">
                    <label class="form-label">Node Name</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                readonly="true"/>
                </div>

                <!-- NODE PATH -->
                <div class="mb-3">
                    <label class="form-label">Node Path</label>
                    <form:input path="path"
                                cssClass="form-control"
                                required="true"/>
                </div>

                <!-- CURRENT ROLES -->
                <div class="mb-3">
                    <label class="form-label">Current Roles</label>
                    <div class="mb-2">
                        <c:forEach var="r" items="${node.roles}">
                            <span class="badge bg-secondary me-1">${r}</span>
                        </c:forEach>
                    </div>
                </div>

                <!-- UPDATE ROLES -->
                <div class="mb-4">
                    <label class="form-label">Update Roles</label>
                    <form:select path="roles"
                                 multiple="true"
                                 cssClass="form-control"
                                 required="true">
                        <form:options items="${roles}"
                                      itemValue="identifier"
                                      itemLabel="identifier"/>
                    </form:select>
                </div>

                <!-- ACTIONS -->
                <div class="d-flex justify-content-between">
                    <a href="${pageContext.request.contextPath}/node/list"
                       class="btn btn-outline-secondary">
                        Cancel
                    </a>

                    <button type="submit" class="btn btn-primary">
                        Update
                    </button>
                </div>

            <c:if test="${not empty message}">
                    <p class="error">${message}</p>
                </c:if>

            </form:form>
        </c:if>

    </div>
</div>

</body>
</html>