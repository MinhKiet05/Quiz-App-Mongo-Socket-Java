package iuh.fit.service.impl;

import iuh.fit.dto.SubjectDTO;
import iuh.fit.entity.Subject;
import iuh.fit.mapper.DataMapper;
import iuh.fit.repository.ISubjectRepository;
import iuh.fit.service.ISubjectService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SubjectServiceImpl implements ISubjectService {
    private final ISubjectRepository subjectRepository;

    @Override
    public List<SubjectDTO> getAllSubjects() {
        // 1. Lấy tất cả môn học từ Repository
        List<Subject> subjects = subjectRepository.getAll();

        // 2. Chuyển đổi sang DTO
        return DataMapper.mapList(subjects, SubjectDTO.class);
    }

    @Override
    public SubjectDTO getSubjectById(String id) {
        Subject subject = subjectRepository.findById(id);
        if (subject == null) {
            throw new RuntimeException("Không tìm thấy môn học với ID: " + id);
        }
        return DataMapper.map(subject, SubjectDTO.class);
    }

    @Override
    public void addSubject(SubjectDTO subjectDTO) {
        if (subjectDTO.getName() == null || subjectDTO.getName().isBlank()) {
            throw new RuntimeException("Tên môn học không được để trống!");
        }
        if (subjectDTO.getCourseCode() == null || subjectDTO.getCourseCode().isBlank()) {
            throw new RuntimeException("Mã môn học không được để trống!");
        }
        Subject subject = DataMapper.map(subjectDTO, Subject.class);
        // Tự tạo ID nếu chưa có
        if (subject.getId() == null || subject.getId().isBlank()) {
            subject.setId(java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        subjectRepository.add(subject);
    }

    @Override
    public void updateSubject(SubjectDTO subjectDTO) {
        if (subjectDTO.getId() == null || subjectDTO.getId().isBlank()) {
            throw new RuntimeException("ID môn học không hợp lệ!");
        }
        if (subjectDTO.getName() == null || subjectDTO.getName().isBlank()) {
            throw new RuntimeException("Tên môn học không được để trống!");
        }
        Subject subject = DataMapper.map(subjectDTO, Subject.class);
        subjectRepository.update(subject);
    }

    @Override
    public void deleteSubject(String id) {
        if (id == null || id.isBlank()) {
            throw new RuntimeException("ID môn học không hợp lệ!");
        }
        subjectRepository.deleteById(id);
    }
}
