package iuh.fit.repository;

import iuh.fit.entity.Subject;
import java.util.List;

public interface ISubjectRepository {
    // Lấy tất cả môn học
    List<Subject> getAll();

    // Lấy chi tiết một môn học theo ID
    Subject findById(String id);
}
