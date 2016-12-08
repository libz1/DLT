package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.sqlite.JDBC;

public class testExe {

	public static void main(String[] args) {

		String fileName = "test.db";
		// Driver to Use
		// http://www.zentus.com/sqlitejdbc/index.html
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:"
					+ fileName);
			Statement stat = conn.createStatement();
			
			String sql = "CREATE TABLE DARData "
					+ "(No PRIMARY KEY     NOT NULL,"
					+ " Name           CHAR(50)    NOT NULL, "
					+ " Desc        CHAR(50)     )";
			stat.executeUpdate(sql);
			
			/*
			String sql = "CREATE TABLE DataType "
					+ "(No PRIMARY KEY     NOT NULL,"
					+ " Name           CHAR(50)    NOT NULL, "
					+ " ByteLen        INT     NOT NULL)";
			stat.executeUpdate(sql);
			
			PreparedStatement prep = conn
					.prepareStatement("insert into DataType values (?, ?, ?);");

			prep.setString(1, "5");
			prep.setString(2, "double-long");
			prep.setInt(3, 4);
			prep.addBatch();

			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
			
			ResultSet rs = stat.executeQuery("select * from DataType where bytelen <> 0 order by no;");
			while (rs.next()) { // 将查询到的数据打印出来
				System.out.print("No:" + rs.getString("No") + ";");
				System.out.print("Name:" + rs.getString("Name") + ";");
				System.out.println("ByteLen:" + rs.getInt("ByteLen"));
			}

			rs.close();
			*/
			conn.close(); // 结束数据库的连接

		} catch (Exception e) {
			e.printStackTrace();
		}

		// stat.executeUpdate( CREATE_TABLE_SONGINFO );

		// System.out.println(Unit698.seprateString("12345"," "));

		/*
		 * 
		 * 
		 * Country China = new Country(); China.c_name = "中国";
		 * 
		 * Province SD = new Province(); SD.p_name = "山东";
		 * 
		 * Province TJ = new Province(); TJ.p_name = "天津";
		 * 
		 * City QD = new City(); QD.cityName = "青岛";
		 * 
		 * City JN = new City(); JN.cityName = "济南";
		 * 
		 * SD.cityList.add(QD); SD.cityList.add(JN);
		 * 
		 * China.provinceList.add(SD); China.provinceList.add(TJ);
		 * 
		 * People people = new People(); people.P_name = "习";
		 * 
		 * China.chairMan = people;
		 * 
		 * 
		 * System.out.println(new Gson().toJson(China));
		 * 
		 * ///System.out.println("hi"); /* Map CLIENT1 = new HashMap();
		 * 
		 * CLIENT1.put("1","1"); CLIENT1.put("1","2"); CLIENT1.put("1","3");
		 * 
		 * CLIENT1.put("2","2"); CLIENT1.put("2","3");
		 * 
		 * System.out.println(CLIENT1.get("1").toString());
		 * System.out.println(CLIENT1.get("2").toString());
		 * 
		 * 
		 * 
		 * 
		 * // 先进出 LinkedList a = new LinkedList(); a.push("2"); a.push("3");
		 * a.push("4");
		 * 
		 * System.out.println(a.pop()); System.out.println(a.pop());
		 * System.out.println(a.pop());
		 * 
		 * System.out.println(a.size()); //System.out.println(a.pop());
		 * 
		 * // 先进先出 Queue<String> queue = new LinkedList<String>();
		 * queue.offer("2"); queue.offer("3"); queue.offer("4");
		 * 
		 * System.out.println(queue.poll()); System.out.println(queue.poll());
		 * System.out.println(queue.poll()); System.out.println(queue.poll());
		 */
	}

}
