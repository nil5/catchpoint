package com.example.catchpoint;

import android.graphics.Paint;

public class Point {
	private float x, y, radius;
	private Paint color;
	private int userID;
	
	public Point(int userID, float x, float y, float radius, int color){
		this.userID = userID;
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = new Paint();
		this.color.setColor(color);
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public void setX(float x){
		this.x = x;
	}
	
	public void setY(float y){
		this.y = y;
	}
	
	public float getRadius(){
		return radius;
	}
	
	public void setRadius(float radius){
		this.radius = radius;
	}
	
	public Paint getColor(){
		return color;
	}
	
	public void setColor(int color){
		this.color.setColor(color);
	}
}
