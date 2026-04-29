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
        // 1. Lấy môn học từ Repository
        Subject subject = subjectRepository.findById(id);

        if (subject == null) {
            throw new RuntimeException("Không tìm thấy môn học với ID: " + id);
        }

        // 2. Chuyển đổi sang DTO
        return DataMapper.map(subject, SubjectDTO.class);
    }
}
