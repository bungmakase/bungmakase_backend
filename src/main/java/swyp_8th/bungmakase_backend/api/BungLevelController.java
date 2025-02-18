package swyp_8th.bungmakase_backend.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import swyp_8th.bungmakase_backend.api.dto.UserLevelResponseDto;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.service.BungLevelService;

@Controller
@RequestMapping("/api/level")
public class BungLevelController {

    private final BungLevelService service;

    public BungLevelController(BungLevelService service) {
        this.service = service;
    }


    @GetMapping("/user")
    public ResponseEntity<ResponseTemplate<UserLevelResponseDto>> getUser(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        //extract Jwt_Access_Token form "Bearer JWT_ACCESS_TOKEN"
        String token = extractToken(authorizationHeader);





        return null;
    }

    private String extractToken(String header) {
        if(header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }
}
