package my.project.service;

import java.util.Scanner;

public class Menu implements AutoCloseable {

    private DepartmentsDataStorage dataBase;
    private Scanner scanner;

    //Menu items
    private final int EXIT = 0;
    private final int BACK = 0;
    private final int OPEN_FILE = 1;
    private final int OPEN_DATA_MENU = 2;
    private final int OPEN_DEPARTMENT_MENU = 1;
    private final int LIST_EMPOLYEES = 2;
    private final int SAVE_PERSON_TRANSFERS = 3;
    private final int SAVE_GROUP_TRANSFERS = 4;

    private final int PERSON = 0;
    private final int GROUP = 1;

    public Menu()
    {
        dataBase = new DepartmentsDataStorage();
        scanner = new Scanner(System.in);

        System.out.println("Main menu:");
    }


    /*
     * Process Menu Methods
     */
    public void processMainMenu()
    {
        boolean exit = false;

        while(!exit)
        {
            int code = mainPage();

            switch(code)
            {
                    case EXIT:
                        exit = true;
                        break;

                    case OPEN_FILE:
                        openFile();
                        break;

                    case OPEN_DATA_MENU:
                        processEmployeesMenu();
                        break;
            }
        }
    }

    private void processEmployeesMenu()
    {
        boolean back = false;

        while (!back)
        {
            int code = employeesPage();

            switch(code)
            {
                case BACK:
                    back = true;
                    break;

                case OPEN_DEPARTMENT_MENU:
                    processDepSalMenu();
                    break;

                case LIST_EMPOLYEES:
                    dataBase.printEmployees();
                    break;

                case SAVE_PERSON_TRANSFERS:
                    saveTransfers(PERSON);
                    break;

                case SAVE_GROUP_TRANSFERS:
                    saveTransfers(GROUP);
                    break;
            }
        }
    }

    private void processDepSalMenu()
    {
        boolean back = false;

        while (!back)
        {
            int code = depSalPage();

            switch(code)
            {
                case BACK:
                    back = true;
                    break;

                //Departments list
                default:
                    String deps[] = dataBase.getAllDepartments();
                    dataBase.printDepartmentAvgSalary(deps[code - 1]);
                    break;
            }
        }
    }

    /*
     * Page content methods
     */

    private int mainPage()
    {
        boolean fileLoaded = !dataBase.isEmpty();

        System.out.println("0: Exit");
        System.out.println("1: Load file");
        if (fileLoaded) System.out.println("2: Employees info");

        int min = 0;
        int max = fileLoaded ? 2 : 1;

        return getReturnCode(min, max);
    }

    private int employeesPage()
    {
        System.out.println("0: Back");
        System.out.println("1: Average department salary");
        System.out.println("2: List employees");
        System.out.println("3: Save possible transfers for one person with increased average salary to file");
        System.out.println("4: Save possible transfers for group with increased average salary to file");

        int min = 0;
        int max = 4;

        return getReturnCode(min, max);
    }

    private int depSalPage()
    {
        String deps[] = dataBase.getAllDepartments();
        int min = 0;
        int max = 0;

        System.out.println("0: Back");

        for (String dep : deps)
        {
            max++;
            System.out.println(max + ": " + dep);
        }

        int retCode = getReturnCode(min, max);

        return retCode;
    }

    /*
     * Service methods
     */

    private void openFile()
    {
        System.out.println("Enter path to a file:");

        if (scanner.hasNextLine())
        {
            String path = scanner.nextLine();
            dataBase.loadFromFile(path);
        }
    }

    private void saveTransfers(int type)
    {
        System.out.println("Enter path to a file:");

        if (scanner.hasNextLine())
        {
            String path = scanner.nextLine();

            if (type == PERSON)
                dataBase.savePersonTransfersByAvgSalary(path);
            else if (type == GROUP)
                dataBase.saveGroupTransfersByAvgSalary(path);
        }
    }

    private int getReturnCode(int min, int max)
    {
        int code = -1;

        boolean validCode = false;
        while (!validCode)
        {
            if (scanner.hasNextInt())
                code = scanner.nextInt();

            scanner.nextLine(); //clears buffer

            if (code >= min && code <= max)
            {
                validCode = true;
            }
            else
            {
                System.out.println("Enter valid code:");
            }
        }

        return code;
    }

    public void close()
    {
        scanner.close();
    }
}
