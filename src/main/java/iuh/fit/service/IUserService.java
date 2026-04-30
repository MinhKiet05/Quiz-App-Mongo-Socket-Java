package iuh.fit.service;

import iuh.fit.dto.UserDTO;
import java.util.List;

public interface IUserService {
    UserDTO login(String id, String password);
    void changePassword(String id, String oldPassword, String newPassword);
    List<UserDTO> getAllUsers();
    List<UserDTO> searchUsers(String keyword, String role, String status);
    void addUser(UserDTO userDTO);
    void updateUser(UserDTO userDTO);
    void deleteUser(String id);
    void resetPassword(String userId, String newPassword);
}
