package br.com.lucas.candidaturaestagio.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri;
        if (contextPath != null && !contextPath.isBlank() && uri.startsWith(contextPath)) {
            path = uri.substring(contextPath.length());
        }
        if (path.isBlank()) {
            path = "/";
        }
        HttpSession session = request.getSession(false);

        // URLs públicas (sem autenticação requerida)
        if (isPublicUrl(path)) {
            return true;
        }

        // Se não há sessão, redirecionar para login
        if (session == null) {
            response.sendRedirect("/login");
            return false;
        }

        // Validar acesso por perfil
        if (uri.startsWith("/candidato/")) {
            if (session.getAttribute("candidatoId") == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        } else if (uri.startsWith("/empresa/")) {
            if (session.getAttribute("empresaId") == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        } else if (uri.startsWith("/admin/")) {
            Boolean isAdmin = (Boolean) session.getAttribute("admin");
            if (isAdmin == null || !isAdmin) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }

        return true;
    }

    private boolean isPublicUrl(String uri) {
        // URLs públicas que não requerem autenticação
        return uri.equals("/") ||
                uri.equals("/login") ||
                uri.equals("/logout") ||
                uri.equals("/candidato/login") ||
                uri.equals("/candidato/register") ||
                uri.equals("/empresa/register") ||
                uri.equals("/vagas") ||
                uri.equals("/profissionais") ||
                uri.equals("/empresas") ||
                uri.startsWith("/css/") ||
                uri.startsWith("/js/") ||
                uri.startsWith("/images/") ||
                uri.startsWith("/static/") ||
                uri.startsWith("/error") ||
                uri.startsWith("/api/");  // APIs podem ter seus próprios mecanismos de autenticação
    }
}
