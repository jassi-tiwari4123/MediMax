<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
    index.jsp — Landing page / entry point.
    Immediately redirects to login if no valid JWT cookie exists.
--%>
<%
    // Check for existing JWT cookie
    Cookie[] cookies = request.getCookies();
    boolean hasToken = false;
    if (cookies != null) {
        for (Cookie c : cookies) {
            if ("ohms_jwt".equals(c.getName()) && c.getValue() != null && !c.getValue().isEmpty()) {
                hasToken = true;
                break;
            }
        }
    }
    if (hasToken) {
        response.sendRedirect(request.getContextPath() + "/login");
    } else {
        response.sendRedirect(request.getContextPath() + "/login");
    }
%>
