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

        var authorization = request.getHeader("Authorization");
        
        if (authorization == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, new JsonBuilder()
                .addProperty("errorMessage", "No authorization token provided.")
                .toString());
            return;
        }

        String token = authorization.split(" ")[1];
        byte[] authDecoded = java.util.Base64.getDecoder().decode(token);

        String auth = new String(authDecoded);

        String userName = auth.split(":")[0];
        String passWord = auth.split(":")[1];

        UserModel userModel = this.userRepository.findByUserName(userName);

        if (userModel == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, new JsonBuilder()
                .addProperty("errorMessage", "Credentials provided on authorization token are invalid.")
                .toString());
            return;
        }

        if (!userModel.getPassWord().equals(passWord)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, new JsonBuilder()
                .addProperty("errorMessage", "Credentials provided on authorization token are invalid.")
                .toString());
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}
