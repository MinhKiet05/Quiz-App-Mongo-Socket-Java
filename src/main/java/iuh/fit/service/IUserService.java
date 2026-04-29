package iuh.fit.service;

import iuh.fit.dto.UserDTO;

public interface IUserService {
    UserDTO login(String id, String password);
    void changePassword(String id, String oldPassword, String newPassword);
}
