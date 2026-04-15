package iuh.fit.ui.shared;

import iuh.fit.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Lớp Singleton để lưu trữ thông tin session của ứng dụng
 * Gồm: Socket kết nối, Streams I/O, và thông tin User đang đăng nhập
 */
@Getter
@Setter
public class SessionManager {
    private static SessionManager instance;
    
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private UserDTO currentUser;
    
    private SessionManager() {
    }
    
    /**
     * Lấy instance Singleton
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Kiểm tra xem có đăng nhập hay chưa
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Đăng xuất - xóa toàn bộ thông tin session
     */
    public void logout() {
        try {
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket = null;
            objectOutputStream = null;
            objectInputStream = null;
            currentUser = null;
        }
    }
}
