package com.banking.auth.service.mapper;

import com.banking.auth.service.dto.request.RegisterRequest;
import com.banking.auth.service.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegisterRequest registerRequest);
}
