package com.example.catchpoint;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class FieldThread extends Thread {
	private boolean isRunning = false;
	private SurfaceHolder holder;
	private Canvas canvas;
	private Context context;
	private TestField field;

	public FieldThread(SurfaceHolder holder, Context context, TestField field) {
		this.holder = holder;
		this.context = context;
		this.field = field;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	@Override
	public void run() {
		super.run();
		while(isRunning) {
			canvas = holder.lockCanvas();
			if(canvas != null) {
				field.doDraw(canvas);
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

}
