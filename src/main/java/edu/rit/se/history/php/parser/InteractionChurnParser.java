package edu.rit.se.history.php.parser;

import java.io.File;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import org.chaoticbits.devactivity.DBUtil;

import com.mysql.jdbc.Connection;

public class InteractionChurnParser {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(InteractionChurnParser.class);

	public void parse(DBUtil dbUtil, File file) throws Exception {
		Connection conn = dbUtil.getConnection();
		PreparedStatement ps = conn
				.prepareStatement("UPDATE GitLogFiles SET LinesDeletedSelf=?, LinesDeletedOther=?, AuthorsAffected=? WHERE Commit=? AND Filepath=?");
		Scanner scanner = new Scanner(file);
		String commit = null;
		String filepath = null;
		Integer linesDeletedSelf = null;
		Integer linesDeletedOther = null;
		Integer authorsAffected = null;
		log.debug("Scanning the log...");
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("***** Revision ")) {
				commit = line.split(" ")[2];
			} else if (line.startsWith("***** File")) {
				filepath = line.split(" ")[2].substring(2); // cut off the ./
			} else if (line.startsWith("Lines Deleted, self:")) {
				linesDeletedSelf = Integer.valueOf(line.split("\t")[1]);
			} else if (line.startsWith("Lines Deleted, other:")) {
				linesDeletedOther = Integer.valueOf(line.split("\t")[1]);
			} else if (line.startsWith("Number of Authors Affected:")) { // this is always last
				authorsAffected = Integer.valueOf(line.split("\t")[1]);
				int i = 1;
				ps.setInt(i++, linesDeletedSelf);
				ps.setInt(i++, linesDeletedOther);
				ps.setInt(i++, authorsAffected);
				ps.setString(i++, commit);
				ps.setString(i++, filepath);
				ps.addBatch();
			} else { /* skip everything else */}
		}
		scanner.close();
		log.debug("Executing batch insert...");
		ps.executeBatch();
		conn.close();
	}
}
