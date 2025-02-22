package swyp_8th.bungmakase_backend.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import swyp_8th.bungmakase_backend.api.dto.RankingResponseDto;
import swyp_8th.bungmakase_backend.api.dto.UserLevelResponseDto;
import swyp_8th.bungmakase_backend.exception.UnauthorizedException;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.service.BungLevelService;

import java.util.List;

@Controller
@RequestMapping("/api/level")
public class BungLevelController {

    private final BungLevelService bungLevelService;

    public BungLevelController( BungLevelService bungLevelService) {
        this.bungLevelService = bungLevelService;
    }


    @GetMapping("/user")
    public ResponseEntity<ResponseTemplate<UserLevelResponseDto>> getUser(
            @RequestHeader("Authorization") String authorizationHeader
    ) {

        //Format of Authorization header : Bearer JWT_ACCESS_TOKEN
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            ResponseTemplate<UserLevelResponseDto> failResponse =
                    new ResponseTemplate<>(FailureCode.UNAUTHORIZED_401, null);
            return ResponseEntity.status(FailureCode.UNAUTHORIZED_401.getCode()).body(failResponse);
        }

        //extract Jwt_Access_Token form "Bearer JWT_ACCESS_TOKEN"
        String token = extractToken(authorizationHeader);

        try {
            UserLevelResponseDto userLevelResponseDto = bungLevelService.getUserLevel(token);
            ResponseTemplate<UserLevelResponseDto> response = new ResponseTemplate<>(SuccessCode.SUCCESS_200, userLevelResponseDto);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException ex) {
            ResponseTemplate<UserLevelResponseDto> failResponse =
                    new ResponseTemplate<>(FailureCode.UNAUTHORIZED_401, null);
            return ResponseEntity.status(FailureCode.UNAUTHORIZED_401.getCode()).body(failResponse);

        }

    }

    @GetMapping("/rankings")
    public ResponseEntity<ResponseTemplate<List<RankingResponseDto>>> getRankings() {
        try {
            List<RankingResponseDto> rankings = bungLevelService.getRankings();
            ResponseTemplate<List<RankingResponseDto>> response =
                    new ResponseTemplate<>(SuccessCode.SUCCESS_200, rankings);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseTemplate<List<RankingResponseDto>> failResponse =
                    new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null);
            return ResponseEntity.status(FailureCode.SERVER_ERROR_500.getCode()).body(failResponse);
        }
    }




    private String extractToken(String header) {
        if(header != null && header.startsWith("Bearer ")) { //delete this method if the logic duplicated
            return header.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }
}
