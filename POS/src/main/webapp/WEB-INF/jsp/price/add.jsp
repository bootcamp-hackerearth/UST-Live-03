<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Add Price</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif;
}

body {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
}

.container {
    width: 350px;
    padding: 30px;
    border-radius: 15px;
    background: rgba(255, 255, 255, 0.05);
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
}

h2 {
    text-align: center;
    color: #fff;
    margin-bottom: 20px;
}

label {
    color: #ccc;
    font-size: 14px;
}

input,
select {
    width: 100%;
    padding: 10px;
    margin-top: 6px;
    margin-bottom: 15px;
    border-radius: 8px;
    border: none;
    outline: none;
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
}

input::placeholder {
    color: #aaa;
}

input:focus,
select:focus {
    border: 1px solid #00ffff;
    box-shadow: 0 0 8px #00ffff;
}

select option {
    background: #1a1b26;
    color: #fff;
}

button {
    width: 100%;
    padding: 12px;
    border: none;
    border-radius: 8px;
    background: #00ffff;
    color: #000;
    font-weight: bold;
    cursor: pointer;
    transition: 0.3s;
}

button:hover {
    box-shadow: 0 0 15px #00ffff,
                0 0 30px #00ffff,
                0 0 60px #00ffff;
    transform: scale(1.03);
}

.message {
    text-align: center;
    margin-top: 10px;
    color: #ffb3b3;
    font-size: 13px;
}
</style>
</head>

<body>

<div class="container">

    <h2>Add Price</h2>

    <form:form action="/price/add"
               method="post"
               modelAttribute="priceDto">

        <label>Product Identifier</label>
        <form:select
            path="identifier"
            required="true"
        >
            <form:option value="">Select Product</form:option>

            <c:forEach var="p" items="${product}">
                <form:option value="${p.identifier}">
                    ${p.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <label>Cost Price</label>
        <form:input
            path="costPrice"
            type="number"
            step="0.01"
            placeholder="Enter cost price"
            required="true"
            min="0"
        />

        <label>Selling Price</label>
        <form:input
            path="sellingPrice"
            type="number"
            step="0.01"
            placeholder="Enter selling price"
            required="true"
            min="0"
        />

        <label>Mrp Price</label>
        <form:input
            path="mrpPrice"
            type="number"
            step="0.01"
            placeholder="Enter mrp price"
            required="true"
            min="0"
        />

        <button type="submit">
            Save
        </button>

        <c:if test="${not empty message}">
            <div class="message">
                ${message}
            </div>
        </c:if>

    </form:form>

</div>

</body>
</html>
