package my.project.service;

import my.project.entity.Department;
import my.project.entity.Employee;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class DepartmentsDataStorage {

    private Map<String, Department> departments;

    public DepartmentsDataStorage()
    {
        departments = new HashMap<>();
    }

    public Map<String, Department> getDepartments() {
        return departments;
    }

    public void loadFromFile(String path)
    {
        FileParser parser = new FileParser(this);
        parser.openFile(path);
    }

    public void printEmployees()
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

    public void printDepartmentsAvgSalary()
    {
        for (String department : departments.keySet())
        {
            printDepartmentAvgSalary(department);
        }
    }

    public void printDepartmentAvgSalary(String depName)
    {
        Department department = departments.get(depName.toUpperCase());

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

    public void savePersonTransfersByAvgSalary(String path)
    {
        if (departments.size() < 2) return;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path)))
        {
            for (Department oldDep : departments.values())
            {
                for (Employee emp : oldDep.getEmployees())
                {
                    BigDecimal empSalary = emp.getSalary();
                    if (empSalary.compareTo(oldDep.getAvgSalary()) >= 0) continue;

                    for (Department newDep : departments.values())
                    {
                        if (newDep != oldDep && empSalary.compareTo(newDep.getAvgSalary()) > 0)
                            writer.write(makeEntry(oldDep, newDep, Arrays.asList(emp), emp.getSalary()));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error has occurred during writing a file " + path + ' ' + e.getMessage());
        }
    }

    public void saveGroupTransfersByAvgSalary(String path)
    {
        if (departments.size() < 2) return;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path)))
        {
            for (Department oldDep : departments.values())
                writer.write(makeGroupsTransfersEntry(oldDep, 0, new ArrayList<>()).toString());

        } catch (IOException e) {
            System.out.println("Error has occurred during writing a file " + path + ' ' + e.getMessage());
        }
    }

    private StringBuilder makeGroupsTransfersEntry(Department oldDep, int index, List<Employee> group)
    {
        StringBuilder entry;

        if (index == oldDep.getEmployees().size())
        {
            entry = new StringBuilder();
            if (!group.isEmpty() && !(group.size() == oldDep.getEmployees().size()))
            {
                BigDecimal groupTotalSalary = BigDecimal.ZERO;
                for (Employee emp : group)
                    groupTotalSalary = groupTotalSalary.add(emp.getSalary());

                BigDecimal groupAvgSalary = groupTotalSalary.divide(new BigDecimal(group.size()), 2, RoundingMode.HALF_UP);

                if (groupAvgSalary.compareTo(oldDep.getAvgSalary()) < 0)
                {
                    for (Department newDep : departments.values())
                    {
                        if (oldDep != newDep && groupAvgSalary.compareTo(newDep.getAvgSalary()) > 0)
                            entry.append(makeEntry(oldDep, newDep, group, groupTotalSalary));
                    }
                }
            }

            return entry;
        }

        entry = makeGroupsTransfersEntry(oldDep, index + 1, group);

        Employee emp = oldDep.getEmployees().get(index);
        group.add(emp);
        entry.append(makeGroupsTransfersEntry(oldDep, index + 1, group));
        group.remove(emp);
        return entry;
    }

    private String makeEntry(Department oldDep, Department newDep, List<Employee> group,
                                  BigDecimal groupTotalSalary)
    {
        BigDecimal oldDepNewSalary = oldDep.getTotalSalary().subtract(groupTotalSalary)
                .divide(new BigDecimal(oldDep.getEmployees().size() - group.size()), 2, RoundingMode.HALF_UP);

        BigDecimal newDepNewSalary = newDep.getTotalSalary().add(groupTotalSalary)
                .divide(new BigDecimal(newDep.getEmployees().size() + group.size()), 2, RoundingMode.HALF_UP);

        StringBuilder employeesNames = new StringBuilder();
        for (int i = 0; i < group.size(); i++)
        {
            employeesNames.append(group.get(i).getName());
            if (i < group.size() - 1) employeesNames.append(", ");
        }

        String entry = "Transfer employees: " + employeesNames + " from " + oldDep.getName() + " to " + newDep.getName() +
                " will cause next changes:" + System.lineSeparator() +
                "old salary:" + System.lineSeparator() +
                oldDep.getName() + ": " + oldDep.getAvgSalary() + "\t" + newDep.getName() + ": " + newDep.getAvgSalary() + System.lineSeparator() +
                "new salary:" + System.lineSeparator() +
                oldDep.getName() + ": " + oldDepNewSalary + "\t" + newDep.getName() + ": " + newDepNewSalary +
                System.lineSeparator() + System.lineSeparator();

        return entry;
    }

    public boolean isEmpty()
    {
        return departments.isEmpty();
    }
}
