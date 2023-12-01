import java.sql.*;
import java.util.Scanner;

public class Main
{
    private static String loggedInPlayerId = null;
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
                System.out.println("----------------------------------------");
                System.out.println("2D RoguleLike Game Databases");
                System.out.println("현재 접속중인 플레이어 : " + (loggedInPlayerId != null ? loggedInPlayerId : "없음"));
                System.out.println("메뉴를 선택하세요:");
                System.out.println("0. 로그아웃");
                System.out.println("1. 플레이어 로그인");
                System.out.println("2. 플레이어 정보 확인");
                System.out.println("3. 플레이어 인벤토리 확인");
                System.out.println("4. 업적 목록 확인");
                System.out.println("5. 아이템 도감 확인");
                System.out.println("6. 몬스터 도감 확인");
                System.out.println("7. NPC 도감 확인");
                System.out.println("8. 아이템 구매 & 판매");
                System.out.println("9. 신규 플레이어 생성");
                System.out.println("10. 플레이어 삭제");
                System.out.println("99. 프로그램 종료");
                System.out.println("----------------------------------------");
                System.out.print("선택: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                if(loggedInPlayerId == null)
                {
                    if (choice >= 2 && choice <= 8)
                    {
                        System.out.println("권한이 없습니다. 로그인을 해주십시오.");
                        continue;
                    }
                }

                if (choice == 0)
                {
                    if (loggedInPlayerId != null)
                    {
                        System.out.println("로그아웃 성공 !");
                        loggedInPlayerId = null;
                    }

                    else
                    {
                        System.out.println("현재 접속중인 플레이어가 없습니다.");
                    }

                    continue;
                }

                else if (choice == 1)
                {
                    playerLogin(con, scanner);
                    continue;
                }

                else if (choice == 2)
                {
                    checkPlayerInformation(con);
                    continue;
                }

                else if (choice == 3)
                {
                    checkPlayerInventory(con, scanner);
                    continue;
                }

                else if (choice == 4)
                {
                    checkAchievementList(con);
                    continue;
                }

                else if (choice == 5)
                {
                    checkItemList(con, scanner);
                    continue;
                }

                else if (choice == 6)
                {
                    checkMonsterList(con, scanner);
                    continue;
                }

                else if (choice == 7)
                {
                    checkNPCList(con, scanner);
                    continue;
                }

                else if (choice == 8)
                {
                    itemBuySell(con, scanner);
                    continue;
                }

                else if (choice == 9)
                {
                    if (loggedInPlayerId != null)
                    {
                        System.out.println("현재 로그인 중입니다. 로그아웃 이후 다시 시도해주세요.");
                        continue;
                    }

                    createNewPlayer(con, scanner);
                    continue;
                }

                else if (choice == 10)
                {
                    if (loggedInPlayerId != null)
                    {
                        System.out.println("현재 로그인 중입니다. 로그아웃 이후 다시 시도해주세요.");
                        continue;
                    }

                    deletePlayer(con, scanner);
                    continue;
                }

                else if (choice == 99)
                {
                    System.out.println("이용해주셔서 감사합니다.");
                    break;
                }

                else
                {
                    System.out.println("잘못된 메뉴 번호입니다. 다시 입력해주세요.");
                    continue;
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

    //1번 메뉴
    private static void playerLogin(Connection con, Scanner scanner) throws SQLException
    {
        System.out.print("ID를 입력하세요 : ");
        String playerId = scanner.nextLine();

        System.out.print("비밀번호를 입력하세요 : ");
        String password = scanner.nextLine();

        if (isValidPlayer(con, playerId, password))
        {
            System.out.println("로그인 성공 !");
            loggedInPlayerId = playerId;
        }

        else
        {
            System.out.println("로그인 실패. 존재하지 않는 ID 이거나 잘못된 Password 입니다.");
        }
    }

    private static boolean isValidPlayer(Connection con, String playerId, String password) throws SQLException
    {
        String query = "SELECT COUNT(*) FROM Player WHERE player_id = ? AND password = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query))
        {
            pstmt.setString(1, playerId);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery())
            {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    //2번 메뉴
    private static void checkPlayerInformation(Connection con) throws SQLException
    {
        String query = "SELECT name, hp, level, attack_power, gold FROM Player WHERE player_id = ?";

        try (PreparedStatement PlayerInfo = con.prepareStatement(query))
        {
            PlayerInfo.setString(1, loggedInPlayerId);

            try (ResultSet rs = PlayerInfo.executeQuery())
            {
                if (rs.next())
                {
                    System.out.println("플레이어 정보");
                    System.out.println("이름 : " + rs.getString("name"));
                    System.out.println("HP : " + rs.getInt("hp"));
                    System.out.println("레벨 : " + rs.getInt("level"));
                    System.out.println("공격력 : " + rs.getInt("attack_power"));
                    System.out.println("소지한 골드 : " + rs.getInt("gold"));
                }

                else
                {
                    System.out.println("플레이어 정보를 찾지 못하였습니다.");
                }
            }
        }
    }

    //3번메뉴
    private static void checkPlayerInventory(Connection con, Scanner scanner) throws SQLException
    {
        String query = "SELECT items, quantity, price FROM Inventory WHERE player_id = ? ORDER BY price DESC";

        try (PreparedStatement inventoryInfo = con.prepareStatement(query))
        {
            inventoryInfo.setString(1, loggedInPlayerId);

            try (ResultSet rs = inventoryInfo.executeQuery())
            {
                System.out.println(loggedInPlayerId + " 의 인벤토리");

                while (rs.next())
                {
                    System.out.print("아이템: " + rs.getString("items"));
                    System.out.print(" | 수량: " + rs.getInt("quantity"));
                    System.out.println(" | 가격: " + rs.getInt("price"));
                }
            }
        }
    }

    //4번 메뉴
    private static void checkAchievementList(Connection con) throws SQLException
    {
        String query = "SELECT achievement_num, achievement_name, description, reward FROM Achievement ORDER BY achievement_num";

        try (PreparedStatement achievementInfo = con.prepareStatement(query);
             ResultSet rs = achievementInfo.executeQuery())
        {
            System.out.println("업적 목록");

            while (rs.next())
            {
                int achievementNum = rs.getInt("achievement_num");
                String achievementName = rs.getString("achievement_name");
                String description = rs.getString("description");
                String reward = rs.getString("reward");

                System.out.print(achievementNum + ". ");
                System.out.print(achievementName + " -->");
                System.out.print(" 달성 조건 : " + description);
                System.out.println(" | 보상 : " + reward);
            }
        }
    }

    //5번 메뉴
    private static void checkItemList(Connection con, Scanner scanner) throws SQLException
    {
        System.out.println("아이템 도감");
        System.out.println("1. 모든 아이템 도감 열기");
        System.out.println("2. 등급별 아이템 검색");
        System.out.print("원하는 메뉴를 입력하세요 : ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice)
        {
            case 1:
                printAllItems(con);
                break;

            case 2:
                printItemsByRank(con, scanner);
                break;

            default:
                System.out.println("잘못된 메뉴 입력입니다.");
                break;
        }
    }

    private static void printAllItems(Connection con) throws SQLException
    {
        String query = "SELECT * FROM Item ORDER BY FIELD(rank, '신화', '전설', '영웅', '고급', '일반'), LENGTH(name), name";

        try (PreparedStatement itemInfo = con.prepareStatement(query);
             ResultSet rs = itemInfo.executeQuery())
        {
            while (rs.next())
            {
                System.out.print("이름 : " + rs.getString("name"));
                System.out.print(" | 등급 : " + rs.getString("rank"));
                System.out.println(" | 설명 : " + rs.getString("description"));
            }
        }
    }

    private static void printItemsByRank(Connection con, Scanner scanner) throws SQLException
    {
        System.out.print("검색하려는 등급을 입력해주세요 (신화, 전설, 영웅, 고급, 일반) : ");
        String itemRank = scanner.nextLine();

        String query = "SELECT * FROM Item WHERE rank = ? ORDER BY LENGTH(name), name";

        try (PreparedStatement itemInfoByRank = con.prepareStatement(query))
        {
            itemInfoByRank.setString(1, itemRank);

            try (ResultSet rs = itemInfoByRank.executeQuery())
            {
                System.out.println(itemRank + " 등급의 아이템");
                while (rs.next())
                {
                    System.out.print("이름 : " + rs.getString("name"));
                    System.out.println(" | 설명 : " + rs.getString("description"));
                }
            }
        }
    }

    //6번 메뉴
    private static void checkMonsterList(Connection con, Scanner scanner) throws SQLException
    {
        System.out.println("몬스터 도감");
        System.out.println("1. 모든 몬스터 도감 열기");
        System.out.println("2. 몬스터 유형별 검색");
        System.out.print("원하는 메뉴를 입력하세요 : ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice)
        {
            case 1:
                printAllMonsters(con);
                break;

            case 2:
                printMonstersByType(con, scanner);
                break;

            default:
                System.out.println("잘못된 메뉴 입력입니다.");
                break;
        }
    }

    private static void printAllMonsters(Connection con) throws SQLException
    {
        String query = "SELECT * FROM Enemy ORDER BY FIELD(type, '근거리', '원거리', '보스 몬스터'), hp ASC";
        try (PreparedStatement monsterInfo = con.prepareStatement(query);
             ResultSet rs = monsterInfo.executeQuery())
        {
            while (rs.next())
            {
                System.out.print("이름 : " + rs.getString("name"));
                System.out.print(" | 체력 : " + rs.getInt("hp"));
                System.out.print(" | 공격력 : " + rs.getInt("attack_power"));
                System.out.println(" | 유형 : " + rs.getString("type"));
            }
        }
    }

    private static void printMonstersByType(Connection con, Scanner scanner) throws SQLException
    {
        System.out.print("검색하려는 유형을 입력해주세요 (근거리, 원거리, 보스 몬스터) : ");
        String monsterType = scanner.nextLine();

        String query = "SELECT * FROM Enemy WHERE type = ? ORDER BY hp ASC";

        try (PreparedStatement monsterInfoByType = con.prepareStatement(query))
        {
            monsterInfoByType.setString(1, monsterType);
            try (ResultSet rs = monsterInfoByType.executeQuery())
            {
                System.out.println(monsterType + " 유형 몬스터 :");
                while (rs.next())
                {
                    System.out.print("이름 : " + rs.getString("name"));
                    System.out.print(" | 체력 : " + rs.getInt("hp"));
                    System.out.print(" | 공격력 : " + rs.getInt("attack_power"));
                    System.out.println(" | 유형 : " + rs.getString("type"));
                }
            }
        }
    }

    //7번 메뉴
    private static void checkNPCList(Connection con, Scanner scanner) throws SQLException
    {
        System.out.println("NPC 도감");
        System.out.println("1. 모든 NPC 도감 열기");
        System.out.println("2. NPC 유형별 검색");
        System.out.print("원하는 메뉴를 입력하세요 : ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice)
        {
            case 1:
                printAllNPCs(con);
                break;

            case 2:
                printNPCsByType(con, scanner);
                break;

            default:
                System.out.println("잘못된 메뉴 입력입니다.");
                break;
        }
    }

    private static void printAllNPCs(Connection con) throws SQLException
    {
        String query = "SELECT * FROM NPC ORDER BY FIELD(type, '상인', '퀘스트', '대장장이'), LENGTH(name), name";

        try (PreparedStatement npcInfo = con.prepareStatement(query);
             ResultSet rs = npcInfo.executeQuery())
        {
            while (rs.next())
            {
                System.out.print("이름 : " + rs.getString("name"));
                System.out.println(" | 유형 : " + rs.getString("type"));
            }
        }
    }

    private static void printNPCsByType(Connection con, Scanner scanner) throws SQLException
    {
        System.out.print("검색하려는 유형을 입력해주세요 (상인, 퀘스트, 대장장이) : ");
        String npcType = scanner.nextLine();

        String query = "SELECT * FROM NPC WHERE type = ? ORDER BY LENGTH(name), name";

        try (PreparedStatement npcInfoByType = con.prepareStatement(query))
        {
            npcInfoByType.setString(1, npcType);
            try (ResultSet rs = npcInfoByType.executeQuery())
            {
                System.out.println("NPC 유형 " + npcType + "의 목록:");

                while (rs.next())
                {
                    System.out.print("이름 : " + rs.getString("name"));
                    System.out.println(" | 유형 : " + rs.getString("type"));
                }
            }
        }
    }

    //8번 메뉴
    private static void itemBuySell(Connection con, Scanner scanner) throws SQLException
    {
        System.out.println("1. 아이템 구매");
        System.out.println("2. 아이템 판매");
        System.out.print("원하는 메뉴를 선택하세요: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice)
        {
            case 1:
                buyItem(con, scanner);
                break;

            case 2:
                sellItem(con, scanner);
                break;

            default:
                System.out.println("잘못된 메뉴 번호입니다. 다시 입력해주세요.");
                break;
        }
    }

    private static void buyItem(Connection con, Scanner scanner) throws SQLException
    {
        String query = "SELECT * FROM Item WHERE rank IN ('고급', '일반') ORDER BY gold DESC";

        try (PreparedStatement itemInfo = con.prepareStatement(query);
             ResultSet rs = itemInfo.executeQuery()) {
            System.out.println("-------------");

            while (rs.next())
            {
                System.out.println(rs.getString("name") + " " + rs.getInt("gold") + "g");
            }
            System.out.println("-------------");
        }

        System.out.print("구매할 아이템의 이름을 입력하세요: ");
        String itemName = scanner.nextLine();

        System.out.print("구매할 수량을 입력하세요: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        int totalPrice = calculatePrice(con, itemName, quantity);

        if (checkGold(con, totalPrice))
        {
            int nowGold = UpdateBuy(con, totalPrice, loggedInPlayerId, itemName, quantity);
            System.out.println("아이템을 성공적으로 구매했습니다! 현재 골드 : " + nowGold + "g");
        }

        else
        {
            System.out.println("골드가 부족합니다. 구매에 실패했습니다.");
        }
    }

    private static void sellItem(Connection con, Scanner scanner) throws SQLException
    {
        checkPlayerInventory(con, scanner);

        System.out.print("판매할 아이템의 이름을 입력하세요: ");
        String itemName = scanner.nextLine();

        System.out.print("판매할 수량을 입력하세요: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        int totalPrice = calculatePrice(con, itemName, quantity);

        if (checkQuantity(con, loggedInPlayerId, itemName, quantity))
        {
            int nowGold = UpdateSell(con, totalPrice, loggedInPlayerId, itemName, quantity);
            System.out.println("아이템을 성공적으로 판매했습니다! 현재 골드 : " + nowGold + "g");
        }

        else
        {
            System.out.println("아이템 수량이 부족합니다. 판매에 실패했습니다.");
        }
    }

    private static int calculatePrice(Connection con, String itemName, int quantity) throws SQLException
    {
        String query = "SELECT gold FROM Item WHERE name = ?";

        try (PreparedStatement buyItem = con.prepareStatement(query))
        {
            buyItem.setString(1, itemName);

            try (ResultSet rs = buyItem.executeQuery())
            {
                if (rs.next())
                {
                    int itemGold = rs.getInt("gold");
                    return itemGold * quantity;
                }
            }
        }

        return 0;
    }

    private static boolean checkGold(Connection con, int totalPrice) throws SQLException
    {
        String query = "SELECT gold FROM Player WHERE player_id = ?";

        try (PreparedStatement gold = con.prepareStatement(query))
        {
            gold.setString(1, loggedInPlayerId);

            try (ResultSet rs = gold.executeQuery())
            {
                if (rs.next())
                {
                    int playerGold = rs.getInt("gold");
                    return playerGold >= totalPrice;
                }
            }
        }

        return false;
    }

    private static boolean checkQuantity(Connection con, String playerId, String itemName, int requestedQuantity) throws SQLException
    {
        String query = "SELECT quantity FROM Inventory WHERE player_id = ? AND items = ?";

        try (PreparedStatement checkQuantityStmt = con.prepareStatement(query))
        {
            checkQuantityStmt.setString(1, playerId);
            checkQuantityStmt.setString(2, itemName);

            try (ResultSet rs = checkQuantityStmt.executeQuery())
            {
                if (rs.next())
                {
                    int availableQuantity = rs.getInt("quantity");
                    return availableQuantity >= requestedQuantity;
                }
            }
        }

        return false;
    }

    private static int getPlayerGold(Connection con, String playerId) throws SQLException
    {
        String query = "SELECT gold FROM Player WHERE player_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(query))
        {
            stmt.setString(1, playerId);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    return rs.getInt("gold");
                }
            }
        }

        return 0;
    }

    private static int UpdateBuy(Connection con, int Price, String playerId, String itemName, int quantity) throws SQLException
    {
        String deductGoldQuery = "UPDATE Player SET gold = gold - ? WHERE player_id = ?";

        try (PreparedStatement deductGoldStmt = con.prepareStatement(deductGoldQuery))
        {
            deductGoldStmt.setInt(1, Price);
            deductGoldStmt.setString(2, playerId);
            deductGoldStmt.executeUpdate();
        }

        String checkItemQuery = "SELECT * FROM Inventory WHERE player_id = ? AND items = ?";

        try (PreparedStatement checkItemStmt = con.prepareStatement(checkItemQuery))
        {
            checkItemStmt.setString(1, playerId);
            checkItemStmt.setString(2, itemName);

            try (ResultSet rs = checkItemStmt.executeQuery())
            {
                if (rs.next())
                {
                    int existingQuantity = rs.getInt("quantity");
                    int price = rs.getInt("price");

                    String updateItemQuery = "UPDATE Inventory SET quantity = ?, price = ? " +
                            "WHERE player_id = ? AND items = ?";

                    try (PreparedStatement updateItemStmt = con.prepareStatement(updateItemQuery))
                    {
                        updateItemStmt.setInt(1, existingQuantity + quantity);
                        updateItemStmt.setInt(2, price);
                        updateItemStmt.setString(3, playerId);
                        updateItemStmt.setString(4, itemName);
                        updateItemStmt.executeUpdate();
                    }
                }

                else
                {
                    String addItemQuery = "INSERT INTO Inventory (player_id, items, quantity, price) " +
                            "VALUES (?, ?, ?, ?)";

                    try (PreparedStatement addItemStmt = con.prepareStatement(addItemQuery))
                    {
                        addItemStmt.setString(1, playerId);
                        addItemStmt.setString(2, itemName);
                        addItemStmt.setInt(3, quantity);
                        addItemStmt.setInt(4, Price);
                        addItemStmt.executeUpdate();
                    }
                }
            }
        }

        return getPlayerGold(con, playerId);
    }

    private static int UpdateSell(Connection con, int totalPrice, String playerId, String itemName, int soldQuantity) throws SQLException
    {
        String updateQuery = "UPDATE Inventory SET quantity = quantity - ? WHERE player_id = ? AND items = ?";

        try (PreparedStatement updateStmt = con.prepareStatement(updateQuery))
        {
            updateStmt.setInt(1, soldQuantity);
            updateStmt.setString(2, playerId);
            updateStmt.setString(3, itemName);
            updateStmt.executeUpdate();
        }

        int remainingQuantity = getRemainingQuantity(con, playerId, itemName);

        if (remainingQuantity == 0)
        {
            deleteItemFromInventory(con, playerId, itemName);
        }

        String addGoldQuery = "UPDATE Player SET gold = gold + ? WHERE player_id = ?";

        try (PreparedStatement addGoldStmt = con.prepareStatement(addGoldQuery))
        {
            addGoldStmt.setInt(1, totalPrice);
            addGoldStmt.setString(2, playerId);
            addGoldStmt.executeUpdate();
        }

        return getPlayerGold(con, playerId);
    }

    private static int getRemainingQuantity(Connection con, String playerId, String itemName) throws SQLException
    {
        String query = "SELECT quantity FROM Inventory WHERE player_id = ? AND items = ?";

        try (PreparedStatement stmt = con.prepareStatement(query))
        {
            stmt.setString(1, playerId);
            stmt.setString(2, itemName);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    return rs.getInt("quantity");
                }
            }
        }

        return 0;
    }

    private static void deleteItemFromInventory(Connection con, String playerId, String itemName) throws SQLException
    {
        String deleteQuery = "DELETE FROM Inventory WHERE player_id = ? AND items = ?";

        try (PreparedStatement deleteStmt = con.prepareStatement(deleteQuery))
        {
            deleteStmt.setString(1, playerId);
            deleteStmt.setString(2, itemName);
            deleteStmt.executeUpdate();
        }
    }

    //9번 메뉴
    private static void createNewPlayer(Connection con, Scanner scanner) throws SQLException
    {
        System.out.print("생성할 플레이어의 ID를 입력하세요 : ");
        String playerId = scanner.nextLine();

        System.out.print("비밀번호를 입력하세요 : ");
        String password = scanner.nextLine();

        System.out.print("플레이어의 이름을 입력하세요 : ");
        String playerName = scanner.nextLine();

        int hp = 100;
        int level = 1;
        int attackPower = 10;
        int gold = 0;

        String query = "INSERT INTO Player (player_id, password, name, hp, level, attack_power, gold) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement newPlayerInfo = con.prepareStatement(query))
        {
            newPlayerInfo.setString(1, playerId);
            newPlayerInfo.setString(2, password);
            newPlayerInfo.setString(3, playerName);
            newPlayerInfo.setInt(4, hp);
            newPlayerInfo.setInt(5, level);
            newPlayerInfo.setInt(6, attackPower);
            newPlayerInfo.setInt(7, gold);

            int rowsAffected = newPlayerInfo.executeUpdate();

            if (rowsAffected > 0)
            {
                System.out.println("새로운 플레이어를 생성했습니다 !");
            }

            else
            {
                System.out.println("플레이어 생성에 실패했습니다.");
            }
        }
    }

    //10번 메뉴
    private static void deletePlayer(Connection con, Scanner scanner) throws SQLException
    {
        System.out.print("삭제할 플레이어의 ID를 입력하세요 : ");
        String playerId = scanner.nextLine();

        System.out.print("비밀번호를 입력하세요 : ");
        String password = scanner.nextLine();

        String query = "DELETE FROM Player WHERE player_id = ? AND password = ?";

        try (PreparedStatement delPlayerInfo = con.prepareStatement(query))
        {
            delPlayerInfo.setString(1, playerId);
            delPlayerInfo.setString(2, password);

            int rowsAffected = delPlayerInfo.executeUpdate();

            if (rowsAffected > 0)
            {
                System.out.println("플레이어 삭제 완료");
            }
            else
            {
                System.out.println("플레이어 삭제에 실패하였습니다. 다시 시도해주세요.");
            }
        }
    }
}