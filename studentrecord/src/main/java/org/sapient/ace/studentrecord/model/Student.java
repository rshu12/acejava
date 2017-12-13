package org.sapient.ace.studentrecord.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "student")
public class Student implements Serializable {

	private static final long serialVersionUID = 6434294242124362997L;

	@Id
	@GenericGenerator(name = "sequence_student_id", strategy = "org.sapient.ace.studentrecord.utility.StudentIdGenerator")
	@GeneratedValue(generator = "sequence_student_id") 
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "student_id", updatable=false, nullable=false)
	private String id;

	private String name;

	private String clas;
	
	private Integer totalMarks;
	
	@Transient
	private Integer rank;
	
	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer i) {
		this.rank = i;
	}


	private Boolean result;
	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}


	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonManagedReference
	private Set<Subject> subject;

	public Integer getTotalMarks() {
		return totalMarks;
	}

	public void setTotalMarks(Integer totalMarks) {
		this.totalMarks = totalMarks;
	}


	public Set<Subject> getSubject() {
		return subject;
	}

	public void setSubject(Set<Subject> subject) {
		this.subject = subject;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClas() {
		return clas;
	}

	public void setClas(String clas) {
		this.clas = clas;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ClassPojo [Name = " + name + ", Subjects = " + subject + ", Class = " + clas + ", ID = " + id + "]";
	}

}