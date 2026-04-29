package iuh.fit.service;

import iuh.fit.dto.SubjectDTO;
import java.util.List;

public interface ISubjectService {
    // Lấy tất cả môn học
    List<SubjectDTO> getAllSubjects();

    // Lấy chi tiết một môn học theo ID
    SubjectDTO getSubjectById(String id);
}
