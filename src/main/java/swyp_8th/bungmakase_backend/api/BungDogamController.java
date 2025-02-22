package swyp_8th.bungmakase_backend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swyp_8th.bungmakase_backend.domain.BungDogam;
import swyp_8th.bungmakase_backend.dto.bung_dogam.BungListResponseDto;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;
import swyp_8th.bungmakase_backend.globals.response.ResponseTemplate;
import swyp_8th.bungmakase_backend.service.BungDogamService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = {"https://bungmakase.vercel.app", "http://localhost:3000"})
@RequiredArgsConstructor
@RequestMapping("/api/dogam")
public class BungDogamController {

    private final BungDogamService bungDogamService;

    @GetMapping("/list")
    public ResponseEntity<ResponseTemplate<List<BungListResponseDto>>> getAllBungDogam() {
        try {
            List<BungDogam> bungDogamList = bungDogamService.getAllBungDogam();

            // BungListResponseDto로 변환
            List<BungListResponseDto> bungList = bungDogamList.stream()
                    .map(bung -> new BungListResponseDto(
                            bung.getId().toString(),
                            bung.getBungName()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseTemplate<>(SuccessCode.SUCCESS_200, bungList));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseTemplate<>(FailureCode.SERVER_ERROR_500, null));
        }
    }


}
