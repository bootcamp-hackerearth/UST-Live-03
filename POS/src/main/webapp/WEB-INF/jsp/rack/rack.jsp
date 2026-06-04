<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Rack</title>

    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: 'Inter', system-ui, sans-serif;
            background-color: #f1f5f9;
            background-image: radial-gradient(#cbd5e1 0.5px, transparent 0.5px);
            background-size: 24px 24px;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            color: #334155;
            padding: 30px;
        }

        .container {
            width: 100%;
            max-width: 420px;
            background: #ffffff;
            padding: 40px;
            border-radius: 16px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1),
                        0 10px 10px -5px rgba(0,0,0,0.04);
        }

        h2 {
            text-align: center;
            margin-bottom: 30px;
            font-weight: 700;
            font-size: 24px;
            color: #0f172a;
        }

        .input-group { margin-bottom: 20px; }

        label {
            font-size: 14px;
            font-weight: 600;
            margin-bottom: 8px;
            display: block;
            color: #64748b;
        }

        input, select {
            width: 100%;
            padding: 12px 16px;
            border-radius: 10px;
            border: 1px solid #cbd5e1;
            background: #ffffff;
            font-size: 15px;
        }

        input[readonly] {
            background-color: #f8fafc;
            color: #64748b;
            cursor: not-allowed;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #6366f1;
            box-shadow: 0 0 0 4px rgba(99,102,241,0.1);
        }

        .error-text {
            color: #be123c;
            font-size: 12px;
            margin-top: 6px;
        }

        button {
            width: 100%;
            padding: 14px;
            margin-top: 10px;
            border-radius: 10px;
            border: none;
            background: #1e293b;
            color: white;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
        }

        button:hover { background: #0f172a; }

        .link-btn {
            display: block;
            text-align: center;
            margin-top: 25px;
            color: #64748b;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
        }

        .link-btn:hover { color: #4f46e5; }

        .bottom-error {
            margin-top: 20px;
            padding: 12px;
            text-align: center;
            border-radius: 8px;
            background: #fff1f2;
            border: 1px solid #fecdd3;
            color: #be123c;
            font-size: 14px;
            font-weight: 500;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Edit Rack</h2>

    <form:form action="${pageContext.request.contextPath}/rack/update"
               method="post"
               modelAttribute="rack">

        <form:hidden path="id"/>

        <div class="input-group">
            <label>Rack Name</label>
            <form:input path="identifier" readonly="true"/>
        </div>

        <div class="input-group">
            <label>Shelf <span style="color:#be123c">*</span></label>
            <form:select path="shelfs" required="true">
                <form:option value="" label="-- Select Shelf --"/>
                <form:options items="${shelfs}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>
            <form:errors path="shelfs" cssClass="error-text"/>
        </div>

        <div class="input-group">
            <label>Status</label>
            <form:select path="status" required="true">
                <form:option value="true">Active</form:option>
                <form:option value="false">Inactive</form:option>
            </form:select>
        </div>
          <div class="input-group">
                            <label>Description</label>
                            <form:textarea path="description"
                                           placeholder="Update shelf description"
                                            required="true"/>
                        </div>

        <button type="submit">Save Changes</button>

        <a href="${pageContext.request.contextPath}/rack/list"
           class="link-btn">
            ← Back to Racks
        </a>

    </form:form>

    <c:if test="${not empty message}">
        <div class="bottom-error">${message}</div>
    </c:if>

</div>

</body>
</html>