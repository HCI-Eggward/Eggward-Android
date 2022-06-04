package com.example.eggward.MyPets;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class CustomMypetListDto {
    private int level;
    private String name;

}
