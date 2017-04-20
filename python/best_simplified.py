"""
.. module:: BEST
    :platform: Unix, linux, Windows
.. moduleauthor:: Sunkyu Kim <sunkyu-kim@korea.ac.kr>

==================================
Biomedical entity query API v2
==================================

.. note:: Usage: Biomedical entity query API (simplified)

>>> from dmis import best
>>>
>>> bestQuery = best.BESTQuery({"keywordA":["cancer"], "keywordB":["BRCA1", "EGFR"], "filterObjectName":"", "topN":30})
>>> bestQuery.addKeywordtoB("TP53")
>>> relevantEntities = best.getRelevantBioEntities(bestQuery)
>>>
>>> print(relevantEntities)
{'diseases': [{'abstracts': ['A distinctive feature ...',
                             'The excess risk of ...',
                             'Many important ...'],
               'entityName': 'breast neoplasms',
               'numArticles': 147184,
               'rank': 1,
               'score': 327993.0},
              {'abstracts': ["The relationship ...",
                             'Relatively little ...',
                             'Strong inter- and ...'],
               'entityName': 'ovarian neoplasms',
               'numArticles': 5526,
               'rank': 2,
               'score': 13530.978},


"""

from functools import reduce
import http
#from http.client import HTTPException
import socket

class BESTQuery():
    """
    dmis.BESTQuery class is basic query object for BEST API.

    """

    besturl = "http://best.korea.ac.kr/s?"


    def __init__(self, query, filterObjectName="All Entity?", topN=20, noAbsTxt=True):
        """BESTQuery
        :param queryObj: keywordA (list), filterObjectName (string), topN (int) dict return.

        >>> query = BESTQuery({"keywordA":["cancer"], "BRCA1"], "filterObjectName":"", "topN":30, "noAbsTxt":False})
        >>> # result of query will include abstract text.
        """

        self.query = query
        self.filterObjectName = filterObjectName
        self.topN = topN
        self.noAbsTxt = noAbsTxt

    def setQuery (self, query):
        """Setting the query

        :param keyword: a string object

        >>> query.setQuery(["cancer"])
        """
        if type(query) is not str:
            print ("Initialize error : invalid query. It should be a string object.")
            print (query)
            return

        if len(query) == 0:
            query = [""]
            return

        for keya in keywords :
            if type(keya) is not str :
                print ("Initialize error : invalid keywordA. keywordA should be list of string")
                print (keywords)
                return

        self.keywordA = keywords

    def getKeywordA (self):
        """Getting the primary keyword (Keyword A)

        :return: keyword A string

        >>> keywordA = query.getKeywordA()
        >>> print (keywordA)
        ["cancer"]
        """
        return self.keywordA

    def addKeywordtoA (self, keyword):
        """Adding a keyword to the primary keyword list (Keyword A)

        :param keyword: the keyword to be added to the primary keyword list

        >>> print (query.getKeywordA())
        ["cancer"]
        >>> query.addKeywordtoA("EGFR")
        >>> print (query.getKeywordA())
        ["cancer","EGFR"]
        """
        self.keywordB.append(keyword)

    def removeKeywordfromA(self, keyword):
        """Removing a keyword from the primary keyword list (Keyword A)

        :param keyword: the keyword to be removed from the primary keyword list

        >>> print (query.getKeywordA())
        ["cancer","EGFR"]
        >>> query.removeKeywordfromA("EGFR")
        >>> print (query.getKeywordA())
        ["cancer"]
        """
        self.keywordB.remove(keyword)

    def isValid(self):
        if self.keywordA is not None and self.keywordA is not None and type(self.keywordA) is not list:
            return False

        for keya in self.keywordA :
            if type(keya) is not str :
                return False

        if self.topN <= 0:
            return False

        return True

    def setTopN (self, n):
        """ Setting the number of results retrieved by query

        :param n: the number of results to be retrieved

        >>> query.setTopN(100)
        """
        self.topN = n

    def getTopN (self):
        """ Getting the number of results retrieved by query

        :return: the number of results to be retrieved

        >>> print (query.getTopN())
        100
        """
        return self.topN

    def setFilterObjectName (self, oname):
        """ Setting the filtering object. Gene name is case-sensitive.

        >>> qeury.setFilterObjectName("breast cancer")
        """
        self.filterObjectName = oname

    def getFilterObjectName (self):
        """ Getting the filtering object. Gene name is case-sensitive.

        >>> print(query.getFilterObjectName())
        "breast cancer"
        """
        return self.filterObjectName

    def getOidFromName(self):

        queryoname = self.filterObjectName.replace(" ", "+")

        oname = self.filterObjectName
        onameLower = oname.lower()

        oidQuery = "http://best.korea.ac.kr/collection1/select?q=dic_object_name%3A%22" + queryoname + "%22+or+dic_object_name%3A%22"+queryoname.lower()+"%22&wt=json&indent=true"

        import urllib.request

        again = 0
        while(again < 5) :
            try:
                request = urllib.request.Request(oidQuery)
                request.add_header('User-Agent', 'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)')

                oidUrl = urllib.request.urlopen(request)
                oidResultStr = oidUrl.read().decode('utf-8')
                again = 10
            except http.client.BadStatusLine:
                again += 1
            except http.client.HTTPException:
                again += 1
            except socket.timeout:
                again += 1
            except socket.error:
                again += 1
            except urllib.error.URLError:
                again += 1
            except Exception:
                again += 1

        if again == 5:
            print("Network status is not good")
            return []

        import json


        resultObject = json.loads(oidResultStr)

        oidDicObjects = resultObject["response"]["docs"]

        candidates = []

        for oidDicObject in oidDicObjects :
            if oidDicObject["dic_object_name"] == oname or oidDicObject["dic_object_name"] == onameLower:
                self.filterObjectName = oidDicObject["dic_object_name"]
                return oidDicObject["dic_object_id"]
            else:
                candidates.append(oidDicObject["dic_object_name"])

        return candidates

    def makeQueryString(self, querytype, abstract=True):
        queryKeywords = reduce(lambda x, y: x + " OR " + y , map(lambda x:"(" + x + ")", self.keywordA))

        import urllib.parse

        queryKeywords = "q=" + urllib.parse.quote(queryKeywords)

        otype = ""
        if querytype == "gene":
            otype = "8"
        elif querytype == "pathway":
            otype = "12"
        elif querytype == "disease":
            otype = "4"

        if otype == "":
            return "Invalid type! type can be [gene, pathway, disease]"

        if abstract:
            strQuery = self.besturl + "t=l&wt=xslt&tr=tmpl.xsl" + "&otype=" + otype + "&rows=" + str(self.topN) + "&" + queryKeywords
        else:
            strQuery = self.besturl + "t=l&wt=xslt&tr=tmpl2.xsl" + "&otype=" + otype + "&rows=" + str(self.topN) + "&" + queryKeywords

        return strQuery

    def toDataObj(self):
        return {"keywordA":self.keywordA, "filterObjectName":self.filterObjectName, "topN":self.topN}

