DROP VIEW IF EXISTS Churn;
DROP VIEW IF EXISTS Results;

CREATE VIEW Churn AS 
SELECT  Filepath,
		Sum(LinesInserted+LinesDeletedOther+LinesDeletedSelf) SumNumChanges,
		Count(Commit) NumCommits,
        Sum(LinesInserted) SumLinesInserted, 
        Sum(LinesDeletedOther+LinesDeletedSelf) SumLinesDeleted,
        Sum(LinesDeletedSelf) SumLinesDeletedSelf, 
        Sum(LinesDeletedOther) SumLinesDeletedOther,
        Sum(AuthorsAffected) SumAuthorsAffected
	FROM GitLogFiles WHERE AuthorsAffected IS NOT NULL GROUP BY Filepath;
	
CREATE VIEW Results AS  
SELECT  c.*,
		f.SLOCType,
		f.SLOC,
		c.SumNumChanges/f.SLOC ChurnPerSLOC,
		SumAuthorsAffected/NumCommits AuthorsAffectedPerCommit,
        SumLinesDeletedOther/NumCommits LinesDeletedOtherPerCommit,
        (SumLinesDeletedOther/(SumLinesDeleted+SumLinesInserted))/NumCommits PercLinesDeletedOtherPerCommit,
		f.Vulnerable 
FROM Churn c INNER JOIN Filepaths f ON (c.filepath=f.filepath) ; 