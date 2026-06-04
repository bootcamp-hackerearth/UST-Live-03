<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Rack | POS System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        body { background-color: #f4f7f6; min-height: 100vh; display: flex; align-items: center; }
        .card-edit { border-radius: 15px; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }
        .card-header { background-color: #0d6efd; color: white; border-top-left-radius: 15px !important; border-top-right-radius: 15px !important; }
        .form-label { font-weight: 600; color: #495057; }
        .readonly-field { background-color: #e9ecef; cursor: not-allowed; }
    </style>
</head>
<body>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="card card-edit">
                <div class="card-header text-center py-3">
                    <h4 class="mb-0"><i class="bi bi-pencil-square me-2"></i>Edit Rack Details</h4>
                </div>

                <div class="card-body p-4">
                    <c:if test="${not empty message}">
                        <div class="alert ${racks.success ? 'alert-success' : 'alert-danger'} alert-dismissible fade show" role="alert">
                            ${message}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <form:form action="${pageContext.request.contextPath}/rack/update" method="post" modelAttribute="racks">

                        <form:hidden path="id" />

                        <div class="mb-3">
                            <label class="form-label">Rack Identifier</label>
                            <form:input path="identifier" cssClass="form-control readonly-field" readonly="true" />
                            <div class="form-text">The unique identifier cannot be modified.</div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Assigned Shelf</label>
                            <form:select path="shelfs" cssClass="form-select" required="true">
                                <option value="" disabled>-- Select a Shelf --</option>
                                <c:forEach var="s" items="${shelves}">
                                    <option value="${s.identifier}" ${s.identifier == racks.shelfs ? 'selected' : ''}>
                                        ${s.identifier}
                                    </option>
                                </c:forEach>
                            </form:select>
                            <div class="form-text">Choose which shelfs this rack is located on.</div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label d-block">Operating Status</label>
                            <div class="form-check form-switch">
                                <form:checkbox path="status" cssClass="form-check-input" id="statusSwitch" role="switch"/>
                                <label class="form-check-label" for="statusSwitch">
                                    <span id="statusLabel">${racks.status ? 'Active' : 'Inactive'}</span>
                                </label>
                            </div>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary py-2 fw-bold">
                                <i class="bi bi-save me-2"></i>Update Rack
                            </button>
                            <a href="${pageContext.request.contextPath}/rack/list" class="btn btn-outline-secondary py-2">
                                <i class="bi bi-arrow-left"></i> Back to List
                            </a>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    // Visual toggle for the status label
    document.getElementById('statusSwitch').addEventListener('change', function() {
        document.getElementById('statusLabel').innerText = this.checked ? 'Active' : 'Inactive';
    });
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>