import java.sql.*;
import java.util.Scanner;

public class Main
{
    public static void main(String args[])
    {
        Connection con = null;
        Scanner scanner = new Scanner(System.in);

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://192.168.56.101:4567/Roguelike",
                    "gmkim", "dovmf3203");

            while (true)
            {
                System.out.println("2D RoguleLIke Game Databases");
                System.out.println("현재 플레이어 : ");
                System.out.println("메뉴를 선택하세요:");
                System.out.println("1. 플레이어 접속");
                System.out.println("2. 플레이어 정보 확인");
                System.out.println("3. 플레이어 인벤토리 확인");
                System.out.println("4. 인벤토리 아이템 검색");
                System.out.println("99. 신규 플레이어 생성");
                System.out.println("0. 로그아웃");
                System.out.print("선택: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1)
                {
                    playerConnection(con, scanner);
                    break;
                }

                else if (choice == 2)
                {
                    checkPlayerInformation(con, scanner);
                    break;
                }

                else if (choice == 3)
                {
                    checkPlayerInventory(con, scanner);
                    break;
                }

                else if (choice == 4)
                {
                    searchInventoryItems(con, scanner);
                    break;
                }

                else if (choice == 99)
                {
                    createNewPlayer(con, scanner);
                    break;
                }


                else
                {

                }
            }
        }

        catch (Exception e)
        {
            System.out.println(e);
        }

        finally
        {
            try
            {
                if (con != null)
                {
                    con.close();
                }
            }

            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void playerConnection(Connection con, Scanner scanner)
    {
        try
        {
            System.out.print("Enter player ID: ");
            int playerId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            if (isValidPlayer(con, playerId, password))
            {
                System.out.println("Player connection successful!");
                // You can perform additional actions here for the connected player
            }

            else
            {
                System.out.println("Invalid player ID or password. Connection failed.");
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isValidPlayer(Connection con, int playerId, String password)
    {
        try
        {
            String query = "SELECT COUNT(*) FROM Player WHERE player_id = ? AND password = ?";
            try (PreparedStatement pstmt = con.prepareStatement(query))
            {
                pstmt.setInt(1, playerId);
                pstmt.setString(2, password);

                try (ResultSet rs = pstmt.executeQuery())
                {
                    return rs.next() && rs.getInt(1) > 0;
                }
            }
        }

        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static void checkPlayerInformation(Connection con, Scanner scanner)
    {
        try
        {
            System.out.print("Enter player ID: ");
            int playerId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            // Check if the player ID is valid
            if (isValidPlayerId(con, playerId))
            {
                // Retrieve player information from the database
                String query = "SELECT * FROM Player WHERE player_id = ?";
                try (PreparedStatement pstmt = con.prepareStatement(query))
                {
                    pstmt.setInt(1, playerId);

                    try (ResultSet rs = pstmt.executeQuery())
                    {
                        // Display player information
                        if (rs.next())
                        {
                            System.out.println("Player Information:");
                            System.out.println("Player ID: " + rs.getInt("player_id"));
                            System.out.println("Name: " + rs.getString("name"));
                            System.out.println("HP: " + rs.getInt("hp"));
                            System.out.println("Level: " + rs.getInt("level"));
                            System.out.println("Attack Power: " + rs.getInt("attack_power"));
                            System.out.println("Defense Power: " + rs.getInt("defense_power"));
                        }
                    }
                }
            }
            else
            {
                System.out.println("Invalid player ID.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isValidPlayerId(Connection con, int playerId)
    {
        try
        {
            // Check if the player ID exists in the database
            String query = "SELECT COUNT(*) FROM Player WHERE player_id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(query))
            {
                pstmt.setInt(1, playerId);

                try (ResultSet rs = pstmt.executeQuery())
                {
                    return rs.next() && rs.getInt(1) > 0;
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static void checkPlayerInventory(Connection con, Scanner scanner) {
        try {
            System.out.print("Enter player ID: ");
            int playerId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            // Check if the player ID is valid
            if (isValidPlayerId(con, playerId)) {
                // Retrieve player inventory from the database
                String query = "SELECT * FROM Inventory WHERE player_id = ?";
                try (PreparedStatement pstmt = con.prepareStatement(query)) {
                    pstmt.setInt(1, playerId);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        // Display player inventory
                        if (rs.next()) {
                            System.out.println("Player Inventory:");
                            System.out.println("Player ID: " + rs.getInt("player_id"));
                            System.out.println("Items: " + rs.getString("items"));
                            System.out.println("Gold: " + rs.getInt("gold"));
                        } else {
                            System.out.println("Player has no inventory.");
                        }
                    }
                }
            } else {
                System.out.println("Invalid player ID.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void searchInventoryItems(Connection con, Scanner scanner) {
        try {
            System.out.print("Enter player ID: ");
            int playerId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            // Check if the player ID is valid
            if (isValidPlayerId(con, playerId)) {
                System.out.print("Enter item name to search: ");
                String itemName = scanner.nextLine();

                // Search for items in the player's inventory
                String query = "SELECT * FROM Inventory WHERE player_id = ? AND items LIKE ?";
                try (PreparedStatement pstmt = con.prepareStatement(query)) {
                    pstmt.setInt(1, playerId);
                    pstmt.setString(2, "%" + itemName + "%");

                    try (ResultSet rs = pstmt.executeQuery()) {
                        // Display search results
                        System.out.println("Search Results for Item: " + itemName);
                        while (rs.next()) {
                            System.out.println("Player ID: " + rs.getInt("player_id"));
                            System.out.println("Items: " + rs.getString("items"));
                            System.out.println("Gold: " + rs.getInt("gold"));
                            System.out.println("-----------------------");
                        }
                    }
                }
            } else {
                System.out.println("Invalid player ID.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createNewPlayer(Connection con, Scanner scanner) {
        try {
            System.out.print("Enter player ID: ");
            int playerId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            // Check if the player ID is already in use
            if (isPlayerIdAvailable(con, playerId)) {
                System.out.print("Enter player name: ");
                String playerName = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                // Insert the new player into the Player table
                String insertQuery = "INSERT INTO Player (player_id, name, password) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = con.prepareStatement(insertQuery)) {
                    pstmt.setInt(1, playerId);
                    pstmt.setString(2, playerName);
                    pstmt.setString(3, password);

                    int rowsAffected = pstmt.executeUpdate();
                    System.out.println(rowsAffected + " row(s) inserted. New player created successfully.");
                }
            } else {
                System.out.println("Player ID already in use. Choose a different ID.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isPlayerIdAvailable(Connection con, int playerId) {
        try {
            String query = "SELECT COUNT(*) FROM Player WHERE player_id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setInt(1, playerId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next() && rs.getInt(1) == 0; // ID is available if count is 0
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}