package edu.rit.se.history.php.parser;

import java.io.File;
import java.sql.PreparedStatement;
import java.util.Scanner;

import org.chaoticbits.devactivity.DBUtil;

import com.mysql.jdbc.Connection;

public class VulnerabilitiesToFilesParser {

	public void parse(DBUtil dbUtil, File file, String phpRelease) throws Exception {
		Connection conn = dbUtil.getConnection();
		PreparedStatement ps = conn.prepareStatement("UPDATE Filepaths SET Vulnerable='Yes' WHERE Filepath=? AND PHPRelease=?");
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			ps.setString(1, line);
			ps.setString(2, phpRelease);
			ps.addBatch();
		}
		ps.executeBatch();
		scanner.close();
		conn.close();
	}

}
