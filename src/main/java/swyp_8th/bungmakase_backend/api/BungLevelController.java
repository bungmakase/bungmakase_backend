package swyp_8th.bungmakase_backend.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import swyp_8th.bungmakase_backend.api.dto.UserLevelDto;
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
    public ResponseEntity<ResponseTemplate<UserLevelDto>> getUser() {
        return null;
    }
}
