<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Edit Node</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif;
}

body {
    min-height: 100vh;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
    display: flex;
    justify-content: center;
    align-items: center;
}

.card {
    width: 420px;
    padding: 30px;
    border-radius: 15px;
    background: rgba(255,255,255,0.05);
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255,255,255,0.2);
    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
}

.card-header {
    text-align: center;
    margin-bottom: 20px;
}

.card-header h5 {
    color: #fff;
    font-size: 18px;
}

.form-label {
    font-size: 13px;
    color: #ccc;
    margin-bottom: 4px;
    display: block;
}

.form-control {
    width: 100%;
    padding: 10px;
    margin-bottom: 14px;
    border-radius: 8px;
    border: none;
    outline: none;
    background: rgba(255,255,255,0.1);
    color: #fff;
}

.form-control:focus {
    border: 1px solid #00ffff;
    box-shadow: 0 0 8px #00ffff;
}

.form-control[readonly] {
    background: rgba(255,255,255,0.05);
    color: #aaa;
}

select[multiple] {
    height: 130px;
}

.form-text {
    font-size: 11px;
    color: #aaa;
    margin-bottom: 10px;
}

.btn {
    padding: 10px 14px;
    border-radius: 8px;
    text-decoration: none;
    font-size: 13px;
    cursor: pointer;
    border: none;
}

.btn-primary {
    background: #00ffff;
    color: #000;
}

.btn-primary:hover {
    box-shadow: 0 0 15px #00ffff;
}

.btn-secondary {
    background: #666;
    color: #fff;
}

.btn-secondary:hover {
    background: #555;
}

.btn-group {
    display: flex;
    justify-content: space-between;
    margin-top: 10px;
}

.alert {
    text-align: center;
    padding: 10px;
    border-radius: 6px;
    margin-bottom: 12px;
    color: #ff8080;
    font-size: 13px;
}

.card-footer {
    text-align: center;
    margin-top: 12px;
    font-size: 12px;
    color: #aaa;
}
</style>
</head>

<body>

<div class="card">

    <div class="card-header">
        <h5>Edit Node</h5>
    </div>

    <c:if test="${empty node}">
        <div class="alert">
            Node not found
        </div>
    </c:if>

    <c:if test="${not empty node}">

        <form:form method="post"
                   action="/node/update"
                   modelAttribute="node">

            <label class="form-label">Node Identifier</label>
            <form:input
                path="identifier"
                cssClass="form-control"
                readonly="true"
            />

            <label class="form-label">Node Path</label>
            <form:input
                path="path"
                cssClass="form-control"
                required="true"
                minlength="3"
                maxlength="100"
                pattern="^/.*"
                title="Path must start with /"
            />

            <label class="form-label">Roles</label>
            <form:select
                path="roles"
                multiple="true"
                cssClass="form-control"
                required="true"
            >
                <form:options
                    items="${roles}"
                    itemValue="identifier"
                    itemLabel="identifier"
                />
            </form:select>

            <div class="form-text">
                Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple roles
            </div>

            <div class="btn-group">
                <a href="${pageContext.request.contextPath}/node/list"
                   class="btn btn-secondary">
                    Cancel
                </a>

                <button type="submit" class="btn btn-primary">
                    Update Node
                </button>
            </div>

        </form:form>

    </c:if>

    <div class="card-footer">
        POS Management System
    </div>

</div>

</body>
</html>
