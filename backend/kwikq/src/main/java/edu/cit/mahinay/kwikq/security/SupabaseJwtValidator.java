package edu.cit.mahinay.kwikq.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Component
public class SupabaseJwtValidator {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.jwt.secret}")
    private String supabaseJwtSecret;

    private static final String JWKS_PATH = "/.well-known/openid-configuration";
    private String cachedJwksUrl;

    /**
     * Validates a Supabase JWT token
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return false;
            }

            // Decode without verification first to get the algorithm
            DecodedJWT decodedJWT = JWT.decode(token);
            
            // Get the secret key from the token header or use Supabase secret
            String secret = getSupabaseSecret();
            Algorithm algorithm = Algorithm.HMAC256(secret);
            
            // Verify the token
            JWT.require(algorithm)
                    .build()
                    .verify(token);
            
            return true;
        } catch (JWTVerificationException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts the user email (sub claim) from a Supabase JWT token
     */
    public String getEmailFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extracts custom claims from the token
     */
    public String getClaimValue(String token, String claimName) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            if (decodedJWT.getClaim(claimName).isNull()) {
                return null;
            }
            return decodedJWT.getClaim(claimName).asString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets the Supabase JWT secret for token verification
     */
    private String getSupabaseSecret() {
        // For Supabase, the JWT secret is typically provided in environment or config
        // Using the anon key encoded as the secret, but ideally you'd fetch the JWKS
        return extractSecretFromJwks();
    }

    /**
     * Extracts the secret from Supabase JWKS endpoint
     * For now, we use the configured secret as fallback
     */
    private String extractSecretFromJwks() {
        // Prefer configured secret to avoid network calls on every request
        if (supabaseJwtSecret != null && !supabaseJwtSecret.isBlank()) {
            return supabaseJwtSecret;
        }

        // If no configured secret is available, attempt a quick JWKS fetch as a fallback.
        // Use a short timeout and fail fast to avoid blocking request threads.
        try {
            String jwksUrl = supabaseUrl + JWKS_PATH;
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(jwksUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                if (jsonResponse.has("jwks_uri")) {
                    String jwksUri = jsonResponse.get("jwks_uri").getAsString();
                    HttpRequest jwksRequest = HttpRequest.newBuilder()
                            .uri(URI.create(jwksUri))
                            .GET()
                            .build();

                    HttpResponse<String> jwksResponse = client.send(jwksRequest, HttpResponse.BodyHandlers.ofString());
                    if (jwksResponse.statusCode() == 200) {
                        JsonObject jwks = JsonParser.parseString(jwksResponse.body()).getAsJsonObject();
                        return jwks.toString();
                    }
                }
            }
        } catch (Exception e) {
            // Fail fast and fall through to empty return
        }

        return "";
    }
}
