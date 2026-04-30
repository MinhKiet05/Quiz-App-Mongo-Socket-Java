package iuh.fit.service.impl;

import iuh.fit.dto.UserDTO;
import iuh.fit.entity.User;
import iuh.fit.mapper.DataMapper;
import iuh.fit.repository.IUserRepository;
import iuh.fit.service.IUserService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final IUserRepository userRepository;

    @Override
    public UserDTO login(String id, String password) {
        // Gọi hàm tìm kiếm theo ID
        User user = userRepository.findByIdAndPassword(id, password);

        if (user == null) {
            throw new RuntimeException("Sai mã đăng nhập hoặc mật khẩu!"); // Đổi thông báo lỗi
        }

        // Vẫn trả về username (tức là Họ và Tên) để Client hiển thị lời chào trên màn hình
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }

    @Override
    public void changePassword(String id, String oldPassword, String newPassword) {
        // Find user by ID and old password to verify
        User user = userRepository.findByIdAndPassword(id, oldPassword);

        if (user == null) {
            throw new RuntimeException("Mật khẩu cũ không chính xác!");
        }

        // Update password
        user.setPassword(newPassword);
        userRepository.update(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return DataMapper.mapList(userRepository.findAll(), UserDTO.class);
    }

    @Override
    public List<UserDTO> searchUsers(String keyword, String role, String status) {
        return DataMapper.mapList(userRepository.search(keyword, role, status), UserDTO.class);
    }

    @Override
    public void addUser(UserDTO userDTO) {
        validateForAdd(userDTO);

        if (userRepository.findById(userDTO.getId()) != null) {
            throw new RuntimeException("Mã người dùng đã tồn tại: " + userDTO.getId());
        }

        User user = User.builder()
                .id(userDTO.getId().trim())
                .username(userDTO.getUsername().trim())
                .password(userDTO.getPassword().trim())
                .role(normalizeRole(userDTO.getRole()))
                .status(normalizeStatus(userDTO.getStatus()))
                .build();
        userRepository.add(user);
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        if (userDTO.getId() == null || userDTO.getId().isBlank()) {
            throw new RuntimeException("Mã người dùng không hợp lệ!");
        }
        if (userDTO.getUsername() == null || userDTO.getUsername().isBlank()) {
            throw new RuntimeException("Họ tên không được để trống!");
        }

        User existing = userRepository.findById(userDTO.getId().trim());
        if (existing == null) {
            throw new RuntimeException("Không tìm thấy người dùng: " + userDTO.getId());
        }

        existing.setUsername(userDTO.getUsername().trim());
        existing.setRole(normalizeRole(userDTO.getRole()));
        existing.setStatus(normalizeStatus(userDTO.getStatus()));
        userRepository.update(existing);
    }

    @Override
    public void deleteUser(String id) {
        if (id == null || id.isBlank()) {
            throw new RuntimeException("Mã người dùng không hợp lệ!");
        }
        User existing = userRepository.findById(id.trim());
        if (existing == null) {
            throw new RuntimeException("Không tìm thấy người dùng: " + id);
        }
        userRepository.deleteById(id.trim());
    }

    @Override
    public void resetPassword(String userId, String newPassword) {
        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("Mã người dùng không hợp lệ!");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new RuntimeException("Mật khẩu mới không được để trống!");
        }

        User user = userRepository.findById(userId.trim());
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng: " + userId);
        }
        user.setPassword(newPassword.trim());
        userRepository.update(user);
    }

    private void validateForAdd(UserDTO userDTO) {
        if (userDTO.getId() == null || userDTO.getId().isBlank()) {
            throw new RuntimeException("Mã người dùng không được để trống!");
        }
        if (userDTO.getUsername() == null || userDTO.getUsername().isBlank()) {
            throw new RuntimeException("Họ tên không được để trống!");
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            throw new RuntimeException("Mật khẩu không được để trống!");
        }
        if (userDTO.getPassword().trim().length() < 6) {
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự!");
        }
    }

    private String normalizeRole(String role) {
        String normalized = role == null ? "" : role.trim().toUpperCase();
        if (!"MANAGER".equals(normalized) && !"LECTURER".equals(normalized) && !"CANDIDATE".equals(normalized)) {
            throw new RuntimeException("Vai trò không hợp lệ!");
        }
        return normalized;
    }

    private String normalizeStatus(String status) {
        String normalized = status == null ? "" : status.trim().toUpperCase();
        if (!"ACTIVE".equals(normalized) && !"INACTIVE".equals(normalized)) {
            throw new RuntimeException("Trạng thái không hợp lệ!");
        }
        return normalized;
    }
}