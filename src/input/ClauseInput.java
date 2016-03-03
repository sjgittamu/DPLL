package input;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ClauseInput {

	Set<String> propostitions = new HashSet<String>();

	public Set<String> getPropostitions() {
		return propostitions;
	}

	public void setPropostitions(Set<String> propostitions) {
		this.propostitions = propostitions;
	}

	public Set<String> getClausesFromFile(String file, String input){
		Set<String> props = new HashSet<String>();
		Set<String> clauses = new HashSet<String>();

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				clauses.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String[] inputStrings = input.split(" ");
		for (String ip : inputStrings) {
			clauses.add(ip);
		}
		for (String clause : clauses) {
			String[] thisLineProps = clause.split(" ");
			for (String string : thisLineProps) {
				String a = string.replace("-","");
				props.add(a);
			}
		}

		setPropostitions(props); 
		return clauses;
	}
}
