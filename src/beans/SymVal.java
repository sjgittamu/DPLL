package beans;

public class SymVal {
	String symbol;
	Boolean value;
	public SymVal(String symbol, Boolean value) {
		super();
		this.symbol = symbol;
		this.value = value;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Boolean getValue() {
		return value;
	}
	public void setValue(Boolean value) {
		this.value = value;
	}
	
}
