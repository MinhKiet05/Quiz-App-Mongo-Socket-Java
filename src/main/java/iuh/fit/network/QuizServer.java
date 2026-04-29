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
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizServer {
    public static void main(String[] args) {
        try {
            // Kiểm tra kết nối MongoDB trước
            System.out.println("[Server] Đang kết nối tới MongoDB tại localhost:27017...");
            try {
                iuh.fit.db.MongoDbConnection.getInstance().getDatabase();
                System.out.println("[Server] ✓ MongoDB kết nối thành công!");
            } catch (Exception e) {
                System.err.println("[Server] ✗ Lỗi kết nối MongoDB: " + e.getMessage());
                System.err.println("[Server] Hãy kiểm tra:");
                System.err.println("  1. MongoDB service đã chạy chưa?");
                System.err.println("  2. MongoDB listening trên port 27017?");
                System.err.println("  3. Database 'QuizAppDB' có tồn tại không?");
                throw e;
            }

            // Mở port 9999 và tạo pool 20 threads
            ServerSocket serverSocket = new ServerSocket(9999);
            java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(20);

            System.out.println("[Server] Quiz Server is running on port 9999...");
            System.out.println("[Server] Waiting for client connections...");

            while (true) {
                try {
                    java.net.Socket socket = serverSocket.accept();
                    System.out.println("[Server] ✓ Client connected: " + socket.getRemoteSocketAddress());

                    QuizRequestHandler handler = new QuizRequestHandler(socket);
                    pool.submit(handler);
                } catch (Exception e) {
                    System.err.println("[Server] Error accepting client: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("[Server] ✗ FATAL: Server failed to start");
            e.printStackTrace();
            System.exit(1);
        }
    }
}

class QuizRequestHandler implements Runnable {
    private Socket socket;
    private IUserService userService;
    private IQuizService quizService;
    private ISubmissionService submissionService;
    private iuh.fit.service.IQuestionService questionService;
    private iuh.fit.service.ISubjectService subjectService;

    public QuizRequestHandler(Socket socket) {
        this.socket = socket;

        try {
            // Khởi tạo các Repository MongoDB
            UserRepositoryImpl userRepo = new UserRepositoryImpl();
            QuizRepositoryImpl quizRepo = new QuizRepositoryImpl();
            QuestionRepositoryImpl questionRepo = new QuestionRepositoryImpl();
            SubmissionRepositoryImpl submissionRepo = new SubmissionRepositoryImpl();
            iuh.fit.repository.impl.SubjectRepositoryImpl subjectRepo = new iuh.fit.repository.impl.SubjectRepositoryImpl();

            // Tiêm (Inject) Repository vào Service
            this.userService = new UserServiceImpl(userRepo);
            this.quizService = new QuizServiceImpl(quizRepo, questionRepo);
            this.submissionService = new SubmissionServiceImpl(submissionRepo, this.quizService);
            this.questionService = new iuh.fit.service.impl.QuestionServiceImpl(questionRepo);
            this.subjectService = new iuh.fit.service.impl.SubjectServiceImpl(subjectRepo);
            
            System.out.println("[Handler] ✓ Services initialized for client: " + socket.getRemoteSocketAddress());
        } catch (Exception e) {
            System.err.println("[Handler] ✗ Failed to initialize services: " + e.getMessage());
            e.printStackTrace();
            try {
                socket.close();
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void run() {
        // Luôn khởi tạo Output trước Input để tránh Deadlock trong Socket
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("[Handler] ✓ I/O streams initialized for " + socket.getRemoteSocketAddress());

            while (true) {
                try {
                    Object obj = in.readObject();
                    if (obj == null) break;

                    Request request = (Request) obj;
                    System.out.println("[Handler] Received request: " + request.getCommandType() + " from " + socket.getRemoteSocketAddress());
                    
                    Response response = processRequest(request);

                    out.writeObject(response);
                    out.flush();
                    System.out.println("[Handler] ✓ Response sent for " + request.getCommandType());
                } catch (EOFException e) {
                    System.out.println("[Handler] Client disconnected normally: " + socket.getRemoteSocketAddress());
                    break;
                } catch (Exception e) {
                    System.err.println("[Handler] Error processing request: " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("[Handler] I/O Error: " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("[Handler] Socket closed for " + socket.getRemoteSocketAddress());
            } catch (Exception ignored) {}
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
                case GET_ALL_QUIZZES -> {
                    java.util.List<iuh.fit.dto.QuizDTO> quizzes = quizService.getAllQuizzes();
                    return Response.builder().success(true).data(quizzes).message("Tải danh sách đề thi thành công").build();
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
                case CHANGE_PASSWORD -> {
                    // Yêu cầu Client gửi mảng String [userId, oldPassword, newPassword]
                    String[] passwordData = (String[]) request.getObject();
                    userService.changePassword(passwordData[0], passwordData[1], passwordData[2]);
                    return Response.builder().success(true).message("Đổi mật khẩu thành công").build();
                }
                
                // ============ Quản lý Câu hỏi ============
                case ADD_QUESTION -> {
                    // Yêu cầu Client gửi QuestionDTO
                    iuh.fit.dto.QuestionDTO questionDTO = (iuh.fit.dto.QuestionDTO) request.getObject();
                    questionService.addQuestion(questionDTO);
                    return Response.builder().success(true).message("Thêm câu hỏi thành công").build();
                }
                case UPDATE_QUESTION -> {
                    // Yêu cầu Client gửi QuestionDTO
                    iuh.fit.dto.QuestionDTO questionDTO = (iuh.fit.dto.QuestionDTO) request.getObject();
                    questionService.updateQuestion(questionDTO);
                    return Response.builder().success(true).message("Cập nhật câu hỏi thành công").build();
                }
                case DELETE_QUESTION -> {
                    // Yêu cầu Client gửi questionId (String)
                    String questionId = (String) request.getObject();
                    questionService.deleteQuestion(questionId);
                    return Response.builder().success(true).message("Xóa câu hỏi thành công").build();
                }
                case GET_QUESTIONS_BY_SUBJECT -> {
                    // Yêu cầu Client gửi subjectId (String)
                    String subjectId = (String) request.getObject();
                    java.util.List<iuh.fit.dto.QuestionDTO> questions = questionService.findBySubjectId(subjectId);
                    return Response.builder().success(true).data(questions).message("Tải danh sách câu hỏi thành công").build();
                }
                case SEARCH_QUESTIONS -> {
                    // Yêu cầu Client gửi keyword (String)
                    String keyword = (String) request.getObject();
                    java.util.List<iuh.fit.dto.QuestionDTO> questions = questionService.searchQuestions(keyword);
                    return Response.builder().success(true).data(questions).message("Tìm kiếm câu hỏi thành công").build();
                }
                case GET_ALL_QUESTIONS -> {
                    java.util.List<iuh.fit.dto.QuestionDTO> questions = questionService.getAllQuestions();
                    return Response.builder().success(true).data(questions).message("Tải tất cả câu hỏi thành công").build();
                }
                case DELETE_QUESTIONS_BY_SUBJECT -> {
                    // Yêu cầu Client gửi subjectId (String)
                    String subjectId = (String) request.getObject();
                    questionService.deleteQuestionsBySubject(subjectId);
                    return Response.builder().success(true).message("Xóa tất cả câu hỏi của môn học thành công").build();
                }
                case GET_QUESTION_COUNT_BY_SUBJECT -> {
                    // Yêu cầu Client gửi subjectId (String)
                    String subjectId = (String) request.getObject();
                    long count = questionService.getQuestionCountBySubject(subjectId);
                    return Response.builder().success(true).data(count).message("Đếm câu hỏi thành công").build();
                }
                
                // ============ Quản lý Môn học ============
                case GET_ALL_SUBJECTS -> {
                    java.util.List<iuh.fit.dto.SubjectDTO> subjects = subjectService.getAllSubjects();
                    return Response.builder().success(true).data(subjects).message("Tải danh sách môn học thành công").build();
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
