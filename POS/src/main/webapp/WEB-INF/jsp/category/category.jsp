<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS | Edit Category</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <style>
        body { background-color: #f4f7f6; font-family: 'Segoe UI', sans-serif; }
        .container { margin-top: 50px; }

        /* Matching the "Warehouse Style" card */
        .card-edit {
            background: #ffffff;
            border-radius: 14px;
            box-shadow: 0 12px 30px rgba(0,0,0,0.08);
            border: none;
            max-width: 550px;
            margin: auto;
        }

        .card-header {
            background-color: #0d6efd;
            color: white;
            border-radius: 14px 14px 0 0 !important;
            padding: 20px;
        }

        .identifier-preview {
            background: #eef3ff;
            color: #0d6efd;
            padding: 8px 12px;
            border-radius: 8px;
            font-weight: 600;
            display: inline-block;
            margin-bottom: 20px;
        }

        .btn-update { background-color: #0d6efd; color: white; border-radius: 8px; padding: 10px 25px; }
        .btn-update:hover { background-color: #0b5ed7; color: white; }
    </style>
</head>
<body>

<div class="container">
    <div class="card card-edit">
        <div class="card-header text-center">
            <h4 class="mb-0"><i class="bi bi-pencil-square me-2"></i>Edit Category</h4>
        </div>

        <div class="card-body p-4">
            <!-- Message Alert -->
            <c:if test="${not empty message}">
                <div class="alert ${success ? 'alert-success' : 'alert-danger'} text-center">
                    ${message}
                </div>
            </c:if>

            <form:form action="${pageContext.request.contextPath}/category/update"
                       method="post" modelAttribute="category">

                <!-- CRITICAL: Ensure the identifier is bound to the form -->
                <form:hidden path="identifier"/>

                <div class="text-center">
                    <span class="identifier-preview">
                        <i class="bi bi-tag-fill me-2"></i>${category.identifier}
                    </span>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Super Category</label>
                    <form:select path="superCategory" cssClass="form-select">
                        <form:option value=" ">-- None (Top Level) --</form:option>
                        <c:forEach items="${categories}" var="cat">
                            <c:if test="${cat.identifier != category.identifier}">
                                <form:option value="${cat.identifier}">${cat.identifier}</form:option>
                            </c:if>
                        </c:forEach>
                    </form:select>
                    <div class="form-text">Choose a parent category if applicable.</div>
                </div>

                <div class="mb-4">
                    <label class="form-label fw-semibold">Availability Status</label>
                    <form:select path="status" cssClass="form-select">
                        <form:option value="true">Active</form:option>
                        <form:option value="false">Inactive</form:option>
                    </form:select>
                </div>

                <div class="d-flex justify-content-between align-items-center">
                    <a href="${pageContext.request.contextPath}/category/list" class="text-decoration-none text-muted">
                        <i class="bi bi-arrow-left"></i> Back to List
                    </a>
                    <button type="submit" class="btn btn-update fw-bold">
                        Save Changes
                    </button>
                </div>
            </form:form>
        </div>
    </div>
</div>

</body>
</html>