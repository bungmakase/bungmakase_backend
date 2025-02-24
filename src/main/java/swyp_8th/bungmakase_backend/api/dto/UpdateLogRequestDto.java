package swyp_8th.bungmakase_backend.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class UpdateLogRequestDto {
    private String bungName;
    private int bungCount;
    private List<String> tags;
    private LocalDate date;
}
