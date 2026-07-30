<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Departments — OHMS</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="wrapper d-flex">
    <%@ include file="../common/admin-sidebar.jspf" %>

    <div class="main-content flex-grow-1">
        <nav class="navbar navbar-light bg-white border-bottom px-4 shadow-sm">
            <button class="btn btn-sm btn-outline-secondary me-2" id="sidebarToggle">
                <i class="bi bi-list"></i>
            </button>
            <span class="navbar-brand mb-0 h5">
                <i class="bi bi-building me-2 text-primary"></i>Manage Departments
            </span>
            <div class="ms-auto">
                <button type="button" class="btn btn-primary btn-sm"
                        data-bs-toggle="modal" data-bs-target="#addDeptModal">
                    <i class="bi bi-plus-circle me-1"></i>Add Department
                </button>
            </div>
        </nav>

        <div class="p-4">

            <%-- Flash messages --%>
            <c:if test="${not empty sessionScope.flashSuccess}">
                <div class="alert alert-success alert-dismissible fade show">
                    <i class="bi bi-check-circle me-2"></i>${sessionScope.flashSuccess}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <c:remove var="flashSuccess" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.flashError}">
                <div class="alert alert-danger alert-dismissible fade show">
                    <i class="bi bi-exclamation-triangle me-2"></i>${sessionScope.flashError}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <c:remove var="flashError" scope="session"/>
            </c:if>

            <%-- Departments Table --%>
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white fw-semibold">
                    <i class="bi bi-building me-2 text-primary"></i>
                    All Departments (${departments.size()})
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Department Name</th>
                                    <th>Description</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="dept" items="${departments}" varStatus="loop">
                                    <tr>
                                        <td>${loop.count}</td>
                                        <td>
                                            <div class="d-flex align-items-center gap-2">
                                                <div class="rounded-3 bg-primary-subtle text-primary d-flex align-items-center justify-content-center p-2">
                                                    <i class="bi bi-building"></i>
                                                </div>
                                                <span class="fw-semibold">${dept.name}</span>
                                            </div>
                                        </td>
                                        <td><small class="text-muted">${dept.description}</small></td>
                                        <td>
                                            <span class="badge ${dept.active ? 'bg-success' : 'bg-secondary'}">
                                                ${dept.active ? 'Active' : 'Inactive'}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="d-flex gap-1">
                                                <%-- Edit --%>
                                                <button type="button"
                                                        class="btn btn-sm btn-outline-primary"
                                                        data-bs-toggle="modal"
                                                        data-bs-target="#editDeptModal"
                                                        data-id="${dept.id}"
                                                        data-name="${dept.name}"
                                                        data-desc="${dept.description}">
                                                    <i class="bi bi-pencil"></i>
                                                </button>
                                                <%-- Toggle status --%>
                                                <form method="post"
                                                      action="${pageContext.request.contextPath}/admin/departments"
                                                      style="display:inline;">
                                                    <input type="hidden" name="action" value="toggle">
                                                    <input type="hidden" name="deptId" value="${dept.id}">
                                                    <button type="submit"
                                                            class="btn btn-sm ${dept.active ? 'btn-outline-warning' : 'btn-outline-success'}"
                                                            title="${dept.active ? 'Deactivate' : 'Activate'}">
                                                        <i class="bi ${dept.active ? 'bi-toggle-on' : 'bi-toggle-off'}"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty departments}">
                                    <tr>
                                        <td colspan="5" class="text-center text-muted py-5">
                                            No departments found.
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>

<%-- Add Department Modal --%>
<div class="modal fade" id="addDeptModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <form method="post" action="${pageContext.request.contextPath}/admin/departments">
            <input type="hidden" name="action" value="add">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="bi bi-plus-circle me-2 text-primary"></i>Add Department</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Department Name</label>
                        <input type="text" name="name" class="form-control"
                               placeholder="e.g. Oncology" required maxlength="100">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Description</label>
                        <textarea name="description" class="form-control" rows="2"
                                  placeholder="Brief description..."></textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            Specializations
                            <small class="text-muted fw-normal">(one per line)</small>
                        </label>
                        <textarea name="specializations" class="form-control" rows="5"
                                  placeholder="Surgical Oncologist&#10;Medical Oncologist&#10;Radiation Oncologist&#10;Pediatric Oncologist"></textarea>
                        <div class="form-text">These will appear in the dropdown when doctors register.</div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-plus-circle me-1"></i>Add Department
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<%-- Edit Department Modal --%>
<div class="modal fade" id="editDeptModal" tabindex="-1">
    <div class="modal-dialog">
        <form method="post" action="${pageContext.request.contextPath}/admin/departments">
            <input type="hidden" name="action" value="edit">
            <input type="hidden" name="deptId" id="editDeptId">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="bi bi-pencil me-2 text-primary"></i>Edit Department</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Department Name</label>
                        <input type="text" name="name" id="editDeptName"
                               class="form-control" required maxlength="100">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Description</label>
                        <textarea name="description" id="editDeptDesc"
                                  class="form-control" rows="3"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-save me-1"></i>Save Changes
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
<script>
    // Populate edit modal with selected department data
    document.getElementById('editDeptModal').addEventListener('show.bs.modal', function(e) {
        const btn = e.relatedTarget;
        document.getElementById('editDeptId').value   = btn.getAttribute('data-id');
        document.getElementById('editDeptName').value = btn.getAttribute('data-name');
        document.getElementById('editDeptDesc').value = btn.getAttribute('data-desc');
    });
</script>
</body>
</html>
