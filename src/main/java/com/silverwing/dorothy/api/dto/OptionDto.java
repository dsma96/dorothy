package com.silverwing.dorothy.api.dto;

import com.silverwing.dorothy.domain.entity.Options;
import lombok.Data;

@Data
public class OptionDto {
    int optionId;
    String name;
    int idx;
    int price;

    public static OptionDto from(Options option){
        if( option == null || option.isUse() == false ){
            return null;
        }

        OptionDto optionDto = new OptionDto();
        optionDto.setOptionId(option.getOptionId());
        optionDto.setName(option.getName());
        optionDto.setIdx(option.getIdx());
        optionDto.setPrice(option.getPrice());
        return optionDto;
    }

}
