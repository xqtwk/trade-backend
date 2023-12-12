package grade.tradeback.websocket;

import grade.tradeback.security.jwt.JwtService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtService jwtService;  // Assuming JwtService is your JWT utility class

    public CustomHandshakeHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // Extract token from request headers and validate it
        String token = extractToken(request);
        if (token != null && jwtService.isWebSocketTokenValid(token)) {
            // Extract username from token
            String username = jwtService.extractUsername(token);
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            return new UsernamePasswordAuthenticationToken(username, null, authorities);
        }
        return null; // or throw exception if token is invalid
    }

    private String extractToken(ServerHttpRequest request) {
        // Extract the token from request parameters
        String tokenParam = request.getURI().getQuery();
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String[] pairs = tokenParam.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
        }
        String token = query_pairs.get("token");
        // Return the extracted token
        return token;
    }

}
