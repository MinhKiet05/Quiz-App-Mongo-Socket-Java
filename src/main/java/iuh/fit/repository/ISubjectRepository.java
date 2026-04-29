package iuh.fit.repository;

import iuh.fit.entity.Subject;
import java.util.List;

public interface ISubjectRepository {
    // Lấy tất cả môn học
    List<Subject> getAll();

    // Lấy chi tiết một môn học theo ID
    Subject findById(String id);

    // Thêm môn học mới
    void add(Subject subject);

    // Cập nhật môn học
    void update(Subject subject);

    // Xóa môn học theo ID
    void deleteById(String id);
}
