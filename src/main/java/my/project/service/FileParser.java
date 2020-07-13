package my.project.service;

import my.project.entity.Department;
import my.project.entity.Employee;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.*;

public class FileParser {

    private HashMap<String, Department> departments;

    public FileParser()
    {
        departments = new HashMap<>();
    }

    public HashMap<String, Department> getDepartments() {
        return departments;
    }

    public boolean openFile(String path)
    {
        departments.clear();

        Scanner scanner = null;

        try {
            Optional<String> extension = Optional.ofNullable(path)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(f.lastIndexOf(".") + 1));

            if (extension.isEmpty() || !extension.get().equals("emp"))
                throw new FileNotFoundException("Invalid file extension, must be \"emp\"");

            scanner = new Scanner(new FileReader(path));

            int line = 1;
            while (scanner.hasNextLine()) {
                processLine(scanner.nextLine(), line);
                line++;
            }

        } catch (FileNotFoundException e) {
            System.out.println("Can't open a file " + path + ":\n" + e.getMessage());
            return false;
        } finally {
            if (scanner != null) scanner.close();
        }

        return true;
    }

    private void processLine(String line, int lineNumber)
    {
        String name;
        Department department;
        BigDecimal salary;
        String data[];
        String depName;

        try{
            if ((data = line.split("\\s+\\|\\s+")).length != 3)
                throw new IllegalArgumentException("Line doesn't match required format: Name | Department | Salary");

            //Get name
            name = data[0];

            //Get salary
            salary = new BigDecimal(data[2]);
            if (salary.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException("Salary can't be negative");
            if (salary.scale() > 2) throw new NumberFormatException("Salary scale can't be more than 2");

            //Get department
            depName = data[1];
            department = departments.get(depName);
            if (department == null)
            {
                department = new Department(depName);
                departments.put(depName, department);
            }

        }
        catch(IllegalArgumentException e)
        {
            System.out.println("Line parse error has occurred in line " + lineNumber + ":\n" + e.getMessage());
            return;
        }

        Employee employee = new Employee(name, department, salary);
        department.addEmployee(employee);
    }
}
