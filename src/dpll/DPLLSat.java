package dpll;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import beans.SymVal;
import input.ClauseInput;

public class DPLLSat {
	static int counter =0;
	boolean unitClause = true;
	boolean pureSymbol = true;
	boolean agents = true;
	public boolean DPLLSatisfiable(String input, String file, boolean agents, boolean unitCl, boolean pureSym){		
		ClauseInput clauseIn = new ClauseInput();
		Set<String> clauses = clauseIn.getClausesFromFile(file, input);
		Set<String> symbols = clauseIn.getPropostitions();
		this.unitClause = unitCl;
		this.pureSymbol = pureSym;
		this.agents = agents;
		Map<String, Boolean> model = new HashMap<String, Boolean>();	//mapping of symbol vs True/False
		return DPLL(clauses, symbols, model, 0, null);
	}
	public boolean DPLL(Set<String> clauses, Set<String> symbols, Map<String, Boolean> model, int trueCall, String nextSymbol) {
		Map<String, Boolean> workingModel = new HashMap<String, Boolean>(model);
		Set<String> workingSymbols = new HashSet<String>(symbols);
		if(trueCall == 1){
			workingModel.put(nextSymbol, true);
			model.put(nextSymbol, true);
			System.out.println("Trying "+nextSymbol +"="+true);
		}
		else if(trueCall == 2){
			workingModel.put(nextSymbol, false);
			model.put(nextSymbol, false);
			System.out.println("Trying "+nextSymbol +"="+false);
		}
		counter++;
		System.out.println("model= "+model);
		
		if(isEveryClauseTrue(clauses, workingSymbols, workingModel) && workingModel.keySet().containsAll(workingSymbols)){
			Set<String> trueProps = new HashSet<String>();
			for (Map.Entry<String, Boolean> entry : model.entrySet()) {
				if(entry.getValue() == true && entry.getKey().length() ==1){
					trueProps.add(entry.getKey());
				}
			}
			System.out.println("Nodes searched = "+counter);
			System.out.println("Solution:");
			for (Map.Entry<String, Boolean> entry : model.entrySet()) {
				System.out.println(entry.getKey() +"="+entry.getValue());
			}
			System.out.println("True Props:");
			for (String string : trueProps) {
				System.out.println(string);
			}
			return true;
		}
		//if some clause in clauses is false in model then return false
		if(someClausesFalse(clauses, workingSymbols, workingModel)){
			System.out.println("BackTracking, false clauses encountered");
			return false;
		}
		SymVal pureSym = findPureSymbol(clauses, workingSymbols, workingModel);
		if(pureSym != null && pureSym.getSymbol() != null ){
			System.out.println("Pure Symbol "+pureSym.getSymbol()+" = "+pureSym.getValue());
			workingModel.put(pureSym.getSymbol(), pureSym.getValue());
			workingSymbols.remove(pureSym.getSymbol());
			return DPLL(clauses, workingSymbols, workingModel, 0, null);
		}
		SymVal unitClause = findUnitClause(clauses, workingModel);
		if(unitClause != null && unitClause.getSymbol() != null){
			System.out.println("Unit clause implies "+unitClause.getSymbol()+" = "+unitClause.getValue());
			workingModel.put(unitClause.getSymbol(), unitClause.getValue());
			workingSymbols.remove(unitClause.getSymbol());
			return DPLL(clauses, workingSymbols, workingModel, 0, null);
		}
		Iterator<String> iter =workingSymbols.iterator();
		nextSymbol = null;
		Set<String> rest = new HashSet<String>();
		if(iter.hasNext()){
			nextSymbol = iter.next();
		}
		for (String string : workingSymbols) {
			if(!string.equals(nextSymbol)) {
				rest.add(string);
			}
		}
		
		return DPLL(clauses, rest, workingModel, 1, nextSymbol) || DPLL(clauses, rest, workingModel, 2, nextSymbol);
	}
	private static boolean someClausesFalse(Set<String> clauses, Set<String> symbols, Map<String, Boolean> model) {
		for (String clause : clauses) {
			if(isClauseFalse(clause, model)){
				return true;
			}
		}
		return false;
	}

