package com.pch777.bargains.model;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NicknameDto {
	
    @NotBlank(message = "Nickname must not be blank")
	private String nickname;

}
