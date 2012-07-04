package edu.rit.se.history.php.analysis;

import java.sql.PreparedStatement;

import org.chaoticbits.devactivity.DBUtil;

import com.mysql.jdbc.Connection;

public class InteractionChurn {

	public void compute(DBUtil dbUtil) throws Exception {
		Connection conn = dbUtil.getConnection();
		PreparedStatement ps = conn
				.prepareStatement("UPDATE GitLog SET LinesDeletedSelf=?, LinesDeletedOther=?, AuthorsAffected=? WHERE Commit=? AND Filepath=?");
		conn.createStatement().execute("SELECT * FROM GitDiffHunks ");
		ps.executeBatch();
		conn.close();
		throw new IllegalStateException("unimplemented!");
	}

}
