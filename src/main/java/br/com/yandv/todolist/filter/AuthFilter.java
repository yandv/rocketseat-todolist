package br.com.yandv.todolist.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.yandv.todolist.model.UserModel;
import br.com.yandv.todolist.repository.IUserRepository;
import br.com.yandv.todolist.utils.JsonBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().equals("/api/v1/users/") && request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");
        
        if (authorization == null || !authorization.startsWith("Basic ") || authorization.split(" ").length < 1) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, new JsonBuilder()
                .addProperty("errorMessage", "Credentials provided on authorization token are invalid.")
                .toString());
            return;
        }

        String token = authorization.split(" ")[1];
        byte[] authDecoded = java.util.Base64.getDecoder().decode(token);

        String auth = new String(authDecoded);

        String userName = auth.split(":")[0];
        String passWord = auth.split(":")[1];

        UserModel userModel = this.userRepository.findByUserName(userName);

        if (userModel == null || !userModel.getPassWord().equals(passWord)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, new JsonBuilder()
                .addProperty("errorMessage", "Credentials provided on authorization token are invalid.")
                .toString());
            return;
        }
        
        request.setAttribute("userId", userModel.getUniqueId());
        filterChain.doFilter(request, response);
    }
}
