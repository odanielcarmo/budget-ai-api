package com.dio.budget;

import jakarta.validation.constraints.NotBlank;

public record CommandRequest(@NotBlank String command) {
}
