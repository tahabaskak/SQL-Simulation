import java.io.*;
import java.util.*;

public class assigment2 {

	public static void main(String[] args) {
		
		String dataName = args[0];
		String commandText = args[1];
		String lineData=null,lineCommand;
		ArrayList<information> values = new ArrayList<information>();
		/*ArrayList<String> values = new ArrayList<String>();
		values.addAll(new ArrayList<String>());*/
		try(BufferedReader brData = new BufferedReader(new FileReader(dataName))){		//Read data and save system.
			lineData =brData.readLine();
			while((lineData= brData.readLine()) != null){
				String[] splitLineData = lineData.split("\\|");
				information temp = new information();
				temp.setCID(Integer.parseInt(splitLineData[0]));
				temp.setFirstName(splitLineData[1]);
				temp.setLastName(splitLineData[2]);
				temp.setCity(splitLineData[3]);
				temp.setAdressLane(splitLineData[4]);
				temp.setSocialNumber(Integer.parseInt(splitLineData[5]));
				values.add(temp);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		try(BufferedReader brCommand = new BufferedReader(new FileReader(commandText))){
			while((lineCommand = brCommand.readLine()) != null){
				String[] line = lineCommand.split("WHERE");
				String selectPart = line[0].replaceAll("SELECT", "");
				String[] selectColmList = selectPart.split(",");
				String wherePart = line[1].replaceAll("WHERE", "");
				String[] whereClauseList = wherePart.split("AND");
				boolean isError = false;
				String errorMessage = "";
				long startTime =System.currentTimeMillis();
				ArrayList<information> resultList = values;
				for(int i = 0; i < whereClauseList.length;i++){			//Split for Command Text after WHERE
					String tempCondition = whereClauseList[i];
					String splitOp = "";
					boolean isNumeric = true;
					if(tempCondition.contains(">")){
						splitOp= ">";	
					} else if(tempCondition.contains("<")){
						splitOp= "<";
					} else if(tempCondition.contains("~")){
						splitOp= "~";
						isNumeric = false;
					}else if(tempCondition.contains("=")){
						splitOp= "=";
					}
					
					String[] conditionRules = tempCondition.split(splitOp);
					
					boolean stringIsNumeric = false;
					int searchValue = 0;
					try{
						String changingValue = conditionRules[1].trim();
						searchValue = Integer.parseInt(changingValue);
						stringIsNumeric = true;
					}catch(NumberFormatException nfe){
						stringIsNumeric = false;
					}
					if(conditionRules[0].trim().equals("AddressLine1")){
						stringIsNumeric = false;
					}
					
					if(isNumeric && !stringIsNumeric ){				// Finding numeric or not numeric
						isError = true;
						break;
					}
					else if(!isNumeric && stringIsNumeric){
						isError = true;
						break;
					}
					
					if(splitOp.equals("~"))
					{
						resultList = SearchContainsInArray(resultList, conditionRules[0].trim(),conditionRules[1].trim());
					}
					else if(splitOp.equals("<")){
						resultList =  SearchInArrayLower(resultList, conditionRules[0].trim(),searchValue);
					}
					else if(splitOp.equals(">")){
						resultList =  SearchInArrayBigger(resultList, conditionRules[0].trim(),searchValue);
					}
					else if(splitOp.equals("=")){
						resultList =  SearchInArrayEqual(resultList, conditionRules[0].trim(),searchValue);
					}
				
				}
				long endTime = System.currentTimeMillis();
				long totalTime = endTime - startTime;
				PrintResult(resultList, selectColmList,lineCommand,totalTime);		// for print go function
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private static ArrayList<information> SearchInArrayLower(ArrayList<information> sourceList, String colmName, int searchValue){	//Searching < for CID and SSN
		ArrayList<information> resultList = new ArrayList<information>();
		if(colmName.equals("CID")){
			quickSortForArrayCID(sourceList,0,(sourceList.size()-1));
			int localPosition = -1;
			for(int i = searchValue; i> 0; i--){
				localPosition = binarySearchForCID(sourceList,0,(sourceList.size()-1),searchValue);
				if(localPosition != -1){
					break;
				}
			}
			for(int i = 0 ; i < localPosition; i++){
					resultList.add(sourceList.get(i));
			}
		}
		else if(colmName.equals("SocialSecurityNumber")){
			quickSortForArraySSN(sourceList,0,(sourceList.size()-1));
			int localPosition = -1;
			for(int i = searchValue; i> 0; i--){
			localPosition = binarySearchForSSN(sourceList,0,(sourceList.size()-1),i);
				if(localPosition != -1){
					break;
				}
			}
			for(int i = 0 ; i < localPosition; i++){
				resultList.add(sourceList.get(i));
			}
		}
		
		return resultList;
	}
	
	private static ArrayList<information> SearchInArrayEqual(ArrayList<information> sourceList, String colmName, int searchValue){	//Searching = for CID and SSN
		ArrayList<information> resultList = new ArrayList<information>();
		if(colmName.equals("CID")){
			quickSortForArrayCID(sourceList,0,(sourceList.size()-1));
			int localPosition = -1;
			for(int i = searchValue; i> 0; i--){
				localPosition = binarySearchForCID(sourceList,0,(sourceList.size()-1),searchValue);
				if(localPosition != -1){
					break;
				}
			}
			resultList.add(sourceList.get(localPosition));
		}
		else if(colmName.equals("SocialSecurityNumber")){
			quickSortForArraySSN(sourceList,0,(sourceList.size()-1));
			int localPosition = -1;
			for(int i = searchValue; i> 0; i--){
			localPosition = binarySearchForSSN(sourceList,0,(sourceList.size()-1),i);
				if(localPosition != -1){
					break;
				}
			}
				resultList.add(sourceList.get(localPosition));
		}
		return resultList;
	}
	
	private static ArrayList<information> SearchInArrayBigger(ArrayList<information> sourceList, String colmName, int searchValue){		//Searching > for CID and SSN
		ArrayList<information> resultList = new ArrayList<information>();
		if(colmName.equals("CID")){
			quickSortForArrayCID(sourceList,0,(sourceList.size()-1));
			int localPosition = -1;
			for(int i = searchValue; i> 0; i--){
				localPosition = binarySearchForCID(sourceList,0,(sourceList.size()-1),searchValue);
				if(localPosition != -1){
					break;
				}
			}
			
			for(int i = 0 ; i > localPosition; i++){
					resultList.add(sourceList.get(i));
			}
		}
		else if(colmName.equals("SocialSecurityNumber")){
			quickSortForArraySSN(sourceList,0,(sourceList.size()-1));
			int localPosition = -1;
			for(int i = searchValue; i> 0; i--){
			localPosition = binarySearchForSSN(sourceList,0,(sourceList.size()-1),i);
				if(localPosition != -1){
					break;
				}
			}

			for(int i = localPosition ; i < sourceList.size(); i++){
				resultList.add(sourceList.get(i));
			}
		}
		return resultList;
	}
	
	private static ArrayList<information> SearchContainsInArray(ArrayList<information> sourceList, String colmName, String searchValue){	//Searching ~ for City-Firstname-Lastname-AddressLine
		ArrayList<information>  resultList = new  ArrayList<information> ();
		if(colmName.equals("FirstName")){
			quickSortForArrayFN(sourceList,0,(sourceList.size()-1));
			int localPosition = -1;
			localPosition = binarySearchForFN(sourceList,0,(sourceList.size()-1),searchValue);
			for(int i = 0 ; i < localPosition; i++){
				if(sourceList.get(i).getFirstName().toLowerCase().startsWith(searchValue.toLowerCase()))
					resultList.add(sourceList.get(i));		
			}
		} else if(colmName.equals("LastName")){
			quickSortForArrayLN(sourceList,0,(sourceList.size()-1));
			int localPosition = -1;
			localPosition = binarySearchForLN(sourceList,0,(sourceList.size()-1),searchValue);
			for(int i = 0 ; i < localPosition; i++){
				if(sourceList.get(i).getLastName().toLowerCase().startsWith(searchValue.toLowerCase()))
					resultList.add(sourceList.get(i));		
			}
		} else if(colmName.equals("City")){
			quickSortForArrayCity(sourceList,0,(sourceList.size()-1));
			int localPosition = -1;
			localPosition = binarySearchForCity(sourceList,0,(sourceList.size()-1),searchValue);
			for(int i = 0 ; i < localPosition; i++){
				if(sourceList.get(i).getCity().toLowerCase().startsWith(searchValue.toLowerCase()))
					resultList.add(sourceList.get(i));		
			}
		} else if(colmName.equals("AddressLine1")){
			quickSortForArrayAddressLine(sourceList,0,(sourceList.size()-1));
			int localPosition = -1;
			localPosition = binarySearchForAddressLine(sourceList,0,(sourceList.size()-1),searchValue);
			for(int i = 0 ; i < localPosition; i++){
				if(sourceList.get(i).getAdressLane().toLowerCase().startsWith(searchValue.toLowerCase()))
					resultList.add(sourceList.get(i));		
			}
		}
		return resultList;
	}
	
	private static void PrintResult (ArrayList<information> resultList, String[] printColms,String commandLine,long totalTime){ 		// Create output text and write
		try {
			PrintWriter writer = new PrintWriter("output.txt");
			writer.println("CommandText :"+ "\"" + commandLine + "\"\n" + "\n" + "Result:");
			if(resultList.size() == 0){
				writer.println("Empty rowset");
			}else{
				for(String item : printColms)
					writer.println(item.trim() + "\t\t");
				writer.println("");
				for(information item : resultList){
					//System.out.println(indexOfArray(printColms, "FirstName"));
					writer.println( 
							(indexOfArray(printColms, "CID") == -1 ? "" : (item.getCID()) + "\t\t" )+
									(indexOfArray(printColms, "FirstName") == -1 ? "" : (item.getFirstName()) + "\t\t") +
									(indexOfArray(printColms, "LastName") == -1 ? "" :( item.getLastName()) + "\t\t") +
									(indexOfArray(printColms, "City") == -1 ? "" :( item.getCity()) + "\t\t" )+
									(indexOfArray(printColms, "AddressLine1") == -1 ? "" : (item.getAdressLane()) + "\t\t") +
									(indexOfArray(printColms, "SocialSecurityNumber") == -1 ? "" : item.getSocialNumber()));
				
				}
				writer.println("-----------------------");
				writer.println("Process time: " + totalTime +"milliseconds");
				
			}
			
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static int indexOfArray(String[] array, String key) {
	    int returnvalue = -1;
	    for (int i = 0; i < array.length; ++i) {
	        if (key.equals(array[i].trim())) {
	            returnvalue = i;
	            break;
	        }
	    }
	    return returnvalue;
	}

	private static void quickSortForArrayCID(ArrayList<information> sourceList,int low,int high){			//Quick sort For CID Number
		if(sourceList == null && sourceList.size() == 0)
			return;
		if(low>=high)
			return;
		int mid = low + (high-low)/2;
		int pivot = sourceList.get(mid).getCID();
		int i =low , j= high;
		while(i<=j){
			while(sourceList.get(i).getCID() < pivot)
				i++;
			while(sourceList.get(j).getCID() > pivot)
				j--;
			if(i<= j){
				information temp = sourceList.get(i);
				sourceList.set(i, sourceList.get(j));
				sourceList.set(j, temp);
				i++;
				j--;
			}
		}
		if(low<j)
			quickSortForArrayCID(sourceList,low,j);
		if(high> i)
			quickSortForArrayCID(sourceList,i,high);
	}
	
	private static void quickSortForArraySSN(ArrayList<information> sourceList,int low,int high){			//Quick sort For SecuritySocialNumber
		if(sourceList == null && sourceList.size() == 0)
			return;
		if(low>=high)
			return;
		int mid = low + (high-low)/2;
		int pivot = sourceList.get(mid).getSocialNumber();
		int i =low , j= high;
		while(i<=j){
			while(sourceList.get(i).getSocialNumber() < pivot)
				i++;
			while(sourceList.get(j).getSocialNumber() > pivot)
				j--;
			if(i<= j){
				information temp = sourceList.get(i);
				sourceList.set(i, sourceList.get(j));
				sourceList.set(j, temp);
				i++;
				j--;
			}
		}
		if(low<j)
			quickSortForArraySSN(sourceList,low,j);
		if(high> i)
			quickSortForArraySSN(sourceList,i,high);
	}
	
	private static void quickSortForArrayFN(ArrayList<information> sourceList,int low,int high){			//Quick sort For First Name
		if(sourceList == null && sourceList.size() == 0)
			return;
		if(low>=high)
			return;
		int mid = low + (high-low)/2;
		String pivot = sourceList.get(mid).getFirstName();
		int i =low , j= high;
		while(i<=j){
			while(sourceList.get(i).getFirstName().compareTo(pivot) < 0)
				i++;
			while(sourceList.get(j).getFirstName().compareTo(pivot) > 0)
				j--;
			if(i<= j){
				information temp = sourceList.get(i);
				sourceList.set(i, sourceList.get(j));
				sourceList.set(j, temp);
				i++;
				j--;
			}
		}
		if(low<j)
			quickSortForArrayFN(sourceList,low,j);
		if(high> i)
			quickSortForArrayFN(sourceList,i,high);
	}
		
	private static void quickSortForArrayLN(ArrayList<information> sourceList,int low,int high){			//Quick sort For Last Name
		if(sourceList == null && sourceList.size() == 0)
			return;
		if(low>=high)
			return;
		int mid = low + (high-low)/2;
		String pivot = sourceList.get(mid).getLastName();
		int i =low , j= high;
		while(i<=j){
			while(sourceList.get(i).getLastName().compareTo(pivot) < 0)
				i++;
			while(sourceList.get(j).getLastName().compareTo(pivot) > 0)
				j--;
			if(i<= j){
				information temp = sourceList.get(i);
				sourceList.set(i, sourceList.get(j));
				sourceList.set(j, temp);
				i++;
				j--;
			}
		}
		if(low<j)
			quickSortForArrayLN(sourceList,low,j);
		if(high> i)
			quickSortForArrayLN(sourceList,i,high);
	}
	
	private static void quickSortForArrayCity(ArrayList<information> sourceList,int low,int high){			//Quick sort For City Name
		if(sourceList == null && sourceList.size() == 0)
			return;
		if(low>=high)
			return;
		int mid = low + (high-low)/2;
		String pivot = sourceList.get(mid).getCity();
		int i =low , j= high;
		while(i<=j){
			while(sourceList.get(i).getCity().compareTo(pivot) < 0)
				i++;
			while(sourceList.get(j).getCity().compareTo(pivot) > 0)
				j--;
			if(i<= j){
				information temp = sourceList.get(i);
				sourceList.set(i, sourceList.get(j));
				sourceList.set(j, temp);
				i++;
				j--;
			}
		}
		if(low<j)
			quickSortForArrayCity(sourceList,low,j);
		if(high> i)
			quickSortForArrayCity(sourceList,i,high);
	}
	
	private static void quickSortForArrayAddressLine(ArrayList<information> sourceList,int low,int high){			//Quick sort For Address Line
		if(sourceList == null && sourceList.size() == 0)
			return;
		if(low>=high)
			return;
		int mid = low + (high-low)/2;
		String pivot = sourceList.get(mid).getAdressLane();
		int i =low , j= high;
		while(i<=j){
			while(sourceList.get(i).getAdressLane().compareTo(pivot) < 0)
				i++;
			while(sourceList.get(j).getAdressLane().compareTo(pivot) > 0)
				j--;
			if(i<= j){
				information temp = sourceList.get(i);
				sourceList.set(i, sourceList.get(j));
				sourceList.set(j, temp);
				i++;
				j--;
			}
		}
		if(low<j)
			quickSortForArrayAddressLine(sourceList,low,j);
		if(high> i)
			quickSortForArrayAddressLine(sourceList,i,high);
	}

	private static int binarySearchForCID(ArrayList<information> sourceList,int first,int last,int searchValue){	//Binary search for CID
		
		if(first>last)
			return -1;
		int mid = (first + last)/2;
		if(sourceList.get(mid).getCID() == searchValue){
			return mid;
		}else if(sourceList.get(mid).getCID() < searchValue){
			return binarySearchForCID(sourceList,mid+1,last,searchValue);
		}else {
			return binarySearchForCID(sourceList,first,mid,searchValue);
		}
	}
	
	private static int binarySearchForSSN(ArrayList<information> sourceList,int first,int last,int searchValue){	//Binary search for SecuritySocialNumber
		if(first>=last)
			return -1;
		int mid = (first + last)/2;
		if(sourceList.get(mid).getSocialNumber() == searchValue){
			return mid;
		}else if(sourceList.get(mid).getSocialNumber() < searchValue){
			return binarySearchForSSN(sourceList,mid+1,last,searchValue);
		}else {
			return binarySearchForSSN(sourceList,first,mid,searchValue);
		}
	}
	
	private static int binarySearchForFN(ArrayList<information> sourceList,int first,int last,String searchValue){	//Binary search for FirstName
		if(first>=last)
			return -1;
		int mid = (first + last)/2;
		if(sourceList.get(mid).getFirstName().toLowerCase().startsWith(searchValue.toLowerCase())){
			return mid;
		}else if(sourceList.get(mid).getFirstName().toLowerCase().compareTo(searchValue.toLowerCase()) < 0){
			return binarySearchForFN(sourceList,mid+1,last,searchValue);
		}else {
			return binarySearchForFN(sourceList,first,mid,searchValue);
		}
	}
	
	private static int binarySearchForLN(ArrayList<information> sourceList,int first,int last,String searchValue){	//Binary search for LastName
		if(first>=last)
			return -1;
		int mid = (first + last)/2;
		if(sourceList.get(mid).getLastName().toLowerCase().startsWith(searchValue.toLowerCase())){
			return mid;
		}else if(sourceList.get(mid).getLastName().toLowerCase().compareTo(searchValue.toLowerCase()) < 0){
			return binarySearchForLN(sourceList,mid+1,last,searchValue);
		}else {
			return binarySearchForLN(sourceList,first,mid,searchValue);
		}
	}
	
	private static int binarySearchForCity(ArrayList<information> sourceList,int first,int last,String searchValue){	//Binary search for City
		if(first>=last)
			return -1;
		int mid = (first + last)/2;
		if(sourceList.get(mid).getCity().toLowerCase().startsWith(searchValue.toLowerCase())){
			return mid;
		}else if(sourceList.get(mid).getCity().toLowerCase().compareTo(searchValue.toLowerCase()) < 0){
			return binarySearchForCity(sourceList,mid+1,last,searchValue);
		}else {
			return binarySearchForCity(sourceList,first,mid,searchValue);
		}
	}
	
	private static int binarySearchForAddressLine(ArrayList<information> sourceList,int first,int last,String searchValue){	//Binary search for AddressLine
		if(first>=last)
			return -1;
		int mid = (first + last)/2;
		if(sourceList.get(mid).getAdressLane().toLowerCase().startsWith(searchValue.toLowerCase())){
			return mid;
		}else if(sourceList.get(mid).getAdressLane().toLowerCase().compareTo(searchValue.toLowerCase()) < 0){
			return binarySearchForAddressLine(sourceList,mid+1,last,searchValue);
		}else {
			return binarySearchForAddressLine(sourceList,first,mid,searchValue);
		}
	}
}