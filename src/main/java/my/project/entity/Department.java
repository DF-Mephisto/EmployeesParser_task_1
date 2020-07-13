package my.project.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Department {

    private String name;
    private List<Employee> employees;

    public Department(String name)
    {
        this.name = name;
        employees = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void addEmployee(Employee employee)
    {
        employees.add(employee);
    }

    public BigDecimal getAvgSalary()
    {
        BigDecimal avgSalary = BigDecimal.ZERO;
        BigDecimal totalSalary = getTotalSalary();

        int depSize = employees.size();
        if (depSize > 0)
            avgSalary = totalSalary.divide(new BigDecimal(depSize), 2, RoundingMode.HALF_UP);

        return avgSalary;
    }

    public BigDecimal getTotalSalary()
    {
        BigDecimal totalSalary = BigDecimal.ZERO;

        for (Employee employee : employees)
            totalSalary = totalSalary.add(employee.getSalary());

        return totalSalary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {

        StringBuilder desc = new StringBuilder(name + ":\n");
        for (Employee employee : employees)
        {
            desc.append(employee.toString());
            desc.append('\n');
        }

        return desc.toString();
    }
}
