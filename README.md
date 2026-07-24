# java8features
Java 8 Features

**Streams *

All-in-one template — covers every major feature**

import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.util.Optional;

public class StreamsDemo {

    record Employee(String name, String dept, double salary, int age) {}

    public static void main(String[] args) {

        List<Employee> employees = List.of(
            new Employee("Alice", "Eng",  120_000, 30),
            new Employee("Bob",   "Eng",   95_000, 25),
            new Employee("Carol", "HR",    80_000, 35),
            new Employee("Dave",  "HR",    75_000, 28),
            new Employee("Eve",   "Fin",  110_000, 40)
        );

        // ── 1. CREATION ─────────────────────────────────────────────────
        Stream<Integer> fromOf      = Stream.of(1, 2, 3);
        Stream<String>  fromList    = employees.stream();          // unused var for demo
        IntStream       range       = IntStream.rangeClosed(1, 10);
        Stream<String>  generated   = Stream.generate(() -> "x").limit(3);
        Stream<Long>    iterated    = Stream.iterate(1L, n -> n * 2).limit(5);

        // ── 2. FILTERING ────────────────────────────────────────────────
        List<Employee> engHighPay = employees.stream()
            .filter(e -> e.dept().equals("Eng"))          // predicate
            .filter(e -> e.salary() > 90_000)
            .toList();

        // ── 3. MAPPING / TRANSFORMATION ─────────────────────────────────
        List<String> names = employees.stream()
            .map(Employee::name)                           // method ref
            .map(String::toUpperCase)
            .toList();

        List<String> words = List.of("hello world", "foo bar");
        List<String> tokens = words.stream()
            .flatMap(s -> Arrays.stream(s.split(" ")))     // flatten nested
            .toList();

        // ── 4. PRIMITIVE STREAMS (avoid boxing overhead) ─────────────────
        double avgSalary = employees.stream()
            .mapToDouble(Employee::salary)
            .average()
            .orElse(0);

        int totalAge = employees.stream()
            .mapToInt(Employee::age)
            .sum();

        DoubleSummaryStatistics stats = employees.stream()
            .mapToDouble(Employee::salary)
            .summaryStatistics();                       // min/max/avg/sum/count

        // ── 5. SORTING & SLICING ─────────────────────────────────────────
        List<Employee> top3 = employees.stream()
            .sorted(Comparator.comparingDouble(Employee::salary).reversed())
            .limit(3)                                     // short-circuit
            .toList();

        List<Employee> paged = employees.stream()
            .sorted(Comparator.comparing(Employee::name))
            .skip(1).limit(2)                             // pagination
            .toList();

        List<String> distinctDepts = employees.stream()
            .map(Employee::dept)
            .distinct()
            .sorted()
            .toList();

        // ── 6. MATCHING & FINDING (short-circuit terminals) ───────────────
        boolean anyRich  = employees.stream().anyMatch(e -> e.salary() > 100_000);
        boolean allAdult = employees.stream().allMatch(e -> e.age() >= 18);
        boolean noneNeg  = employees.stream().noneMatch(e -> e.salary() < 0);

        Optional<Employee> first = employees.stream()
            .filter(e -> e.dept().equals("HR"))
            .findFirst();                                // Optional!

        Optional<Employee> any = employees.parallelStream()
            .filter(e -> e.age() < 30)
            .findAny();                                  // faster in parallel

        // ── 7. OPTIONAL — chaining without null checks ───────────────────
        String result = first
            .map(Employee::name)
            .filter(n -> n.startsWith("C"))
            .orElse("Unknown");

        // ── 8. REDUCTION ────────────────────────────────────────────────
        double totalSalary = employees.stream()
            .map(Employee::salary)
            .reduce(0.0, Double::sum);

        Optional<Employee> maxEarner = employees.stream()
            .reduce((a, b) -> a.salary() > b.salary() ? a : b);

        // ── 9. COLLECTORS ───────────────────────────────────────────────
        // toList / toSet / toUnmodifiableList
        Set<String> deptSet = employees.stream()
            .map(Employee::dept)
            .collect(Collectors.toUnmodifiableSet());

        // joining
        String nameList = employees.stream()
            .map(Employee::name)
            .collect(Collectors.joining(", ", "[", "]"));

        // groupingBy — dept → list of employees
        Map<String, List<Employee>> byDept = employees.stream()
            .collect(Collectors.groupingBy(Employee::dept));

        // groupingBy + downstream collector
        Map<String, Double> avgByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::dept,
                Collectors.averagingDouble(Employee::salary)
            ));

        // groupingBy + counting
        Map<String, Long> countByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::dept,
                Collectors.counting()
            ));

        // partitioningBy — splits into true / false buckets
        Map<Boolean, List<Employee>> partition = employees.stream()
            .collect(Collectors.partitioningBy(e -> e.salary() >= 100_000));

        // toMap — unique keys required or merge function needed
        Map<String, Double> salaryMap = employees.stream()
            .collect(Collectors.toMap(
                Employee::name,
                Employee::salary,
                (a, b) -> a                               // merge fn for duplicate keys
            ));

        // ── 10. PEEK (debug/side-effects only, still lazy) ───────────────
        List<Employee> debugged = employees.stream()
            .filter(e -> e.salary() > 90_000)
            .peek(e -> System.out.println("After filter: " + e.name()))
            .map(e -> new Employee(e.name(), e.dept(), e.salary() * 1.1, e.age()))
            .peek(e -> System.out.println("After raise: " + e.salary()))
            .toList();

        // ── 11. COUNT & MIN/MAX ──────────────────────────────────────────
        long count   = employees.stream().filter(e -> e.age() < 35).count();
        Optional<Employee> youngest = employees.stream()
            .min(Comparator.comparingInt(Employee::age));
        Optional<Employee> oldest   = employees.stream()
            .max(Comparator.comparingInt(Employee::age));

        // ── 12. forEach & forEachOrdered ────────────────────────────────
        employees.stream()
            .filter(e -> e.dept().equals("Eng"))
            .forEach(e -> System.out.println(e.name()));   // terminal; no return

        // ── 13. PARALLEL STREAMS ────────────────────────────────────────
        double parallelSum = employees.parallelStream()
            .mapToDouble(Employee::salary)
            .sum();                                       // thread-safe reduction

        // ── 14. TEEING (Java 12+) ────────────────────────────────────────
        record SalaryReport(double total, long count) {}
        SalaryReport report = employees.stream()
            .collect(Collectors.teeing(
                Collectors.summingDouble(Employee::salary),
                Collectors.counting(),
                SalaryReport::new
            ));

        // ── 15. CUSTOM COLLECTOR (advanced) ─────────────────────────────
        Collector<Employee, ?, Map<String, List<String>>> deptNamesCollector =
            Collectors.groupingBy(
                Employee::dept,
                Collectors.mapping(Employee::name, Collectors.toList())
            );

        Map<String, List<String>> deptNames = employees.stream()
            .collect(deptNamesCollector);

        // ── OUTPUT ────────────────────────────────────────────────────────
        System.out.println("Avg salary:   " + avgSalary);
        System.out.println("Name list:    " + nameList);
        System.out.println("Avg by dept:  " + avgByDept);
        System.out.println("Partition:    " + partition.get(true).size() + " high earners");
        System.out.println("Teeing rpt:   " + report);
        System.out.println("Dept names:   " + deptNames);
    }
}

