package best_api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * <p>
 * 
 * @version 1.0
 * @author Sunkyu Kim <br>
 *         sunkyu-kim@korea.ac.kr
 *         </p>
 *         <h2>Biomedical Entity Search Tool JAVA API</h2><br>
 *         API Page URL<br>
 *         http://infos.korea.ac.kr/BEST_API<br>
 *         <br>
 *         BEST Web version URL<br>
 *         http://best.korea.ac.kr<br>
 *         <p>
 *         You can retrieve Biomedical entity with
 *         getRelevantBioEntities(BESTQuery)
 *         </p>
 * 
 *         For Retrieval, <br>
 *         1. make BESTQuery instance.<br>
 *         2. Use getRelevantBioEntities(BESTQuery). It returns ArrayList of
 *         BESTResult instances.
 *         <p>
 * 
 *         For synonyms,<br>
 *         Use getSynonyms(String). It returns ArrayList of synonyms.<br>
 */
public class BEST {
	final private static boolean TEST = false;

	final public static int MainServer = 0;
	final public static int TestServer = 1;
	final public static int DREAMServer = 2;
	private static String CurrentServer = "http://best.korea.ac.kr";

	private BEST() {

	}

	/**
	 * Getting results with BEST Query
	 * 
	 * @param query
	 *            BESTQuery format
	 * @return BESTResult list of results
	 */

	public static ArrayList<BESTResult> getRelevantBioEntities(BESTQuery query) {
		return getRelevantBioEntities(query, false, false);
	}

