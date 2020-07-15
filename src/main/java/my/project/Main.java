package my.project;

import my.project.service.DepartmentsDataStorage;
import my.project.service.Menu;

public class Main {

    public static void main(String... args)
    {
        if (args.length != 2)
            System.out.println("Run parameters should be: [inputFileName.emp, outputFileName]");
        else
            run(args[0], args[1]);

        try(Menu menu = new Menu())
        {
            menu.processMainMenu();
        }
    }

    private static void run(String input, String output)
    {
        DepartmentsDataStorage dataBase = new DepartmentsDataStorage();
        dataBase.loadFromFile(input);

        if (!dataBase.isEmpty())
        {
            dataBase.printEmployees();
            dataBase.printDepartmentsAvgSalary();
            dataBase.saveGroupTransfersByAvgSalary(output);
        }
    }
}
