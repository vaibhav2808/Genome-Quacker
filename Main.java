import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("python", "main.py");
        Process p=pb.start();
        Scanner inp=new Scanner(new BufferedReader(new InputStreamReader(p.getInputStream())));

    }
}
