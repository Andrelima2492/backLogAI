package com.nulhart.dto.anime;

public record MALTokenResponse(String access_token, String refresh_token, String token_type, Integer expires_in ) {
}
