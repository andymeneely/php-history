DROP VIEW IF EXISTS Churn;

CREATE VIEW Churn AS
SELECT  Sum(NumChanges) SumNumChanges,
        Sum(LinesInserted) SumLinesInserted, 
        Sum(LinesDeleted) SumLinesDeleted,
        Sum(LinesDeletedSelf) SumLinesDeletedSelf, 
        Sum(LinesDeletedOther) SumLinesDeletedOther,
        Sum(AuthorsAffected) SumAuthorsAffected 
FROM GitLogFiles WHERE AuthorsAffected IS NOT NULL GROUP BY Filepath;