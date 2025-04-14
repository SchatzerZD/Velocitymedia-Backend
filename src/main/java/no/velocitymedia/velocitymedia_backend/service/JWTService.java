package no.velocitymedia.velocitymedia_backend.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.annotation.PostConstruct;
import no.velocitymedia.velocitymedia_backend.model.UserEntity;

@Service
public class JWTService {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;

    private Algorithm algorithm;

    @PostConstruct
    public void postConstruct(){
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    public String generateJWT(UserEntity user){
        return JWT.create()
            .withClaim("USERNAME", user.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + (1000*expiryInSeconds)))
            .withIssuer(issuer)
            .sign(algorithm);
    }

    public String getUsername(String token){
        return JWT.decode(token).getClaim("USERNAME").asString();
    }
}
