import Service.Menu;

public class Main {

    public static void main(String... args)
    {
        try(Menu menu = new Menu())
        {
            menu.processMainMenu();
        }
    }

}
