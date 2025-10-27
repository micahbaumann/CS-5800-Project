package com.chachef;

import com.chachef.dto.ChefCreateDto;
import com.chachef.dto.UserCreateDto;

import java.util.UUID;

public class TestHelper {
    public final static String SAMPLE_CHEF_NAME = "SampleChef";
    public final static Double SAMPLE_CHEF_PRICE = 50.0;

    public final static UUID SAMPLE_USER_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    public static ChefCreateDto sampleChefDto() {
        return new ChefCreateDto(SAMPLE_USER_UUID, SAMPLE_CHEF_PRICE, SAMPLE_CHEF_NAME);
    }
}
