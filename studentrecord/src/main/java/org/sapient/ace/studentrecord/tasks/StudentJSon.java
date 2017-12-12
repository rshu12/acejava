package org.sapient.ace.studentrecord.tasks;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.sapient.ace.studentrecord.model.Student;
import org.sapient.ace.studentrecord.repositories.StudentRepository;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StudentJSon implements Callable<String> {

	private final StudentRepository studentRepository;

	@Inject
	public StudentJSon(final StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}

	@Override
	public String call() throws Exception {
		getStudentDBData();

		return "path";
	}

	void getStudentDBData() {
		List<Student> students = studentRepository.findAll();

		ObjectMapper objectMapper = new ObjectMapper();
		List<Student> list = students.stream()
				.sorted(Comparator.comparingInt(Student::getTotalMarks)
						.thenComparing(student -> student.getResult().equals("FAIL")).reversed())
				.collect(Collectors.toList());

		list.forEach(student -> {
			try {
				Student stu = (Student) student;
				objectMapper.writeValue(
						new File("E:/rajat/file/db_to_JSON/" + student.getName().trim() + "_" + stu.getId() + ".json"),
						student);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
