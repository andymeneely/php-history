package edu.rit.se.history.php.parser;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.util.Scanner;

import org.chaoticbits.devactivity.DBUtil;

import com.mysql.jdbc.Connection;

public class GitBlameParser {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GitBlameParser.class);

	public void parse(DBUtil dbUtil, InputStream inputStream) throws Exception {
		Connection conn = dbUtil.getConnection();
		PreparedStatement ps = conn.prepareStatement("INSERT INTO GitBlames(Commit,Filepath,LineNumber,LineCommit) VALUES (?,?,?,?)");
		Scanner scanner = new Scanner(inputStream);
		String commit = null;
		String filepath = null;
		Integer lineNumber = null;
		String lineCommit = null;
		log.debug("Scanning the log...");
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("***** Revision ")) {
				String[] split = line.split(" ");
				commit = split[2];
			} else if (line.startsWith("***** File")) {
				String[] split = line.split(" ");
				filepath = split[2].substring(2); // cut off the ./
			} else if (line.startsWith("***** End")) {
				// skip it
			} else { // the actual git blame contents
				lineCommit = line.substring(0, 40); // first 40 characters
				lineNumber = parseLineNumber(line);
				int i = 1;
				ps.setString(i++, commit);
				ps.setString(i++, filepath);
				ps.setInt(i++, lineNumber);
				ps.setString(i++, lineCommit);
				ps.addBatch();
			}
		}
		scanner.close();
		log.debug("Executing batch insert...");
		ps.executeBatch();
		conn.close();
	}

	private Integer parseLineNumber(String line) {
		int leftSide = line.indexOf("+0000") + 5; // find the timezone thing
		int rightSide = line.indexOf(")", leftSide); // then find a ) after that
		return Integer.valueOf(line.substring(leftSide, rightSide).trim()); // trim and parse!
	}

}
