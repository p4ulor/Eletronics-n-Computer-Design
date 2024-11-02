import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class FileAccess {

    /**
     * Writes in the file FILENAME the information passed in info
     *
     * @param FILENAME Cointains the name of the file
     * @param info     information that will be saved in the file
     */
    public static void writeFile(String FILENAME, String info) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {
            bw.write(info);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Reads the file FILENAME
     *
     * @param FILENAME Cointains the name of the file that will be read
     * @return the information inside the file FILENAME
     */
    public static String readFile(String FILENAME) {
        try (Scanner in = new Scanner(new FileReader(FILENAME))) {
            StringBuilder s = new StringBuilder();
            while (in.hasNext())
                s.append(in.next());
            return s.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
