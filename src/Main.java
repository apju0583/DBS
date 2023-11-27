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
                System.out.println("1. 플레이어 로그인");
                System.out.println("2. 플레이어 정보 확인");
                System.out.println("3. 플레이어 인벤토리 확인");
                System.out.println("4. 몬스터 도감 확인");
                System.out.println("5. NPC 도감 확인");
                System.out.println("6. 아이템 구매");
                System.out.println("7. 아이템 판매");
                System.out.println("8. 신규 플레이어 생성");
                System.out.println("9. 플레이어 삭제");
                System.out.println("0. 로그아웃");
                System.out.println("99. 프로그램 종료");
                System.out.println("----------------------------------------");
                System.out.print("선택: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                if(loggedInPlayerId == null)
                {
                    if (choice >= 2 && choice <= 7)
                    {
                        System.out.println("권한이 없습니다. 로그인을 해주십시오.");
                        continue;
                    }
                }

                if (choice == 1)
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

                    break;
                }

                else if (choice == 4)
                {

                    break;
                }

                else if (choice == 5)
                {

                    break;
                }

                else if (choice == 6)
                {

                    break;
                }

                else if (choice == 7)
                {

                    break;
                }

                else if (choice == 8)
                {
                    if (loggedInPlayerId != null)
                    {
                        System.out.println("현재 로그인 중입니다. 로그아웃 이후 다시 시도해주세요.");
                        continue;
                    }

                    createNewPlayer(con, scanner);
                    continue;
                }

                else if (choice == 9)
                {
                    if (loggedInPlayerId != null)
                    {
                        System.out.println("현재 로그인 중입니다. 로그아웃 이후 다시 시도해주세요.");
                        continue;
                    }

                    deletePlayer(con, scanner);
                    continue;
                }

                else if (choice == 0)
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

    //8번 메뉴
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

    //9번 메뉴
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