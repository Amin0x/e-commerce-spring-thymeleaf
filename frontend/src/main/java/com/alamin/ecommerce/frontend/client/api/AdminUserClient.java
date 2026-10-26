package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.PageDto;
import com.alamin.ecommerce.frontend.dto.UserDto;

/**
 * Admin client for user management on the backend {@code /api/admin/users}
 * endpoints.
 */
public interface AdminUserClient {

    PageDto<UserDto> list(int page, int size);

    UserDto get(Long id);

    UserDto create(UserDto user);

    UserDto update(Long id, UserDto user);

    void delete(Long id);

    Long count();
}
