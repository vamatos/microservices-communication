package br.com.vamatos.product_api.modules.jwt.dto;


import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse {

    private Integer id;

    private String name;

    private String email;

    public static JwtResponse getUser(Claims jwtClaims) {
        try {
            return JwtResponse.builder()
                    .id(jwtClaims.get("id", Integer.class))
                    .name(jwtClaims.get("name", String.class))
                    .email(jwtClaims.get("email", String.class)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
