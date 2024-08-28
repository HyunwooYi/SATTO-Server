package com.example.satto.domain.timeTable.dto;

import java.util.List;

public record CompareTimeTableRequestDTO(
        List<String> studentIds
) {
}
