import java.util.Scanner;
import java.util.ArrayList;
import java.nio.*;
import java.io.*;
public class MinimizeDFA {
	public static void main(String[] args) {
		while(true){
			clearScreen();
			System.out.println(" *----------------------------------------------------------------------------*");
			System.out.println(" |                                DFA MINIMIZER                               |");
			System.out.println(" *----------------------------------------------------------------------------*");
			System.out.println();
			System.out.println();
			System.out.println("Enter the name of the input file");
			Scanner scan=new Scanner(System.in);
			String fName=scan.nextLine();
			DFA automata=new DFA();
			String[] tokens,stokens;
			try(BufferedReader reader=new BufferedReader(new FileReader(fName))) {
				String line = null;
				while((line=reader.readLine())!=null) {
					line=line.replaceAll("\\s+"," ");
					stokens=line.replaceFirst("^ ","").split(" ");
					
					if(stokens[0].equals("Q")) {
						line=reader.readLine();
						line=line.replaceAll("\\s+"," ");
						tokens=line.replaceFirst("^ ","").split(" ");
						automata.initState(tokens);
					}
					else if (stokens[0].equals("S")) {
						line=reader.readLine();
						line=line.replaceAll("\\s+"," ");
						tokens=line.replaceFirst("^ ","").split(" ");
						automata.initAlphabet(tokens);
					}
					else if(stokens[0].equals("qo")) {
						//automata.setInitial(stokens[1]);
						line=reader.readLine();
						line=line.replaceAll("\\s+"," ");
						tokens=line.replaceFirst("^ ","").split(" ");
						automata.setInitial(tokens[0]);
						//System.out.println(automata.getInitial());
					}
					else if(stokens[0].equals("F")) {
						line=reader.readLine();
						line=line.replaceAll("\\s+"," ");
						tokens=line.replaceFirst("^ ","").split(" ");
						automata.setFinal(tokens);
					}
					else if(stokens[0].equals("T")) {
						automata.createTransTable();
						for(int i=0;i<automata.getNoStates();i++) {
							line=reader.readLine();
							line=line.replaceAll("\\s+"," ");
							tokens=line.replaceFirst("^ ","").split(" ");
							automata.setRowTT(tokens);
						}
						
					}
				}
				
			}
			catch(IOException x) {
				System.err.format("IOException: "+x.getMessage());
				System.out.println("\n\nPress enter to continue");
				scan.nextLine();
				continue;
			}
			System.out.println("The entered DFA is");
			automata.display();
			
			System.out.println("\n\nEnter 1 to display intermediate steps, else enter any other number");
			boolean debug=false;
			int inStep=scan.nextInt();
			if(inStep==1)
				debug=true;
			DFAMinimizer minimizer = new DFAMinimizer(automata,debug);
			System.out.println();
			System.out.println();
			System.out.println();
			DFA min=minimizer.minimize(automata);
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("The minimized DFA formed is ");
			System.out.println(" ------------------------------------------------------------------------------");
			min.display();
			PrintStream stdout=System.out;
			try {
			System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("min"+fName)), true));
			min.display();
			}
			catch(FileNotFoundException e) {
				System.out.println("Can not create a file");
			}
			System.setOut(stdout);
			System.out.println("Enter 1 to exit or 2 to input another DFA");
			int again=scan.nextInt();
			if(again==1)
				break;
		} 
	}
	public static void clearScreen() {
		for(int i=0;i<100;i++) {
			System.out.println();
		}
	}
}

class DFA
{
	private String[] state;
	private String[] alphabet;
	private int[][] transTable;
	private int qo;
	private int[] finalState;;
	public DFA(){}
	
	public void initState(String[] states) {
		state=new String[states.length];
		for(int i=0;i<states.length;i++) {
			state[i]=new String(states[i]);
		}
	}
	
	public void initAlphabet(String[] alpha) {
		alphabet=new String[alpha.length];
		for(int i=0;i<alpha.length;i++) {
			alphabet[i]=new String(alpha[i]);
		}
	}
	
	public int getIndexOf(String st) {
		for(int i=0;i<state.length;i++) {
			if(st.equals(state[i]))
				return i;
		}
		return -1;
	}
	
	public void setInitial(String st) {
		qo = this.getIndexOf(st);
	}
	
	public String getInitial() {
		return state[qo];
	}
	public void setFinal(String[] st) {
		finalState=new int[st.length];
		for(int i=0;i<st.length;i++) {
			finalState[i]=getIndexOf(st[i]);
		}
	} 
	
	public int getNoStates() {
		return state.length;
	}
	
	public int getAlphabetSize() {
		return alphabet.length;
	}
	
	public int getFinalSize() {
		return finalState.length;
	}
	
