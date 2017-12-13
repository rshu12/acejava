package org.sapient.ace.studentrecord.repositories;

import java.io.Serializable;
import java.util.List;

import org.sapient.ace.studentrecord.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Serializable>{
	
//	public List<Student> findByStudentid(List<Student> students);

}
