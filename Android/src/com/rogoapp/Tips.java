package com.rogoapp;

public class Tips {
	
	private int tip_id;
	private String tip;
	
	public Tips(){}
	public Tips(int tip_id, String tip){
		this.tip_id = tip_id;
		this.tip = tip;
	}
	
	public String tip(){
		return tip;
	}
	public void setTip(String tip){
		this.tip = tip;
	}
	
	public int tipId(){
		return tip_id;
	}
	public void setTipId(int tip_id){
		this.tip_id = tip_id;
	}

}
