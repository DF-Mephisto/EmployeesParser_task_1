package Service;

import Entity.Employee;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class EmployeeParser {

    private HashMap<String, List<Employee>> employees;

    public EmployeeParser()
    {
        employees = new HashMap<>();
    }

    public void setFile(File file)
    {
        employees.clear();

        String name;
        String dep;
        Double salary;

        try (Scanner scanner = new Scanner(new FileReader(file))){

            String data[];
            while (scanner.hasNextLine() && (data = scanner.nextLine().split("\\s+\\|\\s+")).length == 3)
            {
                name = data[0];
                dep = data[1];

                try {
                    salary = Double.valueOf(data[2]);
                }
                catch(NumberFormatException e)
                {
                    System.out.println("Salary parse error");
                    break;
                }

                Employee employee = new Employee(name, dep, salary);

                List<Employee> depList = employees.get(dep);
                if (depList == null)
                {
                    depList = new ArrayList<>();
                    employees.put(dep, depList);
                }
                depList.add(employee);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Can't open a file");
        }
    }

    public void listEmployees()
    {
        Set<Map.Entry<String, List<Employee>>> deps = employees.entrySet();

        for (Map.Entry<String, List<Employee>> dep : deps)
        {
            System.out.println(dep.getKey() + ":");

            for (Employee emp : dep.getValue())
            {
                System.out.println(emp);
            }
        }
    }

    public void showDepartmentAvgSalary(String dep)
    {
        System.out.println("Average salary in department " + dep + ": " + getDepartmentAvgSalary(dep));
    }

    public Double getDepartmentAvgSalary(String dep)
    {
        List<Employee> depList = employees.get(dep);
        Double avgSalary = 0.0;

        if (depList != null)
        {
            Double totalSalary = 0.0;

            for (Employee employee : depList)
            {
                totalSalary += employee.getSalary();
            }

            if (depList.size() > 0) avgSalary = totalSalary / depList.size();
        }

        return avgSalary;
    }

    public String[] getDepartments()
    {
        String deps[] = new String[employees.size()];
        return employees.keySet().toArray(deps);
    }

    public boolean loaded()
    {
        if (employees.isEmpty()) return false;
        else return true;
    }

}