	private static boolean isClauseFalse(String clause, Map<String, Boolean> model) {	//TODO check for false.. which is not same as not true
		//so have to check if one of the things in clause is definitely false, and none of the others are true both satisfy at the same time.
		//if we break on getting one false, there might be a true later
		//we can defintely break to not being false when we encounter true
		String[] thisLineSymbols = clause.split(" ");
		boolean valueUnknown = false;
		boolean falseClause = true;
		for (String currSymbol : thisLineSymbols) {
			String positiveSymbol = currSymbol.replace("-","");
			Boolean value = model.get(positiveSymbol);
			if(value == null){
				valueUnknown = true;
			}else{ 
				falseClause = falseClause && ((!currSymbol.equals(positiveSymbol) && value == true) || (currSymbol.equals(positiveSymbol) && value == false));
			}
		}
		if(!valueUnknown && falseClause){
			return true;
		}
		return false;
	}
	private static boolean isEveryClauseTrue(Set<String> clauses, Set<String> symbols, Map<String, Boolean> model) {
		for (String clause : clauses) {
			if(!isClauseTrue(clause, model)){
				return false;
			}
		}
		return true;
	}
	private static boolean isClauseTrue(String clause, Map<String,Boolean> model) {
		String[] thisLineSymbols = clause.split(" ");
		for (String currSymbol : thisLineSymbols) {
			String positiveSymbol = currSymbol.replace("-","");
			Boolean value = model.get(positiveSymbol);
			if(currSymbol.equals(positiveSymbol) && value != null && value == true){	//poisitve symbol and false value
				return true;
			}if(!currSymbol.equals(positiveSymbol) && value != null && value == false){	//negative symbol and true value
				return true;
			}
		}
		return false;
	}
	private SymVal findUnitClause(Set<String> clauses, Map<String, Boolean> model) {
		if(!unitClause)
			return null;
		Set<String> resultClauses = applyModelToClauses(clauses, model);
		for (String clause : resultClauses) {
			int spaceCount =0;
			for(int i=0; i< clause.length(); i++){
				if(clause.charAt(i) == ' '){
					spaceCount++;
				}
			}
			if(spaceCount == 0 && clause.length() >0){	//=> unit clause
				boolean isNegative = false;
				if(clause.charAt(0) == '-'){
					isNegative = true;
					clause = clause.replace("-","");
				}
				return new SymVal(clause, !isNegative);
			}

		}
		return null;
	}
	private Set<String> applyModelToClauses(Set<String> clauses, Map<String, Boolean> model) {
		if(model.isEmpty()){
			return clauses;
		}
		Set<String> result = new HashSet<String>();
		boolean flag = false;
		for (String clause : clauses) {
			String[] thisClauseSymbols = clause.split(" ");
			String resultClause ="";
			flag = false;
			for (String currSymbol : thisClauseSymbols) {
				String positiveSymbol = currSymbol.replace("-","");
				Boolean value = model.get(positiveSymbol);
				if(value == null){
					if(resultClause.equals(""))
						resultClause = resultClause + currSymbol;
					else
						resultClause = resultClause +" "+ currSymbol;
				}else if(!currSymbol.equals(positiveSymbol) && value == true ){	//true in model and negative in clause
					continue;//dont add
				}else if(currSymbol.equals(positiveSymbol) && value == false ){	//true in model and negative in clause
					continue;//dont add
				}else if(currSymbol.equals(positiveSymbol) && value == true){
					flag = true;
					break;
				}else if(!currSymbol.equals(positiveSymbol) && value == false){
					flag = true;
					break;
				}

			}
			if(!agents){
				if(!resultClause.equals("") && !flag)
					result.add(resultClause);
			}else{
				if(!resultClause.equals(""))
					result.add(resultClause);
			}
		}

		return result;
	}
	private SymVal findPureSymbol(Set<String> clauses, Set<String> symbols, Map<String, Boolean> model) {
		if(!pureSymbol)
			return null;
		Set<String> resultClauses = applyModelForPurityToClauses(clauses, model);	//Change the way model is applied
		for (String symbol : symbols) {
			boolean isNegative = false;
			boolean isPositive = false;
			for (String clause : resultClauses) {
				if(clause.contains(symbol)){
					if(clause.contains("-"+symbol)){
						isNegative = isNegative || true;
					}else{
						isPositive = isPositive || true;
					}
				}
			}
			if(isNegative && isPositive){
				continue;
			}else if(isNegative || isPositive){
				if(isNegative)
					return new SymVal(symbol, false);
				else if(isPositive)
					return new SymVal(symbol, true);
			}
		}
		//loop through these clauses
		//remove the clauses which are already true
		//check the literals which are pure in remaining ones
		return null;
	}
	private Set<String> applyModelForPurityToClauses(Set<String> clauses, Map<String, Boolean> model) {

		if(model.isEmpty()){
			return clauses;
		}
		
		Set<String> result = new HashSet<String>();
		for (String clause : clauses) {
			String[] thisClauseSymbols = clause.split(" ");
			String resultClause ="";
			boolean isClauseTrue = false;
			for (String currSymbol : thisClauseSymbols) {
				String positiveSymbol = currSymbol.replace("-","");
				Boolean value = model.get(positiveSymbol);
				if(value == null){
					if(resultClause.equals(""))
						resultClause = resultClause + currSymbol;
					else
						resultClause = resultClause +" "+ currSymbol;
				}else if(!currSymbol.equals(positiveSymbol) && value == false ){	//true in model and negative in clause
					isClauseTrue = true;
					continue;//dont add
				}else if(currSymbol.equals(positiveSymbol) && value == true ){	//true in model and negative in clause
					isClauseTrue = true;
					continue;//dont add
				}

			}
			if(!resultClause.equals("") && !isClauseTrue)
				result.add(resultClause);
		}

		return result;
	
		
	}
}
