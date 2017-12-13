package org.sapient.ace.studentrecord.tasks;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.sapient.ace.studentrecord.model.Student;
import org.sapient.ace.studentrecord.repositories.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StudentJSon implements Callable<String> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StudentJSon.class);
	
	private final String jsonPath="E:/rajat/file/db_to_JSON/";
	/*private final String jsonPath=new StringBuilder(System.getProperty("user.dir")).append(File.separator)
	.append("json_upload").toString();*/

	private final StudentRepository studentRepository;

	@Inject
	public StudentJSon(final StudentRepository studentRepository) throws IOException {
		this.studentRepository = studentRepository;
//		Utility.createDirectory(jsonPath);
	}

	@Override
	public String call() throws Exception {
		getStudentDBData();

		return jsonPath;
	}

	void getStudentDBData() {
		int i=1;
		
		List<Student> students = studentRepository.findAll();

		ObjectMapper objectMapper = new ObjectMapper();
		List<Student> list = students.stream().filter(student -> student.getResult().equals(true))
				.sorted(Comparator.comparingInt(Student::getTotalMarks)
						/*.thenComparing(student -> student.getResult().equals("FAIL"))*/.reversed())
				.collect(Collectors.toList());
		for(Student st: list) {
			st.setRank(i++);
		}
		System.out.println("List size::"+ list.size()+list);

		students.forEach(student -> {
				Student stu = (Student) student;
				try {
					objectMapper.writeValue(
							new File( jsonPath+ student.getName().trim() + "_" + stu.getId() + ".json"),
							student);
				} catch (IOException e) {
					e.printStackTrace();
					LOGGER.error(e.getMessage(), e);
				}
				
		});
	}
}