def getRelevantBioEntities(bestQuery):
    """ Function for retrieval from BEST

    :param bestQuery: BESTQuery

    :return: parsed objects (dict-ENTITY_SET).

    * ENTITY_SET (dict): {"queryResult":QUERY_RESULT, "queryObject":QUERY_OBJECT, genes":[BIOENTITY] , "pathways":[BIOENTITY], "diseases":[BIOENTITY]}
    * BIOENTITY (dict): {"rank":int, "score":float, "numArticles":int, "abstracts":[str]}
    * QUERY_RESULT (string): [success - non-filtered | success - invalid filter | success - filtered]
    * QUERY_OBJECT (dict): Instance of class BESTQuery

    >>> bestQuery = BESTQuery({"keywordA":"cancer", "keywordB":["breast cancer","BRCA1"], "filterObjectName":"", "topN":5})
    >>> relevantEntities = getRelevantBioEntities(bestQuery)
    """
    if not (type(bestQuery) is BESTQuery):
        print ("query is invalid! please check your query object.")
        return {"queryResult" : "invalid query", "genes":[], "pathways":[], "diseases":[]}

    if not bestQuery.isValid() :
        print ("Query object is invalid. Please check the query")
        print ("Query : ")
        print ("   keywordA: " + str(bestQuery.keywordA))
        print ("   topN: " + str(bestQuery.topN))

        return {"queryResult" : "invalid query", "genes":[], "pathways":[], "diseases":[]}


    candidateOids = bestQuery.getOidFromName()

    if type(candidateOids) == type([]):
        fq = ""
    else:
        fq = "&fq=oid:" + candidateOids

    if fq != "":
        bestQuery.topN = 10


    if bestQuery.noAbsTxt == True:
        geneQuery = bestQuery.makeQueryString_noAbsTxt("gene") + fq
        pathwayQuery = bestQuery.makeQueryString_noAbsTxt("pathway") + fq
        diseaseQuery = bestQuery.makeQueryString_noAbsTxt("disease") + fq
    else :
        geneQuery = bestQuery.makeQueryString("gene") + fq
        pathwayQuery = bestQuery.makeQueryString("pathway") + fq
        diseaseQuery = bestQuery.makeQueryString("disease") + fq

    import urllib.request

    again = 0
    while(again < 5) :
        try:
            request = urllib.request.Request(geneQuery)
            request.add_header('User-Agent', 'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)')

            geneUrl = urllib.request.urlopen(request, timeout=5)
            geneResultStr = geneUrl.read().decode('utf-8')
            again = 10
        except http.client.BadStatusLine:
            again += 1
        except http.client.HTTPException:
            again += 1
        except socket.timeout:
            again += 1
        except socket.error:
            again += 1
        except urllib.error.URLError:
            again += 1
        except Exception:
            again += 1

    if again == 5:
        print("Network status is not good")
        return {"queryResult" : "invalid", "genes":[], "pathways":[], "diseases":[], "queryObject":bestQuery.toDataObj()}


    if(fq == ""):
        geneResult = makeDataFromBestQueryResult(geneResultStr)
    else:
        geneResult = makeDataFromBestQueryResult_filtered(geneResultStr, bestQuery.filterObjectName)

    again = 0
    while(again < 5) :
        try:
            request = urllib.request.Request(pathwayQuery)
            request.add_header('User-Agent', 'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)')

            pathwayUrl = urllib.request.urlopen(request, timeout=5)
            pathwayResultStr = pathwayUrl.read().decode('utf-8')
            again = 10
        except http.client.BadStatusLine:
            again += 1
        except http.client.HTTPException:
            again += 1
        except socket.timeout:
            again += 1
        except socket.error:
            again += 1
        except urllib.error.URLError:
            again += 1
        except Exception:
            again += 1

    if again == 5:
        print("Network status is not good")
        return {"queryResult" : "invalid", "genes":[], "pathways":[], "diseases":[], "queryObject":bestQuery.toDataObj()}


    if(fq == ""):
        pathwayResult = makeDataFromBestQueryResult(pathwayResultStr)
    else:
        pathwayResult = makeDataFromBestQueryResult_filtered(pathwayResultStr, bestQuery.filterObjectName)


    again = 0
    while(again < 5) :
        try:
            request = urllib.request.Request(diseaseQuery)
            request.add_header('User-Agent', 'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)')

            diseaseUrl = urllib.request.urlopen(request, timeout=5)
            diseaseResultStr = diseaseUrl.read().decode('utf-8')
            again = 10
        except http.client.BadStatusLine:
            again += 1
        except http.client.HTTPException:
            again += 1
        except socket.timeout:
            again += 1
        except socket.error:
            again += 1
        except urllib.error.URLError:
            again += 1
        except Exception:
            again += 1

    if again == 5:
        print("Network status is not good")
        return {"queryResult" : "invalid", "genes":[], "pathways":[], "diseases":[], "queryObject":bestQuery.toDataObj()}


    if(fq == ""):
        diseaseResult = makeDataFromBestQueryResult(diseaseResultStr)
    else:
        diseaseResult = makeDataFromBestQueryResult_filtered(diseaseResultStr, bestQuery.filterObjectName)

    if bestQuery.filterObjectName == "":
        resultType="success - non-filtered"
    elif fq == "":
        resultType="success - invalid filter"
    else:
        resultType="success - filtered"

    return {"queryResult" : resultType, "genes":geneResult, "pathways":pathwayResult, "diseases":diseaseResult, "queryObject":bestQuery.toDataObj()}

