package my.project.service;

import my.project.entity.Department;
import my.project.entity.Employee;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DepartmentsDataStorage {

    private HashMap<String, Department> departments;

    public DepartmentsDataStorage()
    {
        departments = new HashMap<>();
    }

    public HashMap<String, Department> getDepartments() {
        return departments;
    }

    public void loadFromFile(String path)
    {
        FileParser parser = new FileParser();

        if (parser.openFile(path))
        {
            departments = parser.getDepartments();
        }
    }

    public void listEmployees()
    {
        for (Department dep : departments.values())
        {
            System.out.println(dep.toString());
        }
    }

    public String[] getAllDepartments()
    {
        String deps[] = new String[departments.size()];
        return departments.keySet().toArray(deps);
    }

    public void showDepartmentsAvgSalary()
    {
        for (String department : departments.keySet())
        {
            showDepartmentAvgSalary(department);
        }
    }

    public void showDepartmentAvgSalary(String dep)
    {
        Department department = departments.get(dep);

        try{
            if (department == null)
                throw new IllegalArgumentException("No such department");
        } catch(IllegalArgumentException e)
        {
            System.out.println("Error has occurred during department average salary calculation:\n" + e.getMessage());
            return;
        }

        System.out.println("Average salary in department " + department.getName() + ": " + department.getAvgSalary());
    }

    public void saveTransferByAvgSalary(String path)
    {
        if (departments.size() < 2) return;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path)))
        {
            List<DepartmentSummary> deps = new ArrayList<>();
            for (Department dep : departments.values())
            {
                deps.add(new DepartmentSummary(dep.getName(), dep.getAvgSalary(), dep.getTotalSalary(),
                        dep.getEmployees().size(), dep.getEmployees()));
            }

            for (DepartmentSummary dep : deps)
            {
                for (Employee emp : dep.emps)
                {
                    BigDecimal empSalary = emp.getSalary();
                    if (empSalary.compareTo(dep.avgSalary) != -1) continue;

                    for (DepartmentSummary trDep : deps)
                    {
                        if (trDep != dep && empSalary.compareTo(trDep.avgSalary) == 1)
                            writer.write(makeEntry(dep, trDep, emp));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error has occurred during writing a file " + path + ' ' + e.getMessage());
        }
    }

    private String makeEntry(DepartmentSummary curDep, DepartmentSummary newDep, Employee emp)
    {
        BigDecimal oldDepNewSalary = curDep.totalSalary.subtract(emp.getSalary())
                .divide(new BigDecimal(curDep.size - 1), 2, RoundingMode.HALF_UP);

        BigDecimal newDepNewSalary = newDep.totalSalary.add(emp.getSalary())
                .divide(new BigDecimal(newDep.size + 1), 2, RoundingMode.HALF_UP);

        String entry = "Transfer employee " + emp.getName() + " from " + curDep.name + " to " + newDep.name +
                " will cause next changes:" + System.lineSeparator() +
                "old salary:" + System.lineSeparator() +
                curDep.name + ": " + curDep.avgSalary + "\t" + newDep.name + ": " + newDep.avgSalary + System.lineSeparator() +
                "new salary:" + System.lineSeparator() +
                curDep.name + ": " + oldDepNewSalary + "\t" + newDep.name + ": " + newDepNewSalary +
                System.lineSeparator() + System.lineSeparator();

        return entry;
    }

    public boolean isEmpty()
    {
        return departments.isEmpty();
    }

    private class DepartmentSummary
    {
        String name;
        BigDecimal avgSalary;
        BigDecimal totalSalary;
        int size;
        List<Employee> emps;

        DepartmentSummary(String name, BigDecimal avgSalary, BigDecimal totalSalary, int size, List<Employee> emps) {
            this.name = name;
            this.avgSalary = avgSalary;
            this.totalSalary = totalSalary;
            this.size = size;
            this.emps = emps;
        }
    }
}
