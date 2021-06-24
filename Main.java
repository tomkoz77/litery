package Letters;

import java.time.*;
import java.util.*;
import java.util.stream.*;

public class Main {

	public static void main(String[] args) {
		
		int numberOfLetters = 0;
		String sourceWord = null, targetWord = null;
					
		System.out.println("Podaj w kolejnych wierszach: liczbę liter w nazwisku, pierwsze nazwisko oraz drugie nazwisko");

		try (Scanner scanner = new Scanner(System.in);)
		{
			numberOfLetters = scanner.nextInt();
			sourceWord = scanner.next();
			targetWord = scanner.next();
		} 
		catch(InputMismatchException e)
		{
			System.err.println("W pierwszym wierszu należy podać liczbę całkowitą od 2 do 1 000 000");
			return;
		}
	
		if (numberOfLetters != sourceWord.length() || sourceWord.length() != targetWord.length() || numberOfLetters < 2 || numberOfLetters > 1_000_000)
		{
			System.err.println("Wprowadziłeś niepoprawne dane");
			return;
		}
		
		Instant start = Instant.now();
		
		long result = getNumberOfSwaps(new NLogNAlgorithm(), sourceWord, targetWord);
		
		Instant end = Instant.now();
		
		System.out.println("Minimalna liczba zmian liter to: " + result);
		System.out.println("Czas wykonania: " + Duration.between(start, end).toMillis() + " milisekund");

	}
	
	public static long getNumberOfSwaps(Strategy algorithm, String sourceWord, String targetWord){
		
		return algorithm.getNumberOfSwaps(sourceWord, targetWord);
	}

}

interface Strategy{
	public long getNumberOfSwaps(String sourceWord, String targetWord);
}

class NLogNAlgorithm implements Strategy{
	
	public long getNumberOfSwaps(String sourceWord, String targetWord){
		
		long result = 0L;
		
		char[] targetWordTab = targetWord.toCharArray();
		
		char[] sourceWordTab = sourceWord.toCharArray();
		Map<Character,ArrayDeque<Integer>> sourceWordPos = IntStream.range(0,sourceWord.length())
									    .mapToObj(x->Integer.valueOf(x))
									    .collect(Collectors.groupingBy(i->sourceWordTab[i], Collectors.toCollection(ArrayDeque::new)));
		SortedIntegerList indexes = new SortedIntegerList(targetWordTab.length);
		
		int index = 0, position = 0;
		
		for(char letter: targetWordTab)
		{
			index = sourceWordPos.get(letter).poll();
			position = indexes.add(index);
			result += index - position;
		}
		
		return result;
	};
}

class SortedIntegerList{
	
	public static final int LIST_CAPACITY = 10_000;
	
	private List<List<Integer>> collectionOfLists;	
	
	private int numberOfLists = 0;
	
	public SortedIntegerList(int number){
		
		 numberOfLists= number / LIST_CAPACITY + 1;
		 
		 collectionOfLists = new ArrayList<>();
		 
		 for (int i=0; i<numberOfLists;i++) 
			 collectionOfLists.add(new ArrayList<Integer>());
	}
	
	public int add(Integer x){
		int indexStart = 0 , 
			indexEnd,
			indexAdded = -1,
		    currentListNumber = 0;
		
		currentListNumber = x / LIST_CAPACITY;
		
		List<Integer> list = collectionOfLists.get(currentListNumber);
		indexEnd = list.size();
		
		do
		{
			if (indexEnd == indexStart)
			{
				list.add(indexEnd, x);
				indexAdded = indexEnd;
			}
			int index = indexStart + (indexEnd-indexStart)/2;
			
			if (x<list.get(index) )
				indexEnd = index;
			else 
				indexStart = index + 1;
			
		} while (indexAdded < 0);
		
		int sum = 0;
		for (int i=0;i<currentListNumber;i++) 
			sum += collectionOfLists.get(i).size();
		
		return sum + indexAdded;
	}
	
}

