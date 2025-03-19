package ch.eugster.filemaker.fsl.qrbill;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Debtor
{
	@JsonProperty("name")
	private String name;

	@JsonProperty("street")
	private String street;

	@JsonProperty("houseNo")
	private String houseNo;

	@JsonProperty("postalCode")
	private String postalCode;
	
	@JsonProperty("town")
	private String town;
	
	@JsonProperty("countryCode")
	private String countryCode;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public String getHouseNo()
	{
		return this.houseNo;
	}

	public void setHouseNo(String houseNo)
	{
		this.houseNo = houseNo;
	}

	public String getPostalCode()
	{
		return postalCode;
	}

	public void setPostalCode(String postalCode)
	{
		this.postalCode = postalCode;
	}

	public String getTown()
	{
		return town;
	}

	public void setTown(String town)
	{
		this.town = town;
	}

	public String getCountryCode()
	{
		return countryCode;
	}

	public void setCountryCode(String countryCode)
	{
		this.countryCode = countryCode;
	}
}
