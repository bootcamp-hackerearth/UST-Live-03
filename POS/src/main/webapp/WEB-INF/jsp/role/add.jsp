<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
<title>Add Role</title>

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
    margin-top: 10px;
}

input:focus {
    border-color: #2B2B2B;
    outline: none;
}

button {
    width: 100%;
    margin-top: 15px;
    padding: 10px;
    background: #2B2B2B;
    color: white;
    border: none;
    border-radius: 8px;
}

button:hover {
    background: #111111;
}
</style>
</head>

<body>

<div class="card">

<h2>Add Role</h2>

<form:form method="post" action="/role/add" modelAttribute="roleDto">

<form:input path="identifier" placeholder="Enter role"/>

<button type="submit">Add Role</button>

</form:form>

</div>

</body>
</html>