	public String getFinal(int index) {
		return state[finalState[index]];
	}
	public boolean checkFinal(String st) {
		for(int i=0;i<finalState.length;i++) {
			if(this.getIndexOf(st)==finalState[i])
				return true;
		}
		return false;
	}
	public String getState(int index) {
		return state[index];
	}
	public String getSymbol(int index) {
		return alphabet[index];
	}
	public String[] getAlphabet() {
		String[] alpha=new String[alphabet.length];
		System.arraycopy(alphabet,0,alpha,0,alphabet.length);
		return alpha;
	}
	public String[] getStates() {
		String[] st=new String[state.length];
		System.arraycopy(state,0,st,0,state.length);
		return st;
	}
	public void createTransTable() {
		transTable=new int[state.length][alphabet.length];
	}
	public void setRowTT(String st[]) {
		int index=this.getIndexOf(st[0]);
		for(int i=1;i<st.length;i++) {
			transTable[index][i-1]=this.getIndexOf(st[i]);
		}
	}
	public int getSymbolIndex(String alpha) {
		for(int i=0;i<alphabet.length;i++) {
			if(alpha.equals(alphabet[i]))
				return i;
		}
		return -1;
	}
	public int[][] getTransTable() {
		int[][] ttCopy;
		ttCopy=new int[transTable.length][transTable[0].length];
		for(int i=0;i<transTable.length;i++) {
			System.arraycopy(transTable[i],0,ttCopy[i],0,transTable[i].length);
		}
		return ttCopy;
	}
	public String getTransition(String from, String symbol) {
		int fromIndex=this.getIndexOf(from);
		int symbolIndex=this.getSymbolIndex(symbol);
		return state[transTable[fromIndex][symbolIndex]];
	}
	
	public void display() {
		//System.out.println("The following is the five tuple for the DFA");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("Q");
		for(int i=0;i<state.length;i++) {
			System.out.print(state[i]+"   ");
		}
		System.out.println();
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println();
		System.out.println("S");
		for(int i=0;i<alphabet.length;i++) {
			System.out.print(alphabet[i]+"   ");
		}
		System.out.println();
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println();
		System.out.println("qo");
		System.out.println(this.getInitial());
		System.out.println();
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println();
		System.out.println("F");
		for(int i=0;i<finalState.length;i++) {
			System.out.print(this.getFinal(i)+"   ");
		}
		System.out.println();
		System.out.println("-------------------------------------------------------------------------------");
		System.out.println();
		int largestLen=0;
		for(int i=0;i<state.length;i++) {
			if(state[i].length()>largestLen)
				largestLen=state[i].length();
		}
		largestLen=largestLen*-1;
		System.out.printf("%"+largestLen+"s ","T");
		for(int i=0;i<alphabet.length;i++) {
			System.out.printf("%"+largestLen+"s ",alphabet[i]);
		}
		System.out.println();
		for(int i=0;i<transTable.length;i++) {
			System.out.printf("%"+largestLen+"s ",state[i]);
			for(int j=0;j<transTable[i].length;j++) {
				System.out.printf("%"+largestLen+"s ",state[transTable[i][j]]);
			}
			System.out.println();
		}
	}
}

class DFAMinimizer {
	private String[][] table;
	boolean intStep;
	public DFAMinimizer(DFA automaton,boolean in) {
		table=new String[automaton.getNoStates()-1][];
		for(int i=0;i<automaton.getNoStates()-1;i++) {
			table[i]=new String[i+1];
			for(int k=0;k<i+1;k++) {
				table[i][k]=new String("E");
			}
		}
		intStep=in;
	}
	
