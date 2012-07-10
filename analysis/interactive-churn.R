library(RODBC)
library(lattice)
conn <- odbcConnect("phphistory", uid="root", pwd="", case="tolower")

churn <- sqlQuery(conn, "SELECT * FROM Churn")

cor.test(churn$SumNumChanges,churn$SumLinesDeletedSelf)
cor.test(churn$SumLinesDeletedOther,churn$SumLinesDeletedSelf)
cor.test(churn$SumNumChanges,churn$SumAuthorsAffected)

hist(churn$SumLinesDeletedSelf/churn$SumLinesDeleted, breaks=20, col="red", freq=TRUE, labels=TRUE)

#odbcClose(conn)
#rm(conn)

