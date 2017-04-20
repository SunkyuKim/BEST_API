package best_api;

import java.util.ArrayList;

/**
 * This contains the search result which is retrieved by BEST. 
 * You can get result values with 'getter' method
 */
public class BESTResult {
	private int rank;
	private String name;
	private double score;
	private int numOfDoc;
	private String oid;
	private ArrayList<String> abstracts;
	
	BESTResult(String rank, String name, String score, String numOfDoc, String oid){
		this.rank = Integer.parseInt(rank);
		this.name = name;
		this.score = Double.parseDouble(score);
		this.numOfDoc = Integer.parseInt(numOfDoc);
		this.oid = oid;
		this.abstracts = new ArrayList<String>();
	}
	
	/**
	 * Getting the rank of this entity
	 * @return integer rank value
	 */
	public int getRank() {
		return rank;
	}
	/**
	 * Getting the name of this entity
	 * @return String name of this entity
	 */
	public String getName() {
		return name;
	}
	/**
	 * Getting the score of this entity, which is used for ranking
	 * @return double score value
	 */
	public double getScore() {
		return score;
	}
	/**
	 * Getting the number of documents where the query and this entity appear together
	 * @return the number of Document
	 */
	public int getNumOfDoc() {
		return numOfDoc;
	}
	/**
	 * Getting the abstracts where the query and this entity appear together
	 * @return List of documents
	 */
	public ArrayList<String> getAbstracts() {
		return abstracts;
	}
	void addAbstracts(String abs){
		abstracts.add(abs);
	}

	public String getOid() {
		return oid;
	}
	
}
