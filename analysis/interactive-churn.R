library(RODBC)
library(lattice)
library(plotrix)

conn <- odbcConnect("phphistory", uid="root", pwd="", case="tolower")

results <- sqlQuery(conn, "SELECT * FROM Results")

#Churn by itself 
vuln <- results$SumNumChanges[results$Vulnerable=="Yes"]
not_vuln <- results$SumNumChanges[results$Vulnerable=="No"]
mean(vuln,na.rm=TRUE)
mean(not_vuln,na.rm=TRUE)
wilcox.test(vuln,not_vuln)

#Authors affected per commit
mean(results$AuthorsAffectedPerCommit[results$Vulnerable=="No"])
mean(results$AuthorsAffectedPerCommit[results$Vulnerable=="Yes"])
cor.test(results$SumNumChanges,results$AuthorsAffectedPerCommit)
wilcox.test(results$AuthorsAffectedPerCommit[results$Vulnerable=="Yes"],results$AuthorsAffectedPerCommit[results$Vulnerable=="No"])

#PercLinesDeletedOtherPerCommit
vuln <- results$PercLinesDeletedOtherPerCommit[results$Vulnerable=="Yes"]
not_vuln <- results$PercLinesDeletedOtherPerCommit[results$Vulnerable=="No"]
mean(vuln,na.rm=TRUE)
mean(not_vuln,na.rm=TRUE)
wilcox.test(vuln,not_vuln)

fit <- glm(Vulnerable ~ PercLinesDeletedOtherPerCommit + SumNumChanges + AuthorsAffectedPerCommit, data = results, family = "binomial")
summary(fit)

#Histograms
#hist(results$PercLinesDeletedOtherPerCommit, breaks=50, col="red", freq=TRUE, labels=TRUE)
#hist(results$AvgPercLinesDeletedSelf, breaks=50, col="red", freq=TRUE, labels=TRUE)

l <- list(results$AuthorsAffectedPerCommit[results$Vulnerable=="No"],results$AuthorsAffectedPerCommit[results$Vulnerable=="Yes"])
multhist(l,freq=FALSE)

#odbcClose(conn)
#rm(conn)

