import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class ContextFreeGrammar {
	static String startVariable;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		ArrayList<ArrayList<String>> grammar = new ArrayList<ArrayList<String>>();
		ArrayList<String> nonTerminals = new ArrayList<String>();
		// read file
		File fileDir = new File("CFG.txt");
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
		int lineamount = 0;
		while (in.readLine() != null) { // Getting line amount
			lineamount++;
		}
		String str;
		String[] allfile = new String[lineamount];
		in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
		int w = 0;
		while ((str = in.readLine()) != null) {
			allfile[w] = str;
			w++;
		}
		System.out.println("Given Context Free Grammar");
		for (int j = 0; j < allfile.length; j++) {
			System.out.println(allfile[j]);
		}
		in.close();
		startVariable = Character.toString(allfile[0].charAt(0));
		// creating grammar from txt
		for (int j = 0; j < allfile.length; j++) {
			String nT = allfile[j].split(">")[0]; // non terminal
			String[] elements = allfile[j].split(">")[1].split("\\|");
			nonTerminals.add(nT);
			ArrayList<String> row = new ArrayList<String>() {
				{
					add(nT);
					for (String element : elements) {
						add(element);
					}
				}
			};
			grammar.add(row);
		}
		// first step - add E0 (start non terminal)
		ArrayList<String> row4 = new ArrayList<String>() {
			{
				String nTStart = Character.toString(allfile[0].charAt(0));
				add(nTStart.concat("0"));
				add(nTStart);
			}
		};
		;
		grammar.add(0, row4);
		
		System.out.println("\nAfter Adding S0");
		printGrammar(grammar);

		// Delete empty strings
		Boolean isEmptyStringAdded = false;
		for (int i = 1; i < grammar.size(); i++) { // Traversal of all grammer's rows
			ArrayList<String> row = grammar.get(i);// elements of each row
			// Find #'s
			for (int j = 1; j < row.size(); j++) { // traversal except non-terminals
				if (row.get(j).equals("#")) {// # check
					row.remove(j); // delete #
					for (int i2 = 1; i2 < grammar.size(); i2++) {// new rules are added instead of #'s
						ArrayList<String> newRow2 = grammar.get(i2);// elements of each row
						int newRow2Length = newRow2.size();
						for (int j2 = 1; j2 < newRow2Length; j2++) {// traversal except non-terminals
							String element = newRow2.get(j2);// element of non-terminals(E+T)
							if (element.contains(row.get(0))) {// if non-terminal(E, T ,F) exist
								String elementWithEmptyString = "";// creating new elements
								if (element.length() > 1) { // lowercases will remain same
									for (int k = 0; k < element.length(); k++) {
										if (element.charAt(k) != row.get(0).charAt(0)) {// creating new elements
											elementWithEmptyString = elementWithEmptyString
													.concat(Character.toString(element.charAt(k)));
										}
									}
									// each non-terminal should be unique
									if (elementWithEmptyString.length() > 0
											&& !newRow2.contains(elementWithEmptyString))
										newRow2.add(elementWithEmptyString);// adding new string to each row
								} else {
									if (element.equals(Character.toString(row.get(0).charAt(0)))
											&& !newRow2.contains("#")) {
										newRow2.add("#"); // preventing duplicate of #
										isEmptyStringAdded = true;
									}
								}
							}
						}
					}
				}
			}
			if (isEmptyStringAdded) {
				i = 0; // i will be 1 in the first column of the for
				isEmptyStringAdded = false;
			}
		}
		System.out.println("\nAfter Deleting Empty String");
		printGrammar(grammar);
		// Unit rule
		Boolean isARuleSettled = false;
		for (int i = 0; i < grammar.size(); i++) { // Traversal of all grammer's rows
			ArrayList<String> row = grammar.get(i); // elements of each row
			for (int j = 1; j < row.size(); j++) {
				String nonTerminalToBeSettled = row.get(j);
				if (nonTerminalToBeSettled.length() == 1 && nonTerminals.contains(nonTerminalToBeSettled)) {
					// if element is a nonterminal
					for (int i2 = 1; i2 < grammar.size(); i2++) {
						ArrayList<String> newRow2 = grammar.get(i2);// elments of each row
						if (newRow2.get(0).equals(nonTerminalToBeSettled)) {
							row.remove(j); // delete S
							for (int j2 = 1; j2 < newRow2.size(); j2++) {
								if (!row.contains(newRow2.get(j2))) // row should be unique
									row.add(newRow2.get(j2)); // add the right part of the nonterminals (ex A)
							}
							isARuleSettled = true;
							break;
						}
					}
				}
				if (isARuleSettled) {
					j = 0; // j will be 1 in the first column of the for
					isARuleSettled = false;
				}
			}
		}
		System.out.println("\nAfter Unit Rule");
		printGrammar(grammar);
		// Change terminals to non-terminals (X->a)
		ArrayList<String> terminals = new ArrayList<String>();
		for (int i = 0; i < grammar.size(); i++) { // Traversal of all grammer's rows
			ArrayList<String> row = grammar.get(i);// elements of each row
			for (int j = 1; j < row.size(); j++) {
				String element = row.get(j);
				if (element.length() >= 2) {// lowercases will remain same
					for (int k = 0; k < element.length(); k++) {
						String terminal = Character.toString(element.charAt(k));
						if (!nonTerminals.contains(terminal)) {
							// the char is lowercase because of non-terminals does not contain it
							element = element.replace(terminal.charAt(0),
									upperCaseAssigner(nonTerminals, grammar, terminals, terminal));
							row.set(j, element); // puts the element
						}
					}
				}
			}
		}
		// Last step (change elements to 2 size format) EAT => GT
		for (int i = 0; i < grammar.size(); i++) { // Traversal of all grammer's rows
			ArrayList<String> row = grammar.get(i);// elements of each row
			// empty bulma yeri
			for (int j = 1; j < row.size(); j++) {
				String element = row.get(j);
				if (element.length() >= 3) {// more than 2 size elements will be changed
					String pieceOfElement = "";// EAT => EA is a piece of an element
					int elementLength = element.length();
					for (int k = 0; k < elementLength - 2; k++) { // change elements to 2 size format
						pieceOfElement = Character.toString(element.charAt(0)) + Character.toString(element.charAt(1));
						pieceOfElement = Character
								.toString(upperCaseAssigner(nonTerminals, grammar, terminals, pieceOfElement));
						element = element.substring(2);
						pieceOfElement = pieceOfElement.concat(element);
						element = pieceOfElement;
						row.set(j, pieceOfElement);
					}
				}
			}
		}
		System.out.println("\nAfter The Last Step Converting To Chomsky Normal Form");
		printGrammar(grammar);
		System.out.println("\nWELCOME!");
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Write 'exit' to terminate the program.\nEnter a string that will be checked: ");
			String input = sc.nextLine();
			if (input.equals("exit"))
				break;
			if (CYK(grammar, input)) {
				System.out.println("Given input is VALID in this Context Free Grammar.\n");
			} else {
				System.out.println("Given input is INVALID in this Context Free Grammar.\n");
			}
		}
	}

	private static void printGrammar(ArrayList<ArrayList<String>> grammar) {
		for (ArrayList<String> strings : grammar) {
			for (int i = 0; i < strings.size(); i++) {
				if (i == 0) {
					System.out.print(strings.get(i) + ">");
				} else if (i != strings.size() - 1) {
					System.out.print(strings.get(i) + "|");
				} else {
					System.out.print(strings.get(i));
				}
			}
			System.out.println();
		}
	}

	private static boolean CYK(ArrayList<ArrayList<String>> grammar, String input) {
		int n = input.length();
		ArrayList<String>[][] table = new ArrayList[n][n]; // CYK table
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				table[i][j] = new ArrayList<>(); // filling each table element
			}
		}

		// Filling the first row according to CNF
		for (int i = 0; i < n; i++) {
			String temp = Character.toString(input.charAt(i));
			for (int j = 0; j < grammar.size(); j++) { // Traversal of all grammer's rows
				ArrayList<String> row = grammar.get(j); // elements of each row
				if (row.contains(temp)) {
					table[0][i].add(row.get(0));
				}
			}
		}
		// Filling the rest of the rows according to CNF
		for (int row = 1; row < n; row++) {
			for (int column = 0; column < n - row; column++) {
				for (int line = 0; line < row; line++) {
					ArrayList<String> down = table[line][column];
					ArrayList<String> cross = table[row - line - 1][column + line + 1];
					for (String first : down) {
						for (String second : cross) {
							String production = first + second;
							for (int j = 0; j < grammar.size(); j++) { // Traversal of all grammer's rows
								ArrayList<String> newRow = grammar.get(j); // elements of each row
								if (newRow.contains(production)) {
									table[row][column].add(newRow.get(0));
								}
							}
						}
					}
				}
			}
		}
		// if the last element of the table contains start varable, input is VALID
		boolean result = table[n - 1][0].contains(startVariable);
		return result;
	}

	private static char upperCaseAssigner(ArrayList<String> nonTerminals, ArrayList<ArrayList<String>> grammar,
			ArrayList<String> listOfChanged, String changed) {
		char character = (char) 0; //null
		if (listOfChanged.contains(changed)) { // puts existing uppercases
			for (int i = 0; i < grammar.size(); i++) { // Traversal of all grammer's rows
				ArrayList<String> row = grammar.get(i); // elements of each row
				if (row.size() == 2) { //terminals will be represented as nonterminals (X->b) row length = 2
					if (row.get(1).equals(changed)) {
						character = row.get(0).charAt(0); //takes uppercases
					}
				}
			}
		} else { //prevent (A->b and C->b)  repetition of upperCases 
			for (int i = 65; i <= 90; i++) { //ASCII A TO Z
				char upperCase = (char) i;// determine new uppercase ex: A
				// non-terminal does not contain the new uppercase
				if (!nonTerminals.contains(Character.toString(upperCase))) {
					nonTerminals.add(Character.toString(upperCase));
					ArrayList<String> nonTerminalToATerminal = new ArrayList<String>() {
						{
							add(Character.toString(upperCase)); // "A">
							add(changed); // "+"
						}
					}; // A -> +
					grammar.add(nonTerminalToATerminal);// add new rule to the end of the grammar
					character = upperCase;
					break;
				}
			}
			listOfChanged.add(changed);// add changed terminal to the terminals array
		}
		return character;
	}
}