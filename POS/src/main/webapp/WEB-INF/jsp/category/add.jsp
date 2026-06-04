<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Super Category</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            background-color: #f4f7f6;
            min-height: 100vh;
            display: flex;
            align-items: center;
        }
        .card-add {
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            border: none;
        }
        .card-header {
            background-color: #0d6efd;
            color: white;
            border-radius: 15px 15px 0 0 !important;
        }
        .form-label {
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-5">

            <div class="card card-add">

                <div class="card-header py-3 d-flex justify-content-between align-items-center">
                    <h4 class="mb-0">
                        <i class="bi bi-plus-square"></i> Create Sub-Category
                    </h4>


                </div>

                <div class="card-body p-4">
                    <form action="${pageContext.request.contextPath}/category/add" method="post">

                        <div class="mb-3">
                            <label class="form-label">New Category Identifier</label>
                            <input type="text"
                                   class="form-control"
                                   name="identifier"
                                   placeholder="e.g. SUB-ELEC-001"
                                   required />
                        </div>

                        <div class="mb-4">
                            <label class="form-label">Parent Super Category</label>

                            <select class="form-select"
                                    name="superCategory"
                                    multiple
                                    size="5">

                                <c:forEach var="cat" items="${category}">
                                    <option value="${cat.identifier}">
                                        ${cat.identifier}
                                    </option>
                                </c:forEach>

                            </select>

                            <div class="form-text">
                                Hold <b>Ctrl</b> (Windows/Linux) or <b>Cmd</b> (Mac) to select multiple super categories.
                            </div>
                        </div>

                        <div class="d-flex justify-content-between">
                            <a href="${pageContext.request.contextPath}/category/list"
                               class="btn btn-light">
                                Cancel
                            </a>

                            <button type="submit" class="btn btn-primary px-4">
                                Save Entry
                            </button>
                        </div>
                    </form>
                </div>

            </div>

        </div>
    </div>
</div>

</body>
</html>