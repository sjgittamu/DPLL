package trigger;

import dpll.DPLLSat;

public class TriggerDPLL {
	public static void main(String[] args) {
		String file = args[0];
		String input = args[1];
		DPLLSat dpllSat = new DPLLSat(); 
		dpllSat.DPLLSatisfiable(input, file, input.length() >0 ? true: false, Boolean.valueOf(args[2]), Boolean.valueOf(args[3]));
		System.out.println();
	}

}
