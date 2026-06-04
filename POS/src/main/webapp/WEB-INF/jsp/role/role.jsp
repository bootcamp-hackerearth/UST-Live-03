<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
<title>Edit Role</title>

<style>
body {
    background: #F6F7F9;
    font-family: Arial;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

.card {
    width: 380px;
    background: #FFFFFF;
    padding: 25px;
    border-radius: 12px;
    border: 1px solid #E5E7EB;
}

h2 {
    text-align: center;
    color: #111827;
}

input {
    width: 100%;
    padding: 10px;
    border-radius: 8px;
    border: 1px solid #E5E7EB;
}

button {
    padding: 10px 16px;
    border-radius: 8px;
    border: none;
    font-weight: 600;
    cursor: pointer;
}

.btn-primary {
    background: #2B2B2B;
    color: white;
}

.btn-primary:hover {
    background: #111111;
}

.btn-cancel {
    background: #E5E7EB;
    color: #111827;
}

.btn-cancel:hover {
    background: #D1D5DB;
}
</style>
</head>

<body>

<div class="card">

<h2>Edit Role</h2>

<form:form method="post" action="/role/update" modelAttribute="role">

<form:hidden path="id"/>

<form:input path="identifier" readOnly="true"/>
<form:input path="description"/>


<div style="display:flex;justify-content:space-between;margin-top:15px;">
    <a href="/role/list">
        <button type="button" class="btn-cancel">Cancel</button>
    </a>

    <button type="submit" class="btn-primary">Update</button>
</div>

</form:form>

</div>

</body>
</html>
