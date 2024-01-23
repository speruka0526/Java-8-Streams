package java8features;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Java8Features {
	
	/*
	 * Using Java 8 Features - Streams & Collections
	 */

	public static void main(String[] args) {
		List<Student> students = 
						Stream.of(
						new Student(1,"Gill", 30, "Male", "Bioinformatics Engineering", "India", 03, Arrays.asList("7174602822", "3024691177")),
						new Student(2,"Maxwell", 29, "Male", "Comp Science Engineering", "Australia", 12, Arrays.asList("7174602822", "3024691177")),
						new Student(3,"Williamson", 27, "Male", "Health Engineering", "NewZealand", 21, Arrays.asList("7174602822", "3024691177")),
						new Student(4,"Dhube", 31, "Male", "Public Engineering", "Gujarat", 64, Arrays.asList("7174602822", "3024691177")),
						new Student(5,"Kohli", 33, "Male", "Mechanical Engineering", "Hyderabad", 01, Arrays.asList("7174602822", "3024691177")),
						new Student(6,"Rohit", 32, "Male", "Mechanocal Engineering", "Hyderabad", 02, Arrays.asList("7174602822", "3024691177")),
						new Student(7,"Rahul", 34, "Male", "Bioinformatics", "Mumbai", 13, Arrays.asList("7174602822", "3024691177")),
						new Student(8,"Sandeep", 26, "Male", "Electronics Engineering", "Telangana", 18, Arrays.asList("7174602822", "3024691177")),
						new Student(9,"Bhuvi", 28, "Male", "Bioinformatics Engineering", "Assam", 21, Arrays.asList("7174602822", "3024691177")),
						new Student(10,"Rinku", 29, "Male", "computer Engineering", "Bihar", 33, Arrays.asList("7174602822", "3024691177")),
						new Student(11,"Kuldeep", 27, "Male", "Instrumentation Engineering", "Hyderabad", 47, Arrays.asList("7174602822", "3024691177")),
					    new Student(12,"Chahal", 26, "Male", "Health Informatics", "Hyderabad", 51, Arrays.asList("6823817430", "3024691122")))
			.collect(Collectors.toList());
		
		// Get students between rank
		List<Student> btwRankStudents = students.stream().filter(student -> student.getRank()>10 && student.getRank() < 50).collect(Collectors.toList());
		System.out.println(btwRankStudents);		
		
		// Get students age by sorting order
		List<Student> getAgeBySortingAsc = students.stream()
				.filter(student -> student.getCity().equals("Hyderabad"))
				.sorted(Comparator.comparing(Student:: getAge)).collect(Collectors.toList());
		List<Student> getAgeBySortingDesc = students.stream()
							.filter(student -> student.getCity().equals("Hyderabad"))
							.sorted(Comparator.comparing(Student:: getAge).reversed()).collect(Collectors.toList());
		System.out.println(getAgeBySortingDesc);	
		
		// Get all departments with & without duplicates
		List<String> getAllDepartments = students.stream().map(Student:: getDept).distinct().collect(Collectors.toList());
		
		Set<String> getAllDepartmentWithOutDuplicates = students.stream().map(Student:: getDept).collect(Collectors.toSet());
		System.out.println(getAllDepartmentWithOutDuplicates);		
		//one to one relation in contacts use map
		List<List<String>> getAllContacts = students.stream().map(Student::getContacts).distinct().collect(Collectors.toList());
		//one to many relation in contacts use flatMap
		List<String> getAllContactsBy = students.stream().flatMap(student -> student.getContacts().stream()).collect(Collectors.toList());
		System.out.println(getAllContactsBy);	
		
		// Get students count by department
		Map<String, List<Student>> getStudGroupBy = students.stream().collect(Collectors.groupingBy(Student:: getDept));
		Map<String, Long> getStudGroupByDept = students.stream().collect(Collectors.groupingBy(Student:: getDept, Collectors.counting()));
		System.out.println(getStudGroupByDept);	
		
		Map.Entry<String, Long> getStudGroupByDept1 = students.stream().collect(Collectors.groupingBy(Student:: getDept, Collectors.counting())).entrySet().stream().max(Map.Entry.comparingByValue()).get();
		System.out.println(getStudGroupByDept1);	
		
		Map<String, Double> avgStudents = students.stream().collect(Collectors.groupingBy(Student:: getGender, Collectors.averagingLong(Student:: getAge)));
		System.out.println(avgStudents);	
		
		//Highest Rank in each Department
		Map<String, Optional<Student>> stud = students.stream()
				.collect(Collectors.groupingBy(Student:: getDept, Collectors.minBy(Comparator.comparing(Student::getRank))));
		System.out.println(stud);	
		
		//Find the student who has second rank
		Student student = students.stream()
								  .sorted(Comparator.comparing(Student::getRank)).skip(1).findFirst().get();
		System.out.println(student);
	}
}
