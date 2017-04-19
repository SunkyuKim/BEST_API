package best_api;

import java.util.ArrayList;

/**
 * It is used as query for BEST. Query string, search filter and the number of results are contained in this class.  
 */
public class BESTQuery {
	private ArrayList<String> keyword;
	private String filterObjectName;
	private String filterQueryName;
	private int topN;

	/**
	 * The constructor of BEST Query.
	 * 
	 * @param keyword
	 *            keyword list
	 * @param filterObjectName
	 *            Filter which BEST can provide. If you want 'All object', Put
	 *            Null in this parameter.
	 * @param topN
	 *            The number of results.
	 */
	public BESTQuery(ArrayList<String> keyword, String filterObjectName,
			int topN) {
		this.keyword = keyword;
		this.filterObjectName = filterObjectName;
		this.topN = topN;
	}

	/**
	 * The constructor of BEST Query.
	 * 
	 * @param keyword
	 *            keyword list
	 * @param filterObjectName
	 *            Filter which BEST can provide. If you want 'All object', Put
	 *            Null in this parameter.
	 * @param filterQueryName
	 *            If you want to see the result about only one entity, Put the
	 *            name of the entity. Don't put a wrong entity name in this
	 *            field or you'l get no result.
	 */
	public BESTQuery(ArrayList<String> keyword, String filterObjectName,
			String filterQueryName) {
		this(keyword, filterObjectName, 1);
		this.filterQueryName = filterQueryName;
	}

	/**
	 * The constructor of BEST Query.
	 * 
	 * @param keyword
	 *            keyword list
	 * @param filterObjectName
	 *            Filter which BEST can provide. If you want 'All object', Put
	 *            Null in this parameter.
	 * @param topN
	 *            The number of results.
	 */
	public BESTQuery(String[] keyword, String filterObjectName, int topN) {
		this.keyword = new ArrayList<String>();
		for (String k : keyword) {
			this.keyword.add(k);
		}
		this.filterObjectName = filterObjectName;
		this.topN = topN;
	}

	/**
	 * The constructor of BEST Query.
	 * 
	 * @param keyword
	 *            keyword list
	 * @param filterObjectName
	 *            Filter which BEST can provide. If you want 'All object', Put
	 *            Null in this parameter.
	 * @param filterQueryName
	 *            If you want to see the result about only one entity, Put the
	 *            name of the entity. Don't put a wrong entity name in this
	 *            field or you'l get no result.
	 */
	public BESTQuery(String[] keyword, String filterObjectName,
			String filterQueryName) {
		this(keyword, filterObjectName, 1);
		this.filterQueryName = filterQueryName;
	}

	/**
	 * The constructor of BEST Query.
	 * 
	 * @param keyword
	 *            keyword list
	 * @param filterObjectName
	 *            Filter which BEST can provide. If you want 'All object', Put
	 *            Null in this parameter.
	 * @param topN
	 *            The number of results.
	 */
	public BESTQuery(String keyword, String filterObjectName, int topN) {
		this.keyword = new ArrayList<String>();
		this.keyword.add(keyword);
		this.filterObjectName = filterObjectName;
		this.topN = topN;
	}

	/**
	 * The constructor of BEST Query.
	 * 
	 * @param keyword
	 *            keyword list
	 * @param filterObjectName
	 *            Filter which BEST can provide. If you want 'All object', Put
	 *            Null in this parameter.
	 * @param filterQueryName
	 *            If you want to see the result about only one entity, Put the
	 *            name of the entity. Don't put a wrong entity name in this
	 *            field or you'l get no result.
	 */
	public BESTQuery(String keyword, String filterObjectName,
			String filterQueryName) {
		this(keyword, filterObjectName, 1);
		this.filterQueryName = filterQueryName;
	}

	/**
	 * Adding a keyword to the keyword list
	 * 
	 * @param keyWord
	 *            String type object, which you want to add to keyword list
	 */
	public void addKeyword(String keyWord) {
		this.keyword.add(keyWord);
	}

	/**
	 * Getting keyword
	 * 
	 * @return Keyword list
	 */
	public ArrayList<String> getKeyword() {
		return keyword;
	}

	/**
	 * Getting filter object name
	 * 
	 * @return FilterObjectName
	 */
	public String getFilterObjectName() {
		return filterObjectName;
	}

	/**
	 * Getting the number of results retrieved by query
	 * 
	 * @return the number of results to be retrieved
	 */
	public int getTopN() {
		return topN;
	}

	/**
	 * Removing a keyword from the primary keyword list
	 * 
	 * @param keyword
	 *            the keyword to be removed from the primary keyword list
	 */
	public void removeKeyword(String keyword) {
		this.keyword.remove(keyword);
	}

	/**
	 * Setting the keywords
	 * 
	 * @param keyword
	 *            Must be a list of String
	 */
	public void setKeyword(ArrayList<String> keyword) {
		this.keyword = keyword;
	}

	/**
	 * Setting the filtering object.
	 * 
	 * @param filterObjectName
	 *            Filtering object name which BEST can provide.
	 */
	public void setFilterObjectName(String filterObjectName) {
		this.filterObjectName = filterObjectName;
	}

	/**
	 * Setting the number of results retrieved by BEST
	 * 
	 * @param topN
	 *            The number of results retrieved by BEST
	 */
	public void setTopN(int topN) {
		this.topN = topN;
	}

	/**
	 * Getting the current filterQuery
	 * 
	 * @return filterQueryName
	 */
	public String getFilterQueryName() {
		return filterQueryName;
	}

}
