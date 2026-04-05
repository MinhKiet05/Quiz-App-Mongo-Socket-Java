package iuh.fit.network;


import iuh.fit.dto.QuizDTO;
import iuh.fit.dto.SubmissionDTO;
import iuh.fit.dto.UserDTO;
import iuh.fit.service.IUserService;
import iuh.fit.repository.impl.QuestionRepositoryImpl;
import iuh.fit.repository.impl.QuizRepositoryImpl;
import iuh.fit.repository.impl.SubmissionRepositoryImpl;
import iuh.fit.repository.impl.UserRepositoryImpl;
import iuh.fit.service.IQuizService;
import iuh.fit.service.ISubmissionService;
import iuh.fit.service.impl.QuizServiceImpl;
import iuh.fit.service.impl.SubmissionServiceImpl;
import iuh.fit.service.impl.UserServiceImpl;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizServer {
    public static void main(String[] args) {
        // Mở port 9999 và tạo pool 20 threads (chịu tải 20 thí sinh cùng lúc)
        try (ServerSocket serverSocket = new ServerSocket(9999);
             ExecutorService pool = Executors.newFixedThreadPool(20)) {

            System.out.println("Quiz Server is running on port 9999...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getRemoteSocketAddress());

                // Đẩy kết nối vào pool xử lý
                QuizRequestHandler handler = new QuizRequestHandler(socket);
                pool.submit(handler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class QuizRequestHandler implements Runnable {
    private Socket socket;

    // Khai báo các service cốt lõi
    private IUserService userService;
    private IQuizService quizService;
    private ISubmissionService submissionService;

    public QuizRequestHandler(Socket socket) {
        this.socket = socket;

        // Khởi tạo các Repository MongoDB
        UserRepositoryImpl userRepo = new UserRepositoryImpl();
        QuizRepositoryImpl quizRepo = new QuizRepositoryImpl();
        QuestionRepositoryImpl questionRepo = new QuestionRepositoryImpl();
        SubmissionRepositoryImpl submissionRepo = new SubmissionRepositoryImpl();

        // Tiêm (Inject) Repository vào Service
        this.userService = new UserServiceImpl(userRepo);
        this.quizService = new QuizServiceImpl(quizRepo, questionRepo);
        this.submissionService = new SubmissionServiceImpl(submissionRepo, this.quizService);
    }

    @Override
    public void run() {
        // Luôn khởi tạo Output trước Input để tránh Deadlock trong Socket
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            while (true) {
                Object obj = in.readObject();
                if (obj == null) break;

                Request request = (Request) obj;
                Response response = processRequest(request);

                out.writeObject(response);
                out.flush();
            }
        } catch (Exception e) {
            System.out.println("Client disconnected.");
        }
    }

    private Response processRequest(Request request) {
        try {
            switch (request.getCommandType()) {
                case LOGIN -> {
                    // Yêu cầu Client gửi mảng String [id, password]
                    String[] credentials = (String[]) request.getObject();
                    UserDTO user = userService.login(credentials[0], credentials[1]);
                    return Response.builder().success(true).data(user).message("Đăng nhập thành công").build();
                }
                case GET_QUIZ_BY_ID -> {
                    // Yêu cầu Client gửi quizId (String)
                    String quizId = (String) request.getObject();
                    QuizDTO quiz = quizService.getQuizForCandidate(quizId);
                    return Response.builder().success(true).data(quiz).message("Tải đề thi thành công").build();
                }
                case SUBMIT_QUIZ -> {
                    // Yêu cầu Client gửi SubmissionDTO
                    SubmissionDTO incomingSub = (SubmissionDTO) request.getObject();
                    SubmissionDTO gradedSub = submissionService.submitQuiz(incomingSub);
                    return Response.builder().success(true).data(gradedSub).message("Nộp bài thành công").build();
                }
                default -> {
                    return Response.builder().success(false).message("Lệnh không hợp lệ!").build();
                }
            }
        } catch (Exception e) {
            // Nếu Service ném ra lỗi (VD: sai mật khẩu, chưa tới giờ thi...), gói gọn vào Response trả về cho UI
            return Response.builder().success(false).message(e.getMessage()).build();
        }
    }
}
