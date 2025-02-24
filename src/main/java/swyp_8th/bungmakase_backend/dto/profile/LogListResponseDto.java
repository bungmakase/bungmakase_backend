package swyp_8th.bungmakase_backend.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogListResponseDto {
    //fish-bun record identifier(String)
    private String logId;
    //the URL of the first image attached to that record (null if none)
    private String imageUrl;
}
