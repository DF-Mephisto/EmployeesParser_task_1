package my.project.service;

import my.project.entity.Department;
import my.project.entity.Employee;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.*;

public class FileParser {

    private DepartmentsDataStorage db;

    public FileParser(DepartmentsDataStorage db)
    {
        this.db = db;
    }

    public void openFile(String path)
    {
        db.getDepartments().clear();

        try
        {
            Optional<String> extension = Optional.ofNullable(path)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(f.lastIndexOf(".") + 1));

            if (extension.isEmpty() || !extension.get().equals("emp"))
                throw new FileNotFoundException("Invalid file extension, must be \"emp\"");

            try (Scanner scanner = new Scanner(new FileReader(path))) {
                int line = 1;
                while (scanner.hasNextLine()) {
                    processLine(scanner.nextLine(), line);
                    line++;
                }
            }
        } catch (FileNotFoundException e) {
                System.out.println("Can't open a file " + path + ":\n" + e.getMessage());
        }
    }

    private void processLine(String line, int lineNumber)
    {
        String name;
        Department department;
        BigDecimal salary;
        String data[];
        String depName;

        try{
            if ((data = line.split("\\|", -1)).length != 3)
                throw new IllegalArgumentException("Line doesn't match required format: Name | Department | Salary");

            //Get name
            name = data[0].trim();
            if (name.isBlank()) throw new IllegalArgumentException("Name mustn't be blank");

            //Get salary
            salary = new BigDecimal(data[2].trim());
            if (salary.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException("Salary can't be negative or zero");
            if (salary.scale() > 2) throw new NumberFormatException("Salary scale can't be more than 2");

            //Get department
            depName = data[1].trim();
            if (depName.isBlank()) throw new IllegalArgumentException("Department name mustn't be blank");
            department = db.getDepartments().get(depName.toUpperCase());
            if (department == null)
            {
                department = new Department(depName);
                db.getDepartments().put(depName.toUpperCase(), department);
            }

        }
        catch(IllegalArgumentException e)
        {
            String msg = e.getMessage();
            if (msg == null)
                msg = e.getClass().getName();

            System.out.println("Line parse error has occurred in line " + lineNumber + ":\n" + msg);
            return;
        }

        Employee employee = new Employee(name, department, salary);
        department.addEmployee(employee);
    }
}
