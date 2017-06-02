import best
q = best.BESTQuery("breast cancer", "gene", noAbsTxt=False)
r = best.getRelevantBioEntities(q)
print(r[0])

