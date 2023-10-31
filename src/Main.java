import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String args[]) {
        Connection con = null;
        Scanner scanner = new Scanner(System.in);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://192.168.56.101:4567/madang",
                    "gmkim", "dovmf3203");

            while (true) {
                System.out.println("메뉴를 선택하세요:");
                System.out.println("1. 데이터 삽입");
                System.out.println("2. 데이터 삭제");
                System.out.println("3. 데이터 검색");
                System.out.println("4. 종료");
                System.out.print("선택: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    System.out.print("bookid 입력 : ");
                    int bookid = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("bookname 입력 : ");
                    String bookname = scanner.nextLine();
                    System.out.print("publisher 입력 : ");
                    String publisher = scanner.nextLine();
                    System.out.print("price 입력 : ");
                    double price = scanner.nextDouble();
                    insert(con, "Book", bookid, bookname, publisher, price);
                } else if (choice == 2) {
                    System.out.print("삭제할 데이터의 BookID를 입력하세요: ");
                    int bookid = scanner.nextInt();
                    delete(con, "Book", bookid);
                } else if (choice == 3) {
                    System.out.print("검색할 책 제목을 입력하세요: ");
                    String searchTitle = scanner.nextLine();
                    search(con, "Book", searchTitle);
                } else if (choice == 4) {
                    break;
                } else {
                    System.out.println("유효하지 않은 메뉴 선택입니다.");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void insert(Connection con, String tableName, int bookid, String bookname, String publisher, double price) {
        try {
            String query = "INSERT INTO " + tableName + " (bookid, bookname, publisher, price) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, bookid);
            pstmt.setString(2, bookname);
            pstmt.setString(3, publisher);
            pstmt.setDouble(4, price);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(Connection con, String tableName, int bookid) {
        try {
            String query = "DELETE FROM " + tableName + " WHERE bookid = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, bookid);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void search(Connection con, String tableName, String searchTitle) {
        try {
            String query = "SELECT bookid, publisher, price FROM " + tableName + " WHERE bookname = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, searchTitle);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Data in " + tableName + " for book title '" + searchTitle + "':");
                System.out.println("BookID: " + rs.getInt("bookid"));
                System.out.println("Publisher: " + rs.getString("publisher"));
                System.out.println("Price: " + rs.getDouble("price"));
            } else {
                System.out.println("해당 데이터가 없습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