	public DFA minimize(DFA automaton) {
	    // mark each final and non final state, first mark the row corresponding to the final state
		if(intStep)
			System.out.println("Marking pairs of final and non final states as distinguishable");
		for(int i=0;i<automaton.getFinalSize();i++) {   
			int row=automaton.getIndexOf(automaton.getFinal(i))-1;
			for(int col=0;col<row+1;col++) {
			//System.out.println("Checking state "+automaton.getState(col));
				if(!automaton.checkFinal(automaton.getState(col))) {
					table[row][col]=new String("X");
				}
			}
			// then mark the columns corresponding to the final state
			int col=automaton.getIndexOf(automaton.getFinal(i));
			for(row=col;row<table.length;row++) {
			//System.out.println("Checking state "+automaton.getState(row+1));
				if(!automaton.checkFinal(automaton.getState(row+1))) {
				
					table[row][col]=new String("X");
				}
			}
		}
		//Start checking for states which are not in F or which are both in F
		if(intStep) {
			this.displayTable(automaton);
			System.out.println("Now comparing the remaining pairs");
		}
		for(int i=0;i<table.length;i++) {
			for(int j=0;j<table[i].length;j++) {
			    // Process each non marked box
				if(!table[i][j].equals("X")) {
					int flag=0;
					if(intStep)
						System.out.println("--------------------------------------------------------------------------------");
					for(int k=0;k<automaton.getAlphabetSize();k++) {
					    // Determine the transition of the both states (i+1 and j) on the input symbol k
						int trans1=automaton.getIndexOf(automaton.getTransition(automaton.getState(i+1),automaton.getSymbol(k)));
						int trans2=automaton.getIndexOf(automaton.getTransition(automaton.getState(j),automaton.getSymbol(k)));
						if(intStep) {
							
							System.out.println("("+automaton.getState(i+1)+","+automaton.getState(j)+") goes to ("+automaton.getState(trans1)+","+automaton.getState(trans2)+") on symbol "+automaton.getSymbol(k));
						}
						// if the output states are distinguishable, mark both the states as distinguishable. Also mark all the states as distinguishable which are associated with this state
						if(((trans1<trans2)&&(table[trans2-1][trans1].equals("X")))||((trans2<trans1)&&(table[trans1-1][trans2].equals("X")))) {
							String[] token=table[i][j].split(" ");
							if(intStep)
								System.out.println("Output states are distinguishable, Marking ("+automaton.getState(i+1)+","+automaton.getState(j)+") as distinguishable");
							table[i][j]=new String("X");
							for(int p=1;p<token.length;p++) {
								String[] coords=token[p].split(",");
								if(intStep)
									System.out.println("Also marking states "+automaton.getState(Integer.parseInt(coords[0])+1)+" and "+automaton.getState(Integer.parseInt(coords[1]))+" as distinguishable");
								table[Integer.parseInt(coords[0])][Integer.parseInt(coords[1])]=new String("X");
							}
						}
						// else add the list associated with the current pair and the current pair itself to the list of output pair
						else if (trans1<trans2){
							String[] token=table[i][j].split(" ");
							table[trans2-1][trans1]=table[trans2-1][trans1]+" "+String.valueOf(i)+","+String.valueOf(j);
							if(intStep)
								System.out.println("The state pair ("+automaton.getState(i+1)+","+automaton.getState(j)+") depends on the state pair ("+automaton.getState(trans2)+","+automaton.getState(trans1)+")");
							//System.out.println("Added coords "+String.valueOf(i)+" and "+String.valueOf(j)+" at index "+(trans2-1)+" and "+trans1);
							for(int p=1;p<token.length;p++) {
								table[trans2-1][trans1]=table[trans2-1][trans1]+" "+token[p];
								if(intStep)
									System.out.println("Also added "+token[p]);
							}
						}
						else if (trans1>trans2) {
							String[] token=table[i][j].split(" ");
							table[trans1-1][trans2]=table[trans1-1][trans2]+" "+String.valueOf(i)+","+String.valueOf(j);
							if(intStep)
								System.out.println("The state pair ("+automaton.getState(i+1)+","+automaton.getState(j)+") depends on the state pair ("+automaton.getState(trans1)+","+automaton.getState(trans2)+")");
						//	System.out.println("Added coords "+String.valueOf(i)+" and "+String.valueOf(j)+" at index "+(trans1-1)+" and "+trans2);
							for(int p=1;p<token.length;p++) {
								table[trans1-1][trans2]=table[trans1-1][trans2]+" "+token[p];
								if(intStep)
									System.out.println("Also added "+token[p]);
							}
						}
					}
				}
			}
		}
		//Mark with O, all those states which don't have an X associated with them.
		if(intStep) {
			System.out.println();
			System.out.println("--------------------------------------------------------------------------------");
			System.out.println("Marking O those states which are not marked X");
		}
		for(int i=0;i<table.length;i++) {
			for (int j=0;j<table[i].length;j++) {
				if(!table[i][j].equals("X"))
					table[i][j]=new String("O");
			}
		}
		this.displayTable(automaton);
		// Enter the sum of index of second state and automaton.number of states to the state with higher index in the transition table just to act as a pointer to the first stating that the second state is equivalent to the first one
		int[][] tTable=automaton.getTransTable();
		for(int i=0;i<table.length;i++) {
			for(int j=0;j<table[i].length;j++) {
				if(table[i][j].equals("O")) {
					for(int k=0;k<tTable[i+1].length;k++) {
						tTable[i+1][k]=automaton.getNoStates()+j;
					}
				}
			}
		}
		// Print the new indexes
		/*for(int k=0;k<tTable.length;k++) {
		System.out.println();
			for(int l=0;l<tTable[k].length;l++) {
				System.out.print(tTable[k][l]+" ");
			}
		}*/
		
		DFA mautomaton=new DFA();
		//Initialize the alphabet of the minimized automaton
		mautomaton.initAlphabet(automaton.getAlphabet());
		String[] st=new String[automaton.getNoStates()];
		st=automaton.getStates();
		int rmvState=0;//counter for counting the number of states to be removed
		// loop to remove the states and make corresponding pairs
		for(int i=st.length-1;i>=0;i--) {
			if((tTable[i][0]-automaton.getNoStates())>=0) {
				st[tTable[i][0]-automaton.getNoStates()]=st[tTable[i][0]-automaton.getNoStates()]+","+st[i];
				st[i]="-";
				rmvState++;
			}
		}
		//Create an array of the new states
		String[] mst = new String[automaton.getNoStates()-rmvState];
		for(int i=0,k=0;i<st.length;i++) {
			if(!st[i].equals("-")) {
				mst[k]=st[i];
				k++;
			}
		}
		/*System.out.println("The new States are ");
		for (int i=0;i<mst.length;i++) {
			System.out.println(mst[i]);
		}*/
		//Initialize the states of the minimized automaton
		mautomaton.initState(mst);
		int mqo=0;//=automaton.getIndexOf(automaton.getInitial());
		/*while((tTable[mqo][0]-automaton.getNoStates())>=0)
			mqo=tTable[mqo][0]-automaton.getNoStates();*/
		//Loop to determine the initial state of the minimized automaton
		for(int i=0;i<mautomaton.getNoStates();i++) {
			int flag=0;
			String[] stt=mautomaton.getState(i).split(",");
			for(int j=0;j<stt.length;j++) {
				if(automaton.getInitial().equals(stt[j])) {
					mqo=i;
					flag=1;
					break;
				}
			}
			if(flag==1)
				break;
		}
		//Initialize the initial state of the minimized automaton
		mautomaton.setInitial(mautomaton.getState(mqo));
		//System.out.println();
		//System.out.println("The new initial state is "+mautomaton.getState(mqo));
		//Loop to determine the set of final states of the new automaton
		ArrayList<String> finalS=new ArrayList<String>();
		for(int i=0;i<automaton.getFinalSize();i++) {
			for(int j=0;j<mautomaton.getNoStates();j++) {
				int flag=0;
				String[] stt=mautomaton.getState(j).split(",");
				for(int k=0;k<stt.length;k++) {
					if(automaton.getFinal(i).equals(stt[k])) {
						finalS.add(mautomaton.getState(j));
						flag=1;
						break;
					}
				}
				if(flag==1)
					break;
			}
		}
		String[] fState=finalS.toArray(new String[finalS.size()]);
		//Initialize the final states of the new automaton
		mautomaton.setFinal(fState);
		/*System.out.println("The final states are ");
		for(int i=0;i<fState.length;i++) {
			System.out.println(fState[i]);
		}*/
		//Create the new Transition table
		mautomaton.createTransTable();
		for(int i=0,m=0;i<tTable.length;i++) {
			String[] stt=new String[tTable[0].length+1];
			if(tTable[i][0]-automaton.getNoStates()<0) {
				stt[0]=mautomaton.getState(m);
				//System.out.println("State "+m+" is "+mautomaton.getState(m));
				m++;
				for(int j=0;j<tTable[i].length;j++) {
					String st2=automaton.getState(tTable[i][j]);
					//System.out.println("The state to find is "+st2);
					for(int k=0;k<mautomaton.getNoStates();k++) {
						int flag=0;
						//System.out.println("SPlitting "+mautomaton.getState(k));
						String[] stt2=mautomaton.getState(k).split(",");
						for(int l=0;l<stt2.length;l++) {
							//System.out.println("Comparing with "+stt2[l]);
							if(stt2[l].equals(st2)) {
								stt[j+1]=mautomaton.getState(k);
								//System.out.println("The State found is "+mautomaton.getState(k));
								flag=1;
								break;
							}
						}
						if(flag==1)
							break;
					}
				}
				/*for(int k=0;k<stt.length;k++)
					System.out.println(stt[k]);*/
				mautomaton.setRowTT(stt);
			}
		}
		System.out.println();
		return mautomaton;
	}
	
	public void displayTable(DFA automaton) {
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println();
		System.out.println("The table formed is ");
		System.out.println();
		int largestLen=0;
		for(int i=0;i<automaton.getNoStates();i++) {
			if(automaton.getState(i).length()>largestLen)
				largestLen=automaton.getState(i).length();
		}
		largestLen=-1*largestLen;
			for(int i=0;i<table.length;i++) {
			System.out.printf("%"+largestLen+"s ",automaton.getState(i+1));
				for(int j=0;j<table[i].length;j++) {
					System.out.printf("%"+largestLen+"s ",table[i][j]);
				}
				System.out.println();
			}
			System.out.printf("%"+largestLen+"s "," ");
			for(int j=0;j<automaton.getNoStates()-1;j++) {
				System.out.printf("%"+largestLen+"s ",automaton.getState(j));
			}
			System.out.println();
			System.out.println("--------------------------------------------------------------------------------");
	}
}
