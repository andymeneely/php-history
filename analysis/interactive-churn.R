library(RODBC)
library(lattice)
library(plotrix)

conn <- odbcConnect("phphistory", uid="root", pwd="", case="tolower")

churn <- sqlQuery(conn, "SELECT * FROM Churn")
results <- sqlQuery(conn, "SELECT * FROM Results")

#Authors affected per commit
mean(results$AuthorsAffectedPerCommit[results$Vulnerable=="No"])
mean(results$AuthorsAffectedPerCommit[results$Vulnerable=="Yes"])
cor.test(results$SumNumChanges,results$AuthorsAffectedPerCommit)
wilcox.test(results$AuthorsAffectedPerCommit[results$Vulnerable=="Yes"],results$AuthorsAffectedPerCommit[results$Vulnerable=="No"])

#AvgPercLinesDeletedOther
vuln <- results$AvgPercLinesDeletedOther[results$Vulnerable=="Yes"]
not_vuln <- results$AvgPercLinesDeletedOther[results$Vulnerable=="No"]
mean(vuln,na.rm=TRUE)
mean(not_vuln,na.rm=TRUE)
wilcox.test(vuln,not_vuln)

#LinesDeletedOtherPerCommit
vuln <- results$LinesDeletedOtherPerCommit[results$Vulnerable=="Yes"]
not_vuln <- results$LinesDeletedOtherPerCommit[results$Vulnerable=="No"]
mean(vuln,na.rm=TRUE)
mean(not_vuln,na.rm=TRUE)
wilcox.test(vuln,not_vuln)

#PercLinesDeletedOtherPerCommit
vuln <- results$PercLinesDeletedOtherPerCommit[results$Vulnerable=="Yes"]
not_vuln <- results$PercLinesDeletedOtherPerCommit[results$Vulnerable=="No"]
mean(vuln,na.rm=TRUE)
mean(not_vuln,na.rm=TRUE)
wilcox.test(vuln,not_vuln)

#SNRPercLinesDeletedOther
mean(results$SNRPercLinesDeletedOther[results$Vulnerable=="No"],na.rm=TRUE)
mean(results$SNRPercLinesDeletedOther[results$Vulnerable=="Yes"],na.rm=TRUE)
wilcox.test(results$SNRPercLinesDeletedOther[results$Vulnerable=="Yes"],results$SNRPercLinesDeletedOther[results$Vulnerable=="No"])

#Histograms
#hist(churn$AvgPercLinesDeletedSelf, breaks=50, col="red", freq=TRUE, labels=TRUE)
#hist(churn$AvgPercLinesDeletedSelf, breaks=50, col="red", freq=TRUE, labels=TRUE)

l <- list(results$AuthorsAffectedPerCommit[results$Vulnerable=="No"],results$AuthorsAffectedPerCommit[results$Vulnerable=="Yes"])
multhist(l,freq=FALSE)

#odbcClose(conn)
#rm(conn)

