package iuh.fit.service;

import iuh.fit.dto.SubjectDTO;
import java.util.List;

public interface ISubjectService {
    // Lấy tất cả môn học
    List<SubjectDTO> getAllSubjects();

    // Lấy chi tiết một môn học theo ID
    SubjectDTO getSubjectById(String id);

    // Thêm môn học mới
    void addSubject(SubjectDTO subjectDTO);

    // Cập nhật môn học
    void updateSubject(SubjectDTO subjectDTO);

    // Xóa môn học theo ID
    void deleteSubject(String id);
}
