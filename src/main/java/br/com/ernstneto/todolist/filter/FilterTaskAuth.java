package br.com.ernstneto.todolist.filter;

import java.io.IOException;
import java.util.Base64;

//import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.ernstneto.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter{
    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        //throw new UnsupportedOperationException("Unimplemented method 'doFilterInternal'");
        var servletPath = request.getServletPath();
        
        if(servletPath.startsWith("/tasks") || (servletPath.startsWith("/users") && request.getMethod().equals("GET"))){
            var authorization = request.getHeader("Authorization");
            if(authorization == null){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header missing or invalid");
                return;
            }

            System.out.println("\n\nAuthorization: " + authorization + "\n\n");
            
            var authEncoded = authorization.substring("Basic".length()).trim();
            byte[] authDBase64 = Base64.getDecoder().decode(authEncoded);
            var authDecoded = new String(Base64.getDecoder().decode(authEncoded));
            
            System.out.println("\n\nauthDecoded: " + authDecoded + "\n\n");
            System.out.println("\n\nauthDB64: " + authDBase64 + "\n\n");
            String[] authSplit = authDecoded.split(":");
            var username = authSplit[0];
            var password = authSplit[1];
            System.out.println("\n\nusername: " + username + "\n\n");
            System.out.println("\n\npassword: " + password + "\n\n");
            
            var user = this.userRepository.findByUsername(username);
            var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
            
            if(user != null && passwordVerify.verified){
                request.setAttribute("idUser", user.getId());
                filterChain.doFilter(request, response);
            }
            else{
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid password");
            }
        }
        
        else{
            filterChain.doFilter(request, response);    
        }
    }    
}
