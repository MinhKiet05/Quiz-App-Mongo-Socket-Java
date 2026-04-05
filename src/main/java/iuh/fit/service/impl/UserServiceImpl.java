package iuh.fit.service.impl;

import iuh.fit.dto.UserDTO;
import iuh.fit.entity.User;
import iuh.fit.repository.IUserRepository;
import iuh.fit.service.IUserService;
import lombok.RequiredArgsConstructor;

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
}