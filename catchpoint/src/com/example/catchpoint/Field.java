package com.example.catchpoint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

public class Field extends View implements SensorEventListener {
	Context context;
	Canvas canvas;
	Paint color;
	Sensor sensor;
	float x = 50, y = 50, size = 2;
	long lastSensorUpdate = 0;
	private static final float DEADZONE = 0.25F;
	private static final int SPEED_DIVISOR = 4;
	private float canvasRatio;

	public Field(Context context) {
		super(context);
		this.context = context;

		SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
		color = new Paint();
		color.setColor(Color.GREEN);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.canvas = canvas;
		canvasRatio = canvas.getWidth()/canvas.getHeight();
		canvas.drawCircle(percentWidth(x), percentHeight(y), percentWidth(size), color);
	}

	private int percentWidth(float percent) {
		return Math.round(canvas.getWidth() / 100F * percent);
	}

	private int percentHeight(float percent) {
		return Math.round(canvas.getHeight() / 100F * percent);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		long curTime = System.currentTimeMillis();
		if(curTime - lastSensorUpdate > 50) {
			float eventX = event.values[0];
			float eventY = event.values[1];

			if(Math.abs(eventY) > DEADZONE || Math.abs(eventX) > DEADZONE) {
				if(eventX < -DEADZONE){
					eventX = Math.abs(eventX)/SPEED_DIVISOR;
					x = (x + eventX) % 100;
					color.setColor(Color.GREEN);
				}
				else if(eventX > DEADZONE){
					eventX = Math.abs(eventX)/SPEED_DIVISOR;
					x = (x - eventX) < 0 ? (x - eventX + 100) : (x - eventX);
					color.setColor(Color.BLUE);
				}
				if(eventY > DEADZONE){
					eventY = Math.abs(eventY)/SPEED_DIVISOR*canvasRatio;
					y = (y + eventY) % 100;
					color.setColor(Color.MAGENTA);
				}
				else if(eventY < -DEADZONE){
					eventY = Math.abs(eventY)/SPEED_DIVISOR*canvasRatio;
					y = (y - eventY) < 0 ? (y - eventY + 100) : (y - eventY);
					color.setColor(Color.RED);
				}
				invalidate();
			}
		}
	}
}
