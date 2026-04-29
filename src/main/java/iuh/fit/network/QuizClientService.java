package iuh.fit.network;

import iuh.fit.dto.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * QuizClientService - Quản lý kết nối socket từ GUI tới QuizServer
 * Singleton pattern để tái sử dụng kết nối
 */
public class QuizClientService {
    private static QuizClientService instance;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9999;

    private QuizClientService() {
    }

    /**
     * Lấy instance singleton
     */
    public static synchronized QuizClientService getInstance() {
        if (instance == null) {
            instance = new QuizClientService();
        }
        return instance;
    }

    /**
     * Kết nối tới server
     */
    public void connect() throws Exception {
        if (socket == null || socket.isClosed()) {
            try {
                System.out.println("[Client] Connecting to " + SERVER_HOST + ":" + SERVER_PORT + "...");
                socket = new Socket(SERVER_HOST, SERVER_PORT);
                // Khởi tạo output trước input để tránh deadlock
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                System.out.println("[Client] ✓ Connected to QuizServer successfully!");
            } catch (Exception e) {
                System.err.println("[Client] ✗ Failed to connect: " + e.getMessage());
                throw new RuntimeException("Không thể kết nối tới QuizServer tại " + SERVER_HOST + ":" + SERVER_PORT + 
                    "\nHãy kiểm tra:\n" +
                    "  1. QuizServer có đang chạy không?\n" +
                    "  2. MongoDB service có sẵn sàng không?\n" +
                    "  3. Kiểm tra logs của QuizServer để xem chi tiết lỗi", e);
            }
        }
    }

    /**
     * Đóng kết nối
     */
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Đã ngắt kết nối khỏi server.");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi ngắt kết nối: " + e.getMessage());
        }
    }

    /**
     * Gửi request và nhận response từ server
     */
    private Response sendRequest(Request request) throws Exception {
        if (socket == null || socket.isClosed()) {
            throw new RuntimeException("Không có kết nối tới server. Vui lòng kết nối lại.");
        }

        synchronized (this) {
            out.writeObject(request);
            out.flush();

            Object obj = in.readObject();
            if (obj == null) {
                throw new RuntimeException("Mất kết nối với server.");
            }
            return (Response) obj;
        }
    }

    /**
     * Đăng nhập
     */
    public UserDTO login(String userId, String password) throws Exception {
        Request request = Request.builder()
                .commandType(CommandType.LOGIN)
                .object(new String[]{userId, password})
                .build();

        Response response = sendRequest(request);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        return (UserDTO) response.getData();
    }

    /**
     * Lấy tất cả đề thi
     */
    @SuppressWarnings("unchecked")
    public List<QuizDTO> getAllQuizzes() throws Exception {
        Request request = Request.builder()
                .commandType(CommandType.GET_ALL_QUIZZES)
                .object(null)
                .build();

        Response response = sendRequest(request);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        return (List<QuizDTO>) response.getData();
    }

    /**
     * Lấy đề thi theo ID
     */
    public QuizDTO getQuizById(String quizId) throws Exception {
        Request request = Request.builder()
                .commandType(CommandType.GET_QUIZ_BY_ID)
                .object(quizId)
                .build();

        Response response = sendRequest(request);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        return (QuizDTO) response.getData();
    }

    /**
     * Nộp bài thi
     */
    public SubmissionDTO submitQuiz(SubmissionDTO submission) throws Exception {
        Request request = Request.builder()
                .commandType(CommandType.SUBMIT_QUIZ)
                .object(submission)
                .build();

        Response response = sendRequest(request);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        return (SubmissionDTO) response.getData();
    }

    /**
     * Đổi mật khẩu
     */
    public boolean changePassword(String userId, String oldPassword, String newPassword) throws Exception {
        Request request = Request.builder()
                .commandType(CommandType.CHANGE_PASSWORD)
                .object(new String[]{userId, oldPassword, newPassword})
                .build();

        Response response = sendRequest(request);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMessage());
        }
        return true;
    }

    /**
     * Kiểm tra xem đã kết nối hay chưa
     */
    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }
}