def makeDataFromBestQueryResult(resultStr):
    lines = resultStr.split('\n')
    linesCnt = len(lines)

    resultDataArr = []
    curData = {"rank":0}
    for i in range(1, linesCnt) :
        line = lines[i]

        if line.startswith("@@@"):
            curData["abstracts"].append(line[3:].strip())
        else:
            if len(line.strip()) == 0 :
                continue

            if curData["rank"] != 0:
                resultDataArr.append(curData)

            dataResult = line.split(" | ")

            curData = {"rank":int(dataResult[0].strip()), "entityName":dataResult[1].strip(), "score":float(dataResult[2].strip()), "numArticles":int(dataResult[3].strip()), "abstracts":[]}

    resultDataArr.append(curData)

    return resultDataArr

def makeDataFromBestQueryResult_filtered(resultStr, filter):
    lines = resultStr.split('\n')
    linesCnt = len(lines)

    resultDataArr = []
    curData = {"rank":0}
    for i in range(1, linesCnt) :
        line = lines[i]

        if line.startswith("@@@"):
            curData["abstracts"].append(line[3:].strip())
        else:
            if len(line.strip()) == 0 :
                continue

            if curData["rank"] != 0:
                resultDataArr.append(curData)

            dataResult = line.split(" | ")

            curData = {"rank":int(dataResult[0].strip()), "entityName":dataResult[1].strip(), "score":float(dataResult[2].strip()), "numArticles":int(dataResult[3].strip()), "abstracts":[]}

    resultDataArr.append(curData)

    for resultData in resultDataArr:
        if resultData["entityName"] == filter:
            return [resultData];

    return []
