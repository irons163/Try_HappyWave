package com.example.try_happywave;

import com.example.try_gameengine.framework.Sprite;

public class Tool extends Sprite{
	public int type;
	
	public Tool(float x, float y, boolean autoAdd) {
		super(x, y, autoAdd);
		// TODO Auto-generated constructor stub
	}

	public boolean isNeedRemoveInstance(){
	    boolean isNeedRemoveInstance = false;
	    if(this.getY() < 0){
	        isNeedRemoveInstance = true;
	    }
	    return isNeedRemoveInstance;
	}
}
