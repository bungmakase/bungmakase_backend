package swyp_8th.bungmakase_backend.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Getter @Setter
public class KakaoUserInfoDto {
    private String id;
    private LocalDateTime connectedAt;
    private String email;
    private String nickname;
    private String image_url;

    public KakaoUserInfoDto(String jsonResponseBody){
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonResponseBody);

        this.id = element.getAsJsonObject().get("id").getAsString();

        String connected_at = element.getAsJsonObject().get("connected_at").getAsString();
        connected_at = connected_at.substring(0, connected_at.length() - 1);
        LocalDateTime connectDateTime = LocalDateTime.parse(connected_at, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        this.connectedAt = connectDateTime;

        JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
        this.nickname = properties.has("nickname") ? properties.getAsJsonObject().get("nickname").getAsString() : null;
        this.image_url = properties.has("profile_image_url") ? properties.getAsJsonObject().get("profile_image_url").getAsString() : null;

        JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
        this.email = kakaoAccount.has("email") ? kakaoAccount.getAsJsonObject().get("email").getAsString() : null;


    }

}
