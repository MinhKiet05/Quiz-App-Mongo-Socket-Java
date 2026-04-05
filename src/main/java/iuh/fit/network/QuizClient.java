package iuh.fit.network;

import iuh.fit.dto.QuizDTO;
import iuh.fit.dto.SubmissionDTO;
import iuh.fit.dto.UserDTO;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;

public class QuizClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 9999);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner sc = new Scanner(System.in)) {

            System.out.println("=== HỆ THỐNG THI TRẮC NGHIỆM ===");
            System.out.println("Đã kết nối tới Server!");

            UserDTO loggedInUser = null;

            while (true) {
                if (loggedInUser == null) {
                    System.out.print("Nhập mã số SV/GV: ");
                    String id = sc.nextLine();
                    System.out.print("Nhập mật khẩu: ");
                    String password = sc.nextLine();

                    // Đóng gói request đăng nhập
                    Request req = Request.builder()
                            .commandType(CommandType.LOGIN)
                            .object(new String[]{id, password})
                            .build();

                    out.writeObject(req);
                    out.flush();

                    Response res = (Response) in.readObject();
                    if (res.isSuccess()) {
                        loggedInUser = (UserDTO) res.getData();
                        System.out.println("-> Xin chào " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")");
                    } else {
                        System.err.println("-> Lỗi: " + res.getMessage());
                    }
                } else {
                    System.out.println("\n--- MENU ---");
                    System.out.println("1. Vào thi (Nhập mã đề)");
                    System.out.println("2. Đăng xuất");
                    System.out.print("Chọn: ");
                    int choice = Integer.parseInt(sc.nextLine());

                    if (choice == 1) {
                        System.out.print("Nhập mã đề thi (Ví dụ: quiz_001): ");
                        String quizId = sc.nextLine();

                        Request req = Request.builder()
                                .commandType(CommandType.GET_QUIZ_BY_ID)
                                .object(quizId)
                                .build();
                        out.writeObject(req);
                        out.flush();

                        Response res = (Response) in.readObject();
                        if (res.isSuccess()) {
                            QuizDTO quiz = (QuizDTO) res.getData();
                            System.out.println("-> Bắt đầu bài thi: " + quiz.getTitle());
                            System.out.println("-> Đang giả lập nộp bài...");

                            // Giả lập tạo đối tượng bài nộp
                            SubmissionDTO sub = new SubmissionDTO();
                            sub.setCandidateId(loggedInUser.getId());
                            sub.setQuizId(quiz.getId());
                            sub.setStartTime(Instant.now().toString());
                            sub.setDetails(new ArrayList<>()); // Chi tiết rỗng (coi như nộp giấy trắng)

                            Request submitReq = Request.builder()
                                    .commandType(CommandType.SUBMIT_QUIZ)
                                    .object(sub)
                                    .build();
                            out.writeObject(submitReq);
                            out.flush();

                            Response submitRes = (Response) in.readObject();
                            SubmissionDTO gradedSub = (SubmissionDTO) submitRes.getData();
                            System.out.println("-> " + submitRes.getMessage() + "! Điểm của bạn: " + gradedSub.getScore());
                        } else {
                            System.err.println("-> Lỗi mở đề: " + res.getMessage());
                        }
                    } else if (choice == 2) {
                        loggedInUser = null;
                        System.out.println("Đã đăng xuất.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Mất kết nối tới Server.");
        }
    }
}
