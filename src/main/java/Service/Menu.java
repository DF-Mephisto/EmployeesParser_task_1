package Service;

import java.io.File;
import java.util.Optional;
import java.util.Scanner;

public class Menu implements AutoCloseable {

    private EmployeeParser parser;
    private Scanner scanner;

    public Menu()
    {
        parser = new EmployeeParser();
        scanner = new Scanner(System.in);
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
                    case 0:
                        exit = true;
                        break;

                    case 1:
                        openFile();
                        break;

                    case 2:
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
                case 0:
                    back = true;
                    break;

                case 1:
                    processDepSalMenu();
                    break;

                case 2:
                    parser.listEmployees();
                    break;
            }
        }
    }

    private void processDepSalMenu()
    {
        boolean back = false;

        while (!back)
        {
            StringBuilder depName = new StringBuilder();
            int code = depSalPage(depName);

            switch(code)
            {
                case 0:
                    back = true;
                    break;

                default:
                    parser.showDepartmentAvgSalary(depName.toString());
                    break;
            }
        }
    }

    /*
     * Page content methods
     */

    private int mainPage()
    {
        boolean fileLoaded = parser.loaded();

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

        int min = 0;
        int max = 2;

        return getReturnCode(min, max);
    }

    private int depSalPage(StringBuilder depName)
    {
        String deps[] = parser.getDepartments();
        int min = 0;
        int max = 0;

        System.out.println("0: Back");

        for (String dep : deps)
        {
            max++;
            System.out.println(max + ": " + dep);
        }

        int retCode = getReturnCode(min, max);
        if (retCode > 0) depName.append(deps[retCode - 1]);

        return retCode;
    }

    /*
     * Service methods
     */

    private void openFile()
    {
        System.out.println("Enter path to a file:");
        scanner.nextLine(); //clears buffer

        File file;
        String path = "";
        boolean validFilePath = false;

        while (!validFilePath)
        {
            if (scanner.hasNextLine())
                path = scanner.nextLine();

            Optional<String> extension = Optional.ofNullable(path)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(f.lastIndexOf(".") + 1));

            if (extension.isEmpty() || !extension.get().equals("emp"))
            {
                System.out.println("Invalid file extension, must be \"emp\"");
                continue;
            }

            file = new File(path);

            if (file.exists())
            {
                validFilePath = true;
                parser.setFile(file);
            }
            else
            {
                System.out.println("Can't open a file");
            }
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
            else scanner.nextLine();

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
