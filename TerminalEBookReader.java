import java.io.*;
import java.util.*;

public class TerminalEBookReader {
    private static final String BOOK_DIR = System.getProperty("user.home") + File.separator + "book";  // 文件夹路径
    private static final int BOOKS_PER_PAGE = 9; // 列表行数
    private static final int CHARS_PER_PAGE = 170; // 每页的字数

    public static void main(String[] args) throws IOException {
        File folder = new File(BOOK_DIR);
        File[] bookFiles = folder.listFiles((dir, name) -> name.endsWith(".txt"));

        if (bookFiles == null || bookFiles.length == 0) {
            String userhome = System.getProperty("user.home");
            System.out.println("在'" + userhome + File.separator + "book'目录中找不到txt或目录不存在");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        int currentBookIndex = 0;

        while (true) {
            clearScreen();
            // 书籍列表
            for (int i = 0; i < BOOKS_PER_PAGE && i + currentBookIndex < bookFiles.length; i++) {
                System.out.printf("%d->%s\n", i + 1, bookFiles[i + currentBookIndex].getName());
            }

            System.out.println("\nj_下一页 k_上一页 1-9_选择 q_退出");
            System.out.print(">");

            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("j")) {
                if (currentBookIndex + BOOKS_PER_PAGE < bookFiles.length) {
                    currentBookIndex += BOOKS_PER_PAGE;
                }
            } else if (input.equalsIgnoreCase("k")) {
                if (currentBookIndex - BOOKS_PER_PAGE >= 0) {
                    currentBookIndex -= BOOKS_PER_PAGE;
                }
            } else if (input.equalsIgnoreCase("q")) {
                System.out.println("正在结束进程并保存——"); // 真的有在保存嘛？
                break;  // 程序退出
            } else {
                try {
                    int selectedBook = Integer.parseInt(input) - 1 + currentBookIndex;
                    if (selectedBook >= 0 && selectedBook < bookFiles.length) {
                        readBook(bookFiles[selectedBook]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("无效变量。");
                }
            }
        }
    }

    private static void readBook(File bookFile) throws IOException {
        List<String> pages = paginateBook(bookFile);

        File dataFile = new File(bookFile.getPath() + ".data");
        int currentPageIndex = 0;
        if (dataFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
                currentPageIndex = Integer.parseInt(reader.readLine());
            } catch (NumberFormatException e) {
                currentPageIndex = 0;
            }
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            clearScreen();
            System.out.println("\n---  " + (currentPageIndex + 1) + "/" + pages.size() + " ---");
            System.out.println(pages.get(currentPageIndex));
            System.out.println("\nj_下一页 k_上一页 t_选择页 h_返回列表");
            System.out.print(">");

            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("j")) {
                if (currentPageIndex < pages.size() - 1) {
                    currentPageIndex++;
                }
            } else if (input.equalsIgnoreCase("k")) {
                if (currentPageIndex > 0) {
                    currentPageIndex--;
                }
            } else if (input.equalsIgnoreCase("t")) {
                System.out.print("输入页码>");
                try {
                    int pageIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    if (pageIndex >= 0 && pageIndex < pages.size()) {
                        currentPageIndex = pageIndex;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("无效变量。");
                }
            } else if (input.equalsIgnoreCase("h")) {
                saveCurrentPage(dataFile, currentPageIndex);
                break;
            }

            saveCurrentPage(dataFile, currentPageIndex);
        }
    }

    private static List<String> paginateBook(File bookFile) throws IOException {
        List<String> pages = new ArrayList<>();
        StringBuilder currentPage = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(bookFile))) {
            int character;
            int charCount = 0;

            while ((character = reader.read()) != -1) {
                currentPage.append((char) character);
                charCount++;

                if (charCount >= CHARS_PER_PAGE) {
                    pages.add(currentPage.toString());
                    currentPage.setLength(0);
                    charCount = 0;
                }
            }

            if (currentPage.length() > 0) {
                pages.add(currentPage.toString());
            }
        }

        return pages;
    }

    private static void saveCurrentPage(File dataFile, int currentPageIndex) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            writer.write(String.valueOf(currentPageIndex));
        } catch (IOException e) {
            System.out.println("Error>阅读进度保存出错——");
        }
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

