package com.spring.jwt.Classes;
import com.spring.jwt.entity.Classes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
 public class ClassesServiceImpl implements ClassesService{
    @Autowired
    private ClassesRepository classesRepository;
    @Autowired    private ClassMapper mapper;
    @Override    public ClassesDto createClass(ClassesDto classesDto) {
        if(classesDto==null) {
            throw new IllegalArgumentException("Class data cannot be null");
        }        Classes entity = mapper.toEntity(classesDto);
        Classes savedClass = classesRepository.save(entity);
        ClassesDto dto = mapper.toDto(savedClass);
        return dto;
    }    @Override
    public List<ClassesDto> getClassBySubject(String sub, String studentClass) {
        List<ClassesDto> classes = classesRepository.findClassBySubject(sub.toLowerCase(),
                studentClass).stream().map(mapper::toDto).collect(Collectors.toList());
        if (classes.isEmpty()) {
            throw new ClassesNotFoundException("Class not found");
        }        return classes;
    }    @Override
    public ClassesDto updateClass(Long id, ClassesDto classesDto) {
        Classes classes = classesRepository.findById(id)
                .orElseThrow(() -> new ClassesNotFoundException("Class with id : " + id + "not found"));
        if(classes.getSub()!= null){
            classes.setSub(classesDto.getSub());
        }
        if(classes.getDuration()!=null){
            classes.setDuration(classesDto.getDuration());
        }
        if(classes.getDate()!=null){
            classes.setDate(classesDto.getDate());
        }
        if(classes.getTeacherId()!=null){
            classes.setTeacherId(classesDto.getTeacherId());
        }
        if(classes.getStudentClass()!=null){
            classes.setStudentClass(classesDto.getStudentClass());
        }
        Classes savedClass = classesRepository.save(classes);
        return mapper.toDto(savedClass);
    }    @Override
    public void deleteClass(Long id) {
        Classes classes = classesRepository.findById(id).
                orElseThrow(() -> new ClassesNotFoundException("Class does not exist"));
        classesRepository.delete(classes);
    }
}