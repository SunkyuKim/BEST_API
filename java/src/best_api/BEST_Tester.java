package best_api;

import best_api.BEST;
import best_api.BESTQuery;
import best_api.BESTResult;
import java.io.IOException;
import java.util.ArrayList;

class BEST_Tester {
	public static void main(String args[]) {
		
		//String[] cellLines = {"BT-20","CAL-51","HCC1187","HCC1806","HCC1937","HCC70","MDA-MB-231","MDA-MB-436","MDA-MB-453","M14","BT-549","CAMA-1","DU-4475","HCC1395","MDA-MB-157","MDA-MB-468","MFM-223","HCC1143","HCC38","CAL-148","HCC1428","Hs-578-T","BFTC-905","HT-1197","HT-1376","J82","SW780","T-24","TCCSUP","Calu-6","DMS-114","NCI-H1437","NCI-H1703","NCI-H1793","NCI-H2085","NCI-H2228","NCI-H23","NCI-H520","NCI-H522","HCC1500","MCF7","647-V","VCaP","UM-UC-3","22RV1","UACC-812","CAL-120","NCI-H1299","NCI-H1563","NCI-H226","SW900","COLO-205","HT-29","RKO","SW620","SW837","NCI-H747","SW948","HCT-116","SW48","NCI-H1975","NCI-H2291","NCI-H838","EVSA-T","HCC1419","HCC1569","MDA-MB-361","BT-474","MDA-MB-415","T47D","HCC1954","A549","Calu-3","NCI-H2170","NCI-H3122","NCI-H358","VM-CUB-1","KU-19-19","KATOIII","RT4","NCI-SNU-16","KMS-11","C32","LS-513","MDA-MB-175-VII"};
		String[] cellLines = {"BT 20"};
		
		for(String c : cellLines){
			tempFunction(c);
		}
	}
	
	public static void tempFunction(String cellLine){
		String[] keyword = {cellLine};
		BESTQuery query = new BESTQuery(keyword, "gene", 50);
		
		ArrayList<BESTResult> results = BEST.getRelevantBioEntities(query, false, true);

		System.out.print(cellLine);
		for (BESTResult result : results) {
			System.out.print("," + result.getName());
		}
		System.out.println();
		for (BESTResult result : results) {
			System.out.print("," + result.getScore());
		}
		System.out.println();
	}
	
	
	
	public static void main2(String args[]) throws IOException {
		

		if("a".equals("a"))
			System.out.println("a yes");
		
		if(" ".equals(" "))
			System.out.println("yes");
		
//		String[] keyword = { "dasatinib" };
//		String filterQuery = "chronic myeloid leukemia";
//		BESTQuery query = new BESTQuery(keyword, "gene", 50);
//		BESTQuery query_filter = new BESTQuery(keyword, null, filterQuery);
		//BEST.getDicObjectId("gata3");
//		System.out.println(BEST.getDicObjectId("Azacytidine"));
//		ArrayList<BESTResult> results = BEST.getRelevantBioEntities(query);
		
//		for(BESTResult r : results){
//			System.out.println(r.getName());
//		}
	}
}
