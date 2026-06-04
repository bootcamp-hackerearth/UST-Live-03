<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Entry</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

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
                    <h4 class="mb-0"><i class="bi bi-pencil-square me-2"></i>Edit Details</h4>
                </div>

                <div class="card-body p-4">
                    <c:if test="${not empty message}">
                        <div class="alert ${success ? 'alert-success' : 'alert-danger'} alert-dismissible fade show" role="alert">
                            ${message}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <form:form action="/brand/update" method="post" modelAttribute="brands">

                        <form:hidden path="id" />

                        <div class="mb-3">
                            <label class="form-label">Identifier</label>
                            <form:input path="identifier" cssClass="form-control readonly-field" readonly="true" />
                            <div class="form-text text-muted small">The identifier code cannot be changed.</div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Description</label>
                            <form:textarea path="description" cssClass="form-control" rows="3" required="true" />
                        </div>

                        <div class="mb-4">
                             <label class="form-label fw-bold">Status</label>
                                 <form:select path="status" class="form-select">
                                     <form:option value="true">Active</form:option>
                                     <form:option value="false">Deactive</form:option>
                                 </form:select>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-success py-2 fw-bold">
                                <i class="bi bi-check2-circle me-1"></i> Update Changes
                            </button>
                            <a href="/brand/list" class="btn btn-outline-secondary">
                                Cancel
                            </a>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.getElementById('statusSwitch').addEventListener('change', function() {
        document.getElementById('statusLabel').innerText = this.checked ? 'Active' : 'Inactive';
    });
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>