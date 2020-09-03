import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class Apriori {
	static double minSupp;
	static double confidence;
	static BufferedReader br;
	static ArrayList<ArrayList<String>> list;
	static int rowCount;
	static HashMap<List<String>, Integer> items1HM;
	static HashMap<List<String>,Integer> associationHM;
	static int fis = 0;
	public static void main(String[] args) throws IOException {
		
		getDataFromFile();
		
		associationHM = new HashMap<List<String>, Integer>();
		
		//Display data - TEMPORARY - Remove
		System.out.println("DataSet :");
		for(int i=0;i<list.size();i++)
		{
			for(int j=0;j<list.get(i).size();j++)
			{
				System.out.print(list.get(i).get(j)+" ");
			}
			System.out.println();
		}
		System.out.println();
		long startTime = System.nanoTime();
		
		//Start - Frequent Item sets
		HashMap<List<String>, Integer> itemsHM = new HashMap<List<String>,Integer>();
		ArrayList<String> itemSets;
		for(int i=0;i<list.size();i++)
		{
			for(int j=0;j<list.get(i).size();j++)
			{
				itemSets = new ArrayList<String>(Arrays.asList(list.get(i).get(j)));
				if(itemsHM.containsKey(itemSets))
				{
					itemsHM.put(itemSets, itemsHM.get(itemSets)+1);
				}
				else
				{
					itemsHM.put(itemSets, 1);
				}
			}
		}
		//System.out.println("Frequent itemset count "+itemsHM);
		
		//Calculate support and update table
		ArrayList<List<String>> itemsHMKeySets = new ArrayList<List<String>>(itemsHM.keySet());
		ArrayList<List<String>> itemsToRemove = new ArrayList<List<String>>();
		for(int i=0;i<itemsHM.size();i++)
		{
			List<String> key = itemsHMKeySets.get(i);
			int value = itemsHM.get(key);
			
			double itemSup = ((double)value)/((double)rowCount);
			
			if (itemSup < minSupp)
			{
				//items.remove(key);
				itemsToRemove.add(key);
			}
		}
		//System.out.println("Before removal "+items); //Remove
		removeItems(itemsToRemove,itemsHM);
		
		itemsHMKeySets = new ArrayList<List<String>>(itemsHM.keySet());
		System.out.println("Generating Frequent Item...");
		System.out.println("Frequent Itemsets "+(++fis)+" "+itemsHM);
		System.out.println();
		associationHM.putAll(itemsHM);
		//End - Frequent Item sets
		
		if(!itemsHM.isEmpty())
		{
			frequentItemSets(itemsHMKeySets);
			generateAssociationRules(associationHM,rowCount);
			long endTime = System.nanoTime();
			System.out.println("Took "+(endTime - startTime) + " ns"); 
		}
		else
		{
			System.out.println("No items");
		}
	}

	private static void generateAssociationRules(HashMap<List<String>, Integer> associationHM, int rowCount) 
	{
		DecimalFormat df = new DecimalFormat("0.00");
		System.out.println();
		System.out.println("Calculating Confidence...");
		ArrayList<List<String>> associationHMKeySets = new ArrayList<List<String>>(associationHM.keySet());
		List<String> item;
		List<String> itemPair;
		double result;
		
		for(int i=0;i<associationHM.size();i++)
		{
			double itemCount = 0;

			if(associationHMKeySets.get(i).size()>1)
			{
				double support = associationHM.get(associationHMKeySets.get(i));
				item = new ArrayList<String>();
				for(int j=0;j<associationHMKeySets.get(i).size();j++)
				{
					//itemPair = new ArrayList<String>();
					for(int k=0;k<associationHMKeySets.get(i).size();k++)
					{	
						//System.out.print(associationHMKeySets.get(i).get(j));
						if(associationHMKeySets.get(i).get(j)!=associationHMKeySets.get(i).get(k))
						{
							item.add(associationHMKeySets.get(i).get(k));
							//System.out.print(" --> "+associationHMKeySets.get(i).get(k));
						}
					}
					//System.out.print(item+" --> ["+associationHMKeySets.get(i).get(j)+"]");
					itemCount = associationHM.get(item);
					/* START Calculate Confidence */
					result = (support/rowCount)/(itemCount/rowCount);
					if(confidence<=result)
					{
						System.out.println(item+" --> ["+associationHMKeySets.get(i).get(j)+"] : "+df.format(result*100)+"%");
					}
					//System.out.println(" : "+df.format(result*100)+"%");
					/* END Calculate Confidence */

					if(associationHMKeySets.get(i).size()>2)
					{
						itemPair = new ArrayList<String>(item);
//						System.out.print("["+associationHMKeySets.get(i).get(j)+"] --> "+item);
						item.clear();
						item.add(associationHMKeySets.get(i).get(j));
						itemCount = associationHM.get(item);
						/* START Calculate Confidence */
						result = (support/rowCount)/(itemCount/rowCount);
						if(confidence<=result)
						{
							System.out.println("["+associationHMKeySets.get(i).get(j)+"] --> "+itemPair+" : "+df.format(result*100)+"%");
						}
						//System.out.println(" : "+df.format(result*100)+"%");
//						System.out.println();
						/* END Calculate Confidence */
					}
					item.clear();
				}
				//System.out.println();
			}
		}
	}

	public static void frequentItemSets(ArrayList<List<String>> itemsHMKeySets) 
	{
		//Start - Calculate support and update table
		if(!(itemsHMKeySets.size()==1 || itemsHMKeySets.isEmpty()))
		{
			items1HM = new HashMap<List<String>,Integer>();
			ArrayList<String> itemSets1 = null;// = new ArrayList<String>();
			ArrayList<String> sourceList = null;// = new ArrayList<String>();
			ArrayList<String> destinationList;// = new ArrayList<String>();
			ArrayList<List<String>> itemsToRemove1 = new ArrayList<List<String>>();
			Set<String> removeDuplicates;
			ArrayList<String> uniqueItemSets = null;
			ArrayList<List<String>> items1HMKeySets;
			
			for(int i=0;i<itemsHMKeySets.size();i++)
			{
				for(int j=i+1;j<itemsHMKeySets.size();j++)
				{
					int temp=0;
					itemSets1 = new ArrayList<String>();
					for(int k=0;k<itemsHMKeySets.get(i).size();k++)
					{
						//itemSets1 = Arrays.asList(itemsHMKeySets.get(i),itemsHMKeySets.get(j));
						itemSets1.add(itemsHMKeySets.get(i).get(k));
						itemSets1.add(itemsHMKeySets.get(j).get(k));
					}
					Collections.sort(itemSets1);
					//System.out.println("itemSets1 : "+itemSets1);
					removeDuplicates = new LinkedHashSet<>(itemSets1);
					uniqueItemSets = new ArrayList<String>(removeDuplicates);
					
					if(items1HM.containsKey(uniqueItemSets))
					{
						continue;
					}
					while(temp!=rowCount)
					{
						//Inserting in HashMap
						removeDuplicates = new LinkedHashSet<>(itemSets1);
						uniqueItemSets = new ArrayList<String>(removeDuplicates);
						sourceList = uniqueItemSets;
						//System.out.println("sourceList "+sourceList);
						destinationList = new ArrayList<String>(list.get(temp));
						//System.out.println("destinationList "+destinationList);
						
						if(!sourceList.retainAll(destinationList))
						{
							if(items1HM.containsKey(uniqueItemSets))
							{
								items1HM.put(uniqueItemSets, items1HM.get(uniqueItemSets)+1);
							}
							else
							{
								items1HM.put(uniqueItemSets,1);
							}
						}
						temp++;
					}
				}
			}
			//System.out.println("Frequent itemset count "+items1HM);
					
			/* Remove items below minimum support */
			items1HMKeySets = new ArrayList<List<String>>(items1HM.keySet());
			for(int i=0;i<items1HMKeySets.size();i++)
			{
				List<String> key = items1HMKeySets.get(i);
				int value = items1HM.get(key);
				
				double itemSup = ((double)value)/((double)rowCount);
				
				if (itemSup < minSupp)
				{
					itemsToRemove1.add(key);
				}
			}
			
			removeItems(itemsToRemove1,items1HM);
			if(!items1HM.isEmpty())
			{
				items1HMKeySets = new ArrayList<List<String>>(items1HM.keySet());
				System.out.println("Frequent Itemsets "+(++fis)+" "+items1HM);
				System.out.println();
				associationHM.putAll(items1HM);
				frequentItemSets(items1HMKeySets);
			}
			
		}
		else
		{
			System.out.println("Frequent Itemsets Completed!");
		}
		//End - Calculate support and update table
	}

	public static void getDataFromFile() throws IOException {
		//Start - Get Data
				Scanner sc = new Scanner(System.in);
				System.out.print("Provide minimum support in % : ");
				
				minSupp = (sc.nextDouble())/100;
				System.out.println("Minimum Support : "+minSupp);
				System.out.println();
				while(minSupp<=0 || minSupp>1)
				{
					System.out.println("Please enter a value between 0 to 100!");
					minSupp = (sc.nextDouble())/100;
				}
				
				System.out.print("Provide Confidence in % : ");
				
				confidence = (sc.nextDouble())/100;
				System.out.println("Minimum Confidence : "+confidence);
				System.out.println();
				while(confidence<=0 || confidence>1)
				{
					System.out.println("Please enter a value between 0 to 100!");
					confidence = (sc.nextDouble())/100;
				}
				sc.close();
				
				br = new BufferedReader(new FileReader("/Users/akshaywaghela/eclipse-workspace/Apriori/Dataset1.txt"));
				String[] line = null;
				rowCount = 0;
				
				/*
				 * list = {	{       },
				 * 			{       },
				 * 			{       }}
				 */
				list = new ArrayList<>();
				
				//Read the data from text file
				while(br.ready())
				{
					list.add(new ArrayList<>());
					line = br.readLine().split(" ");
					for(int j=0;j<line.length;j++)
					{
						list.get(rowCount).add(line[j].toUpperCase());
					}
					rowCount++;
				}
				//End - Get Data
	}

	public static void removeItems(ArrayList<List<String>> itemsToRemove, HashMap<List<String>, Integer> itemsHM) {
		for(int i=0;i<itemsToRemove.size();i++)
		{
			itemsHM.remove(itemsToRemove.get(i));
		}
	}
}
