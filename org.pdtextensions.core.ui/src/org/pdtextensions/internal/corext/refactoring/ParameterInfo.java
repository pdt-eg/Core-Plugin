package org.pdtextensions.internal.corext.refactoring;

public class ParameterInfo {

	String fOldParameterName;
	String fOldParameterType;
	String fOldParameterDefaultValue;
	
	String fNewParameterName;
	String fNewParameterType;
	String fNewParameterDefaultValue;
	
	public ParameterInfo(String paramName) 
	{
		fOldParameterName = fNewParameterName = paramName;
		fOldParameterType = fNewParameterType = "";
		fOldParameterDefaultValue = fNewParameterDefaultValue = "";
	}

	public String getParameterName() {
		return fNewParameterName;
	}

	public void setParameterName(String newParameterName) {
		this.fNewParameterName = newParameterName;
	}

	public String getParameterType() {
		return fNewParameterType;
	}

	public void setParameterType(String newParameterType) {
		this.fNewParameterType = newParameterType;
	}

	public String getParameterDefaultValue() {
		return fNewParameterDefaultValue;
	}

	public void setParameterDefaultValue(String newParameterDefaultValue) {
		this.fNewParameterDefaultValue = newParameterDefaultValue;
	}
	
	public boolean isRenamed()
	{
		return fNewParameterName != fOldParameterName;
	}
	
	public boolean hasTypeChanged()
	{
		return fNewParameterType != fOldParameterType;
	}
	
	public boolean hasDefaultValueChanged()
	{
		return fNewParameterDefaultValue != fOldParameterDefaultValue;
	}
	
	public String getOldParameterName()
	{
		return fOldParameterName;
	}
	
	public boolean isEqual(String variableName) {
		return fNewParameterName.equals(variableName);
	}
	
	
}