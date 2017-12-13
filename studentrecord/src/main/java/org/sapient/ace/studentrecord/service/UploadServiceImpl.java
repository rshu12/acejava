package org.sapient.ace.studentrecord.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.sapient.ace.studentrecord.exception.DataException;
import org.sapient.ace.studentrecord.model.Student;
import org.sapient.ace.studentrecord.model.Subject;
import org.sapient.ace.studentrecord.repositories.StudentRepository;
import org.sapient.ace.studentrecord.tasks.StudentJSon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class UploadServiceImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadServiceImpl.class);
	 private static String UPLOADED_FOLDER = "E://rajat//file//xml_upload";
	/*private static String UPLOADED_FOLDER = new StringBuilder(System.getProperty("user.dir")).append(File.separator)
			.append("xml_upload").toString();*/

	private final StudentRepository studentRepository;

	@Autowired
	private StudentJSon studentJSon;

	@Inject
	public UploadServiceImpl(final StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}

	public Boolean uploadOperation(MultipartFile file)
			throws DataException, ParserConfigurationException, SAXException, IOException {

		// checkData()
//		Utility.createDirectory(UPLOADED_FOLDER);
		Path path = uploadFileLocal(file);
		processDataIntoDB(path);
		createJson();

		return true;
	}

	private void createJson() {

		ExecutorService executorService = Executors.newFixedThreadPool(1);
		executorService.submit(studentJSon);
		executorService.shutdown();
	}

	private void processDataIntoDB(Path path) throws ParserConfigurationException, SAXException, IOException {

		File file = path.toFile();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);

		doc.getDocumentElement().normalize();

		List<Student> students = createStudent(doc);

		studentRepository.save(students);

	}

	private List<Student> createStudent(Document doc) {
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

		List<Student> students = new ArrayList<>();

		NodeList nList = doc.getElementsByTagName("Student");
		for (int i = 0; i < nList.getLength(); i++) {
			Student student = new Student();
			Node node = nList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				student.setId(element.getAttribute("ID"));
				student.setName(element.getElementsByTagName("Name").item(0).getTextContent());
				student.setClas(element.getElementsByTagName("Class").item(0).getTextContent());
				if (studentRepository.findOne(student.getId()) != null) {
					studentRepository.delete(student.getId());
				}

				Set<Subject> subjects = createSubject(student, element);
				if (subjects.stream().anyMatch(subject -> subject.getMarks() < 35))
					student.setResult(false);
				else
					student.setResult(true);
				student.setSubject(subjects);
				student.setTotalMarks(getTotalMarks(subjects));
				students.add(student);
			}
		}

		return students;
	}

	private Integer getTotalMarks(Set<Subject> subjects) {
		Iterator<Subject> itr = subjects.iterator();
		Integer total = 0;
		while (itr.hasNext()) {
			Subject subject = itr.next();
			int marks = subject.getMarks();
			total = total + subject.getMarks();

		}
		return total;
	}

	private Set<Subject> createSubject(Student student, Element element) {
		NodeList sList = element.getElementsByTagName("Subjects").item(0).getChildNodes();
		Set<Subject> subjects = new HashSet<>();
		for (int j = 0; j < sList.getLength(); j++) {
			Node sNode = sList.item(j);
			if (sNode.getNodeType() == Node.ELEMENT_NODE) {
				Element element2 = (Element) sNode;
				String id = element2.getElementsByTagName("id").item(0).getTextContent();
				String sName = element2.getElementsByTagName("name").item(0).getTextContent();
				String sMarks = element2.getElementsByTagName("marks").item(0).getTextContent();
				Subject subject = new Subject(id, sName, Integer.parseInt(sMarks), student);
				subjects.add(subject);
			}
		}
		return subjects;
	}

	private Path uploadFileLocal(MultipartFile file) throws DataException {
		try {

			byte[] bytes = file.getBytes();
			File file2 = new File(file.getOriginalFilename());

			Path path = Paths.get(UPLOADED_FOLDER + File.separator + file2.getName());
			Files.write(path, bytes);

			LOGGER.info("File loaded sussessfully::Server File Location=" + path.getFileName());

			return path;

		} catch (Exception e) {
			String msg = new StringBuilder().append("You failed to upload => ").append(e.getMessage()).toString();
			LOGGER.error(msg, e);
			throw new DataException(msg);

		}
	}

}