**Quick-reference — all operations**
filter(Predicate) intermediate
Keep elements matching a condition
map(Function) intermediate
Transform each element to another type
flatMap(Function) intermediate
Map then flatten nested streams
mapToInt / Double / Long intermediate
Convert to primitive stream (no boxing)
sorted(Comparator) intermediate
Sort elements; natural or custom order
distinct() intermediate
Remove duplicates via equals()
limit(n) short-circuit
Keep at most n elements
skip(n) intermediate
Discard first n elements
peek(Consumer) intermediate
Inspect elements without consuming
collect(Collector) terminal
Accumulate into List, Map, Set, String…
reduce(identity, BinaryOp) terminal
Fold elements into a single value
forEach(Consumer) terminal
Consume each element; returns void
count() terminal
Number of elements in the stream
min / max(Comparator) terminal
Smallest or largest element as Optional
findFirst / findAny() short-circuit
First or any matching element as Optional
anyMatch / allMatch / noneMatch short-circuit
Boolean predicate tests; stop early
summaryStatistics() terminal
min, max, sum, avg, count in one pass
parallelStream() advanced
Fork-join parallel execution
Collectors.teeing() advanced
Two downstream collectors merged (Java 12+)
Collectors.groupingBy() terminal
Group by key into Map<K, List<V>>
Collectors.partitioningBy() terminal
Split into Map<Boolean, List> buckets
Collectors.joining() terminal
Concatenate strings with delimiter/prefix/suffix
Collectors.toMap() terminal
Build Map; supply merge fn for dup keys
Collectors.mapping() terminal
Transform before collecting (downstream)

