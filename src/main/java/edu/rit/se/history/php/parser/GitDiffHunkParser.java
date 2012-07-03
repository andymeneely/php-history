package edu.rit.se.history.php.parser;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.chaoticbits.devactivity.DBUtil;

public class GitDiffHunkParser {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GitDiffHunkParser.class);
	private SimpleDateFormat format;

	public GitDiffHunkParser() {
		format = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy +SSSS");
	}

	/**
	 * This parser goes through output of this type of command (not showing file path or revision specifiers
	 * - shown in $revrange and $file)
	 * 
	 * <code>
	 *  git log -p $revrange -- $file | grep -i -e "^@@" -e "^commit"
	 *  </code>
	 * 
	 * @param dbUtil
	 * @param input
	 * @throws Exception
	 */
	public void parse(DBUtil dbUtil, File input) throws Exception {
		Connection conn = dbUtil.getConnection();
		PreparedStatement ps = conn.prepareStatement("INSERT INTO GitDiffHunks(Commit, Filepath, LineDeletedStart, "
				+ "LineDeletedNumber, LineAddedStart, LineAddedNumber) " + "VALUES (?,?,?,?,?,?)");
		Scanner scanner = new Scanner(input);
		log.debug("Scanning the file...");
		String commit = null; // defaults should fail on a bad parse
		String filepath = null;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("commit")) {
				commit = line.substring(7);
			} else if (line.startsWith("--- a/")) {
				filepath = line.substring(6);
			} else if (line.startsWith("@@")) {
				int lineDeletedStart = -1;
				int lineDeletedNumber = 1;
				int lineAddedStart = -1;
				int lineAddedNumber = 1;
				// @@ -a,b +c,d @@
				// @@ -a +c,d @@ when b is 1
				String[] split = line.split(" ");
				String[] subSplit = split[1].split(",");
				if (subSplit.length > 1) {
					lineDeletedNumber = Integer.valueOf(subSplit[1]); // b
				}
				lineDeletedStart = Integer.valueOf(subSplit[0]) * -1;// a
				String[] subSplit2 = split[2].split(",");
				if (subSplit2.length > 1) {
					lineAddedNumber = Integer.valueOf(subSplit2[1]);// c
				}
				lineAddedStart = Integer.valueOf(subSplit2[0].substring(1));// d
				int i = 1;
				if(commit==null || filepath==null)
					i=1;
				ps.setString(i++, commit);
				ps.setString(i++, filepath);
				ps.setInt(i++, lineDeletedStart);
				ps.setInt(i++, lineDeletedNumber);
				ps.setInt(i++, lineAddedStart);
				ps.setInt(i++, lineAddedNumber);
				ps.addBatch();
			}
		}
		log.debug("\tExecuting batch insert...");
		ps.executeBatch();
		scanner.close();
		conn.close();
	}
}
