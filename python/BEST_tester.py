import best

bestQuery = best.BESTQuery({"keywordA:['cancer']"})
r = best.getRelevantBioEntities(bestQuery)
print (r)
