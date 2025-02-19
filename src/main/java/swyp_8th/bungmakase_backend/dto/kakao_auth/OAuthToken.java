package swyp_8th.bungmakase_backend.dto.kakao_auth;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class OAuthToken {
    @SerializedName("access_token")  // JSON 응답의 키와 일치하도록 설정
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("expires_in")
    private int expiresIn;

    @SerializedName("scope")
    private String scope;

    @SerializedName("refresh_token_expires_in")
    private int refreshTokenExpiresIn;
}