	/**
	 * Getting results with BEST Query
	 * 
	 * @param query
	 *            BESTQuery format
	 * @param abstractSend
	 *            If you want to get the PubMed abstract documents in which the
	 *            entity of the result exists, Put true.
	 * @param queryExpansionWithSynonyms
	 *            If you want to expand your query with "oid"(object id in solr
	 *            system), Put true. e.g) "imatinib" ==
	 *            "imatinib OR imatinib mesylate OR ..."
	 * @return BESTResult list of results
	 */
	public static ArrayList<BESTResult> getRelevantBioEntities(BESTQuery query, boolean abstractSend,
			boolean queryExpansionWithSynonyms) {

		String keyword = getKeywordString(query.getKeyword());

		String filterObjectName = query.getFilterObjectName();
		String filterObjectNumber = "";

		if (filterObjectName != null) {
			filterObjectNumber = getFilterObjectNumber(query.getFilterObjectName().toLowerCase());
		}

		String filterQueryName = query.getFilterQueryName();
		int resultNum = query.getTopN();
		/*
		 * Document text = Jsoup.connect(CurrentServer + "/s?otype=" +
		 * filterObjectNumber + "&q=" + keyword +
		 * "&t=l&wt=xslt&tr=tmpl.xsl").get();
		 * 
		 * return extractResult(text.getElementsByTag("body").text());
		 */

		String keyword_web = keyword.replaceAll(" ", "%20");

		if (TEST) {
			System.out.println("===getRelevantBioEntities===");
			System.out.println("Query :\t" + keyword);
			System.out.println("Filter :\t" + filterObjectName + ", " + filterObjectNumber);
			System.out.println("Query_web :\t" + keyword_web);
		}

		/*
		 * Synonym checking 부분
		 * 
		 * 기본적으로 keyword_web 으로부터 oid 를 찾는다.. 단일 object 일 경우만 찾는다..
		 * "imatinib and dasatinib" 일 경우, "oid:123 and oid:456" 이렇게 바꿔주지는 못한다.
		 * 추후 object extractor 등으로 찾을 예정.
		 * 
		 * 해당 기능이 필요하지 않을 수 있다. 단순 query_web 만 갖고 검색하고 싶을 경우 oid=null 로 설정해준다.
		 * 15.10.22
		 */
		String oid = null;
		if (queryExpansionWithSynonyms)
			oid = getDicObjectId(keyword_web.replace("\"", ""));

		String urlString = "";
		/*
		 * if oid exists, search with oid.
		 * 
		 * 수정사항 : result num을 반영하도록 수정
		 */
		if (oid == null || oid == "") {
			urlString = CurrentServer + "/s?otype=" + filterObjectNumber + "&q=" + keyword_web + "&rows=" + resultNum
					+ "&t=l&wt=xslt";
		} else {
			urlString = CurrentServer + "/s?otype=" + filterObjectNumber + "&q=" + "oid%3A" + oid + "&rows=" + resultNum
					+ "&t=l&wt=xslt";
		}
		if (TEST) {
			System.out.println("#oid:\t" + oid);
		}

		/*
		 * if abstractSend is true, use tmpl.xsl. else, use tmpl2.xsl. tmpl.xsl
		 * : the search result has abstract documents. tmpl2.xsl : the search
		 * result doesn't have the documents.
		 */
		if (abstractSend) {
			urlString += "&tr=tmpl.xsl";
		} else {
			urlString += "&tr=tmpl2.xsl";
		}

		/*
		 * if the query has filterQueryName, add "fq=filterquery" to the url
		 * string. then, we can get a result focused on the filterQuery entity.
		 */
		String filterQueryName_web = null;
		String filterQueryName_oid = null;
		if (filterQueryName != null) {
			if (TEST)
				System.out.println("filterquery 검색 됩니까?");
			filterQueryName_web = filterQueryName.replaceAll(" ", "%20");
			filterQueryName_oid = getDicObjectId(filterQueryName_web);
			if (!(filterQueryName_oid == null || filterQueryName_oid == "")) {
				urlString += ("&fq=oid:" + filterQueryName_oid);
			} else {
				urlString += ("&fq=" + filterQueryName_web);
			}
		}

		if (TEST) {
			System.out.println("URL:\t" + urlString);
		}
		// URL url = new URL(CurrentServer + "/s?otype="
		// + filterObjectNumber + "&q=" + "oid%3A" + oid
		// + "&t=l&wt=xslt&tr=tmpl.xsl");
		// System.out.println(CurrentServer + "/s?otype="
		// + filterObjectNumber + "&q=" + "oid%3A" + oid
		// + "&t=l&wt=xslt&tr=tmpl.xsl");
		// URL url = new URL(CurrentServer + "/s?otype="
		// + filterObjectNumber + "&q=" + keyword_web
		// + "&t=l&wt=xslt&tr=tmpl.xsl");
		//

		ArrayList<String> lines = new ArrayList<String>();
		try {
			lines = readLineFromURL(urlString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return extractResult(lines, filterQueryName_oid);
	}

	private static ArrayList<String> readLineFromURL(String urlString) throws IOException {
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();

		connection.setConnectTimeout(3000);

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		ArrayList<String> lines = new ArrayList<String>();
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			lines.add(inputLine);
		}
		in.close();

		return lines;
	}

	/**
	 * Getting synonyms
	 * 
	 * @param name
	 *            It returns the synonyms of this name
	 * @return String synonym list
	 */
	public static ArrayList<String> getSynonyms(String name) {

		if (TEST) {
			System.out.println("===getSynonyms===");
		}
		String dicObjId = getDicObjectId(name);

		if (dicObjId == null) {
			if (TEST) {
				System.out.println("No dicObjID(oid)");
			}
			return null;
		}

		ArrayList<String> syns = getObjectNames(dicObjId);

		if (TEST) {
			System.out.println("~~Synonyms of \"" + name + "\"");
			for (String s : syns) {
				System.out.println(s);
			}
		}

		return syns;
	}

	private static String getKeywordString(ArrayList<String> keywords) {

		StringBuilder sb = new StringBuilder();
		if(keywords.size() == 1){
			sb.append(keywords.get(0));
		}
		else{
			for (String k : keywords) {
				sb.append("\"" + k + "\"" + " ");
			}
		}

		return sb.toString().trim();
	}

	private static String getFilterObjectNumber(String filterObjectName) {
		String otype;

		if (filterObjectName.equals("gene")) {
			otype = "8";
		} else if (filterObjectName.equals("drug")) {
			otype = "5";
		} else if (filterObjectName.equals("chemical compound")) {
			otype = "3";
		} else if (filterObjectName.equals("target")) {
			otype = "14";
		} else if (filterObjectName.equals("disease")) {
			otype = "4";
		} else if (filterObjectName.equals("toxin")) {
			otype = "15";
		} else if (filterObjectName.equals("transcription factor")) {
			otype = "16";
		} else if (filterObjectName.equals("mirna")) {
			otype = "10";
		} else if (filterObjectName.equals("pathway")) {
			otype = "12";
		} else if (filterObjectName.equals("mutation")) {
			otype = "17";
		} else {
			otype = "";
		}
		return otype;
	}

	/*
	 * extract result from body text
	 */
	private static ArrayList<BESTResult> extractResult(ArrayList<String> lines, String filterQueryName_oid) {

		ArrayList<BESTResult> results = new ArrayList<BESTResult>();
		BESTResult result = null;

		for (String l : lines) {
			if (l.equals(""))
				continue;
			if (l.charAt(0) == '<') {
				continue;
			} else if (l.charAt(0) == '@') {
				String abs = l.replace("@@@", "");
				// System.out.println(abs);
				result.addAbstracts(abs);
			} else {
				if (result != null) {
					// except the first line,
					// add this result to result array
					if (filterQueryName_oid == null) {
						results.add(result);
					} else {
						if (result.getOid().equals(filterQueryName_oid)) {
							results.add(result);
							return results;
						}
					}
				}

				String[] info = l.split(" \\| ");
				// System.out.println(l);
				result = new BESTResult(info[0].trim(), info[1].trim(), info[2].trim(), info[3].trim(), info[4].trim());

			}
		}
		if (result != null)
			results.add(result);
		// last result

		return results;
	}

	/**
	 * Getting Dictionary object ID(oid)
	 * 
	 * @param name
	 *            The name of the string
	 * @return String oid
	 */
	/*
	 * get dic object id from json object
	 */
	public static String getDicObjectId(String name) {
		if (TEST) {
			System.out.println("===getDicObjectId===");
		}
		JSONObject jsonResult = getJsonResult("dic_object_nameLower", name);

		JSONObject response = (JSONObject) jsonResult.get("response");
		JSONArray docs = (JSONArray) response.get("docs");

		try {
			if (docs.size() > 1) {
				// System.out.println("it has many ids...");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * If case-sensitive is 0, it is not important whether lower case so,
		 * return matched one But case-sensitive is 1, it is important If the
		 * query is different with real name(dic_object_name), return null
		 */

		if (docs.size() == 0)
			return null;

		JSONObject obj = (JSONObject) docs.get(0);

		// if(obj.get("dic_case_sensitive") == null){
		// System.out.println("null");
		// }
		if ((Long) obj.get("dic_case_sensitive") == 1) {
			if (!name.equals((String) obj.get("dic_object_name"))) {
				return null;
			}
		}

		String dicObjId = (String) obj.get("dic_object_id");

		return dicObjId;
	}

	private static ArrayList<String> getObjectNames(String dic_obj_id) {
		ArrayList<String> names = new ArrayList<String>();

		JSONObject jsonResult = getJsonResult("dic_object_id", dic_obj_id);

		JSONObject response = (JSONObject) jsonResult.get("response");
		JSONArray docs = (JSONArray) response.get("docs");

		for (int i = 0; i < docs.size(); i++) {
			JSONObject obj = (JSONObject) docs.get(i);
			String name = (String) obj.get("dic_object_name");
			names.add(name);
		}
		return names;
	}

	/*
	 * get json format string from url
	 */
	private static JSONObject getJsonResult(String tag, String keyword) {
		StringBuilder jsonSB = new StringBuilder();
		JSONObject jsonResult = null;

		/*
		 * Query : A AND "B" => A AND B
		 * 
		 * Dic Object ID 찾을 때에만 쿼리에서 "" 를 지운다. Chronic Myeloid Leukemia 같은
		 * multiword 가 들어올 때를 대비해 앞뒤로 "" 를 붙여서 검색하게 되어있다. 근데 쿼리 중간에
		 * " 가 들어가버릴 경우 "~"B"~" 같은 형태가 되어버려서 신택스 오류가 난다. 따라서 dicObject ID 찾을 때에만
		 * 쿼리에서 ""를 지운다. 다만 이러면, 실제 chemical compound 들 중에 이름이 AB-ADE-"aef"-dBA
		 * 같은 것이 있을 경우 oid 를 못 찾게 된다.
		 */
		String repairedKeyword = keyword.replaceAll("\"", "");
		repairedKeyword = repairedKeyword.replaceAll(" ", "%20");
		// String repairedKeyword = keyword;
		// System.out.println(repairedKeyword);
		if (TEST) {
			System.out.println(repairedKeyword);
		}
		try {
			URL url = new URL(CurrentServer + "/collection1/select?q=" + tag + "%3A" + "%22"
					+ repairedKeyword.toLowerCase() + "%22" + "&wt=json&indent=true");
			// System.out.println(url.toString());
			URLConnection connection = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String line;
			while ((line = in.readLine()) != null) {
				// System.out.println(line);
				jsonSB.append(line);
			}

			JSONParser parser = new JSONParser();
			jsonResult = (JSONObject) parser.parse(jsonSB.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return jsonResult;
	}

	/**
	 * You can change the server either Main server or Test server as
	 * setServer(BEST.MainServer) or setServer(BEST.TestServer)
	 * 
	 * @param serverCode
	 */
	public static void setServer(int serverCode) {
		switch (serverCode) {
		case MainServer:
			CurrentServer = "http://best.korea.ac.kr";
			break;
		case TestServer:
			CurrentServer = "http://best.korea.ac.kr:38080";
		case DREAMServer:
			CurrentServer = "http://buzket.korea.ac.kr";
			break;
		}
	}

	private static ArrayList<Integer> getRelevantBioEntities_searchResult(BESTQuery query) {
		return getRelevantBioEntities_searchResult(query, false, false);
	}

	/**
	 * Getting results with BEST Query
	 * 
	 * @param query
	 *            BESTQuery format
	 * @param abstractSend
	 *            If you want to get the PubMed abstract documents in which the
	 *            entity of the result exists, Put true.
	 * @param queryExpansionWithSynonyms
	 *            If you want to expand your query with "oid"(object id in solr
	 *            system), Put true. e.g) "imatinib" ==
	 *            "imatinib OR imatinib mesylate OR ..."
	 * @return BESTResult list of results
	 */
	private static ArrayList<Integer> getRelevantBioEntities_searchResult(BESTQuery query, boolean abstractSend,
			boolean queryExpansionWithSynonyms) {

		String keyword = getKeywordString(query.getKeyword());

		String filterObjectName = query.getFilterObjectName();
		String filterObjectNumber = "";

		if (filterObjectName != null) {
			filterObjectNumber = getFilterObjectNumber(query.getFilterObjectName().toLowerCase());
		}

		String filterQueryName = query.getFilterQueryName();
		int resultNum = query.getTopN();
		/*
		 * Document text = Jsoup.connect(CurrentServer + "/s?otype=" +
		 * filterObjectNumber + "&q=" + keyword +
		 * "&t=l&wt=xslt&tr=tmpl.xsl").get();
		 * 
		 * return extractResult(text.getElementsByTag("body").text());
		 */

		String keyword_web = keyword.replaceAll(" ", "%20");
		keyword_web = keyword_web.replaceAll(" ", "%20");

		if (TEST) {
			System.out.println("===getRelevantBioEntities_searchResult===");
			System.out.println("Query :\t" + keyword);
			System.out.println("Filter :\t" + filterObjectName + ", " + filterObjectNumber);
			System.out.println("Query_web :\t" + keyword_web);
		}

		/*
		 * 언젠가 사라질 함수. searchinfo 는 다른 방향으로 구현
		 */
		String oid = null;
		if (queryExpansionWithSynonyms)
			oid = getDicObjectId(keyword_web);

		String urlString = "";
		/*
		 * if oid exists, search with oid.
		 * 
		 * 수정사항 : result num을 반영하도록 수정
		 */
		if (oid == null || oid == "") {
			urlString = CurrentServer + "/s?otype=" + filterObjectNumber + "&q=" + keyword_web + "&rows=" + resultNum
					+ "&t=l&wt=xslt";
		} else {
			urlString = CurrentServer + "/s?otype=" + filterObjectNumber + "&q=" + "oid%3A" + oid + "&rows=" + resultNum
					+ "&t=l&wt=xslt";
		}
		if (TEST) {
			System.out.println("#oid:\t" + oid);
		}

		/*
		 * if abstractSend is true, use tmpl.xsl. else, use tmpl2.xsl. tmpl.xsl
		 * : the search result has abstract documents. tmpl2.xsl : the search
		 * result doesn't have the documents.
		 */
		urlString += "&tr=searchinfo.xsl";

		/*
		 * if the query has filterQueryName, add "fq=filterquery" to the url
		 * string. then, we can get a result focused on the filterQuery entity.
		 */
		String filterQueryName_web = null;
		String filterQueryName_oid = null;
		if (filterQueryName != null) {
			if (TEST)
				System.out.println("filterquery 검색 됩니까?");
			filterQueryName_web = filterQueryName.replaceAll(" ", "%20");
			filterQueryName_oid = getDicObjectId(filterQueryName_web);
			if (!(filterQueryName_oid == null || filterQueryName_oid == "")) {
				urlString += ("&fq=oid:" + filterQueryName_oid);
			}
		}

		if (TEST) {
			System.out.println("URL:\t" + urlString);
		}
		// URL url = new URL(CurrentServer + "/s?otype="
		// + filterObjectNumber + "&q=" + "oid%3A" + oid
		// + "&t=l&wt=xslt&tr=tmpl.xsl");
		// System.out.println(CurrentServer + "/s?otype="
		// + filterObjectNumber + "&q=" + "oid%3A" + oid
		// + "&t=l&wt=xslt&tr=tmpl.xsl");
		// URL url = new URL(CurrentServer + "/s?otype="
		// + filterObjectNumber + "&q=" + keyword_web
		// + "&t=l&wt=xslt&tr=tmpl.xsl");
		//

		ArrayList<String> lines = new ArrayList<String>();
		boolean readLineFromURLChecker = false;
		int tryNumber = 0;

		// 서버 에러 났을 때 체킹..
		while (!readLineFromURLChecker) {
			try {
				lines = readLineFromURL(urlString);
				readLineFromURLChecker = true;
			} catch (IOException e) {
				// wait
				switch (tryNumber) {
				case 0:
				case 1:
					break;
				case 2:
					System.out.println("trying..");
					sleep(500);
					break;
				case 3:
					System.out.println("trying..");
					sleep(500);
					break;
				case 4:
					System.out.println("Poor Server\n");
					return null;
				}
				// TODO Auto-generated catch block

			}
			tryNumber++;
		}

		return extractResult_searchinfo(lines, filterQueryName_oid);
	}

	/*
	 * 언젠가 사라질 함수. searchinfo 는 다른 방향으로 구현
	 */
	private static ArrayList<Integer> extractResult_searchinfo(ArrayList<String> lines, String filterQueryName_oid) {

		ArrayList<Integer> results = new ArrayList<Integer>();

		for (String l : lines) {

			if (l.equals(""))
				continue;
			if (l.charAt(0) == '<') {
				continue;
			} else if (l.charAt(0) == '@') {
				String abs = l.replace("@@@", "");
				// System.out.println(abs);

			} else {
				String[] info = l.split(" \\| ");
				// System.out.println(l);

				int numOfObject = Integer.parseInt(info[0].trim());
				int numOfDoc = Integer.parseInt(info[1].trim());
				results.add(numOfObject);
				results.add(numOfDoc);
			}
		}
		return results;
	}

	private static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {

		}
	}

	public static void main(String args[]) throws IOException {

		BEST.setServer(BEST.MainServer);
		//String[] query = { "imatinib", "combination" };
		String[] query = {"imatninib OR combination"};

		BESTQuery bq = new BESTQuery("cancer", "gene", 100);

		ArrayList<BESTResult> results = BEST.getRelevantBioEntities(bq, true, false);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("100gene.txt")));
		for (BESTResult r : results) {			 
			bw.write(r.getName());
			bw.newLine();
			//System.out.println("===" + r.getName() + "");
			//for (String abs : r.getAbstracts()) {
			//	System.out.println(abs);
			//}
			//System.out.println();
		}
		bw.close();
	}

	public static void oidTestFunction(String cellLine, BufferedWriter bw) throws IOException {
		String[] keyword = { cellLine };

		boolean hasOid = false;

		String oid = BEST.getDicObjectId(cellLine);
		ArrayList<String> syns = BEST.getSynonyms(cellLine);
		if (oid != null)
			hasOid = true;
		// BESTQuery query = new BESTQuery(keyword, "gene", 5);
		// ArrayList<BESTResult> results = BEST.getRelevantBioEntities(query,
		// false, true);
		//
		bw.write(cellLine);
		// System.out.print(cellLine);
		// for (BESTResult result : results) {
		// bw.write("," + result.getName());
		//// bw.write("," + result.getName().toUpperCase());
		// }
		// bw.newLine();

		if (hasOid) {
			bw.write(",oid:" + oid);
			bw.write(",");
			for (String s : syns) {
				bw.write("," + s);
			}
		}
		// bw.write("O " + oid);

		else
			bw.write(",X");

		// for (BESTResult result : results) {
		// bw.write("," + result.getScore());
		//
		// }
		bw.newLine();
		System.out.println();
	}
}